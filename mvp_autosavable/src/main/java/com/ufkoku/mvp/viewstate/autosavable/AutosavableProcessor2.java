/*
 * Copyright 2017 Ufkoku (https://github.com/Ufkoku/AndroidMVPHelper)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ufkoku.mvp.viewstate.autosavable;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@SupportedAnnotationTypes({"com.ufkoku.mvp.viewstate.autosavable.AutoSavable"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class AutosavableProcessor2 extends AbstractProcessor {

    private static final String SAVER_SUFFIX = "Saver";

    private static final String CLASS_NAME_BUNDLE = "android.os.Bundle";
    private static final String CLASS_NAME_PARCELABLE = "android.os.Parcelable";

    private TypeElement bundleTypeElement;
    private TypeElement parcelableTypeElement;

    @Override
    @SuppressWarnings("unchecked")
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        bundleTypeElement = processingEnv.getElementUtils().getTypeElement(CLASS_NAME_BUNDLE);
        parcelableTypeElement = processingEnvironment.getElementUtils().getTypeElement(CLASS_NAME_PARCELABLE);
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(AutoSavable.class);
            if (elements.size() > 0) {
                for (Element element : elements) {
                    if (element.getKind() == ElementKind.CLASS) {
                        AutoSavable annotation = element.getAnnotation(AutoSavable.class);
                        TypeElement typeElement = (TypeElement) element;
                        createSaverClass(typeElement, createDeclaredType(typeElement), annotation.includeSuper());
                    }
                }
            }
            return true;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void createSaverClass(TypeElement typeElement, DeclaredType declaredType, boolean includeSuper) throws IOException {
        String name = typeElement.getSimpleName() + SAVER_SUFFIX;

        final String OUT_STATE = "outState";
        final String IN_STATE = "inState";
        final String STATE = "state";

        List<TypeVariableName> methodsTypeVariables = convertTypeParametersToTypeVariableNames(typeElement.getTypeParameters());

        MethodSpec.Builder saveSpecBuilder = MethodSpec.methodBuilder("save")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addTypeVariables(methodsTypeVariables)
                .addParameter(TypeName.get(typeElement.asType()), STATE)
                .addParameter(TypeName.get(bundleTypeElement.asType()), IN_STATE)
                .returns(void.class);

        MethodSpec.Builder restoreSpecBuilder = MethodSpec.methodBuilder("restore")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addTypeVariables(methodsTypeVariables)
                .addParameter(TypeName.get(typeElement.asType()), STATE)
                .addParameter(TypeName.get(bundleTypeElement.asType()), OUT_STATE)
                .returns(void.class);

        Map<FieldData, MethodsPair> fieldsData = getAllAcceptableFieldsData(typeElement, declaredType, includeSuper);
        if (fieldsData.size() > 0) {

            boolean allPublic = true;
            int gettersCount = 0;
            int settersCount = 0;

            for (Map.Entry<FieldData, MethodsPair> entry : fieldsData.entrySet()) {
                allPublic = allPublic && entry.getKey().variableElement.getModifiers().contains(Modifier.PUBLIC);

                if (entry.getValue().getterElement != null) {
                    gettersCount++;
                }

                if (entry.getValue().setterElement != null) {
                    settersCount++;
                }
            }

            if (!allPublic) {
                if (gettersCount != fieldsData.size()) {
                    saveSpecBuilder.addCode("try {\n");
                }

                if (settersCount != fieldsData.size()) {
                    restoreSpecBuilder.addCode("try {\n");
                }
            }

            for (Map.Entry<FieldData, MethodsPair> entry : fieldsData.entrySet()) {

                FieldData fieldData = entry.getKey();
                MethodsPair methodsPair = entry.getValue();

                Types typeUtils = processingEnv.getTypeUtils();
                Elements elementUtils = processingEnv.getElementUtils();

                if (fieldData.typeMirror instanceof PrimitiveType) {
                    if (typeUtils.isSameType(fieldData.typeMirror, typeUtils.getPrimitiveType(TypeKind.BOOLEAN))) {
                        addStatementsForField(saveSpecBuilder, restoreSpecBuilder, fieldData, methodsPair, STATE, IN_STATE, OUT_STATE, "putBoolean", "getBoolean", "setBoolean", "getBoolean", false);
                    } else if (typeUtils.isSameType(fieldData.typeMirror, typeUtils.getPrimitiveType(TypeKind.BYTE))) {
                        addStatementsForField(saveSpecBuilder, restoreSpecBuilder, fieldData, methodsPair, STATE, IN_STATE, OUT_STATE, "putByte", "getByte", "setByte", "getByte", false);
                    } else if (typeUtils.isSameType(fieldData.typeMirror, typeUtils.getPrimitiveType(TypeKind.CHAR))) {
                        addStatementsForField(saveSpecBuilder, restoreSpecBuilder, fieldData, methodsPair, STATE, IN_STATE, OUT_STATE, "putChar", "getChar", "setChar", "getChar", false);
                    } else if (typeUtils.isSameType(fieldData.typeMirror, typeUtils.getPrimitiveType(TypeKind.FLOAT))) {
                        addStatementsForField(saveSpecBuilder, restoreSpecBuilder, fieldData, methodsPair, STATE, IN_STATE, OUT_STATE, "putFloat", "getFloat", "setFloat", "getFloat", false);
                    } else if (typeUtils.isSameType(fieldData.typeMirror, typeUtils.getPrimitiveType(TypeKind.INT))) {
                        addStatementsForField(saveSpecBuilder, restoreSpecBuilder, fieldData, methodsPair, STATE, IN_STATE, OUT_STATE, "putInt", "getInt", "setInt", "getInt", false);
                    } else if (typeUtils.isSameType(fieldData.typeMirror, typeUtils.getPrimitiveType(TypeKind.SHORT))) {
                        addStatementsForField(saveSpecBuilder, restoreSpecBuilder, fieldData, methodsPair, STATE, IN_STATE, OUT_STATE, "putShort", "getShort", "setShort", "getShort", false);
                    } else if (typeUtils.isSameType(fieldData.typeMirror, typeUtils.getPrimitiveType(TypeKind.LONG))) {
                        addStatementsForField(saveSpecBuilder, restoreSpecBuilder, fieldData, methodsPair, STATE, IN_STATE, OUT_STATE, "putLong", "getLong", "setLong", "getLong", false);
                    } else if (typeUtils.isSameType(fieldData.typeMirror, typeUtils.getPrimitiveType(TypeKind.DOUBLE))) {
                        addStatementsForField(saveSpecBuilder, restoreSpecBuilder, fieldData, methodsPair, STATE, IN_STATE, OUT_STATE, "putDouble", "getDouble", "setDouble", "getDouble", false);
                    }
                } else {
                    if (typeUtils.isSameType(fieldData.typeMirror, bundleTypeElement.asType())) {
                        addStatementsForField(saveSpecBuilder, restoreSpecBuilder, fieldData, methodsPair, STATE, IN_STATE, OUT_STATE, "putBundle", "getBundle", "set", "get", false);
                    } else if (typeUtils.isSameType(fieldData.typeMirror, typeUtils.getArrayType(typeUtils.getPrimitiveType(TypeKind.BOOLEAN)))) {
                        addStatementsForField(saveSpecBuilder, restoreSpecBuilder, fieldData, methodsPair, STATE, IN_STATE, OUT_STATE, "putBooleanArray", "getBooleanArray", "set", "get", false);
                    } else if (typeUtils.isSameType(fieldData.typeMirror, typeUtils.getArrayType(typeUtils.getPrimitiveType(TypeKind.BYTE)))) {
                        addStatementsForField(saveSpecBuilder, restoreSpecBuilder, fieldData, methodsPair, STATE, IN_STATE, OUT_STATE, "putByteArray", "getByteArray", "set", "get", false);
                    } else if (typeUtils.isSameType(fieldData.typeMirror, typeUtils.getArrayType(typeUtils.getPrimitiveType(TypeKind.CHAR)))) {
                        addStatementsForField(saveSpecBuilder, restoreSpecBuilder, fieldData, methodsPair, STATE, IN_STATE, OUT_STATE, "putCharArray", "getCharArray", "set", "get", false);
                    } else if (typeUtils.isSameType(fieldData.typeMirror, typeUtils.getArrayType(typeUtils.getPrimitiveType(TypeKind.FLOAT)))) {
                        addStatementsForField(saveSpecBuilder, restoreSpecBuilder, fieldData, methodsPair, STATE, IN_STATE, OUT_STATE, "putFloatArray", "getFloatArray", "set", "get", false);
                    } else if (typeUtils.isSameType(fieldData.typeMirror, typeUtils.getArrayType(typeUtils.getPrimitiveType(TypeKind.INT)))) {
                        addStatementsForField(saveSpecBuilder, restoreSpecBuilder, fieldData, methodsPair, STATE, IN_STATE, OUT_STATE, "putIntArray", "getIntArray", "set", "get", false);
                    } else if (typeUtils.isSameType(fieldData.typeMirror, typeUtils.getArrayType(typeUtils.getPrimitiveType(TypeKind.SHORT)))) {
                        addStatementsForField(saveSpecBuilder, restoreSpecBuilder, fieldData, methodsPair, STATE, IN_STATE, OUT_STATE, "putShortArray", "getShortArray", "set", "get", false);
                    } else if (typeUtils.isSameType(fieldData.typeMirror, typeUtils.getArrayType(typeUtils.getPrimitiveType(TypeKind.LONG)))) {
                        addStatementsForField(saveSpecBuilder, restoreSpecBuilder, fieldData, methodsPair, STATE, IN_STATE, OUT_STATE, "putLongArray", "getLongArray", "set", "get", false);
                    } else if (typeUtils.isSameType(fieldData.typeMirror, typeUtils.getArrayType(typeUtils.getPrimitiveType(TypeKind.DOUBLE)))) {
                        addStatementsForField(saveSpecBuilder, restoreSpecBuilder, fieldData, methodsPair, STATE, IN_STATE, OUT_STATE, "putDoubleArray", "getDoubleArray", "set", "get", false);
                    } else if (typeUtils.isAssignable(elementUtils.getTypeElement(CharSequence.class.getCanonicalName()).asType(), fieldData.typeMirror)) {
                        addStatementsForField(saveSpecBuilder, restoreSpecBuilder, fieldData, methodsPair, STATE, IN_STATE, OUT_STATE, "putCharSequence", "getCharSequence", "set", "get", false);
                    } else if (typeUtils.isAssignable(fieldData.typeMirror, parcelableTypeElement.asType())) {
                        addStatementsForField(saveSpecBuilder, restoreSpecBuilder, fieldData, methodsPair, STATE, IN_STATE, OUT_STATE, "putParcelable", "getParcelable", "set", "get", false);
                    } else {
                        boolean saved = false;

                        if (!saved) {
                            DeclaredType arrayListType = typeUtils.getDeclaredType(
                                    elementUtils.getTypeElement("java.util.ArrayList"),
                                    typeUtils.getWildcardType(parcelableTypeElement.asType(), null));

                            if (typeUtils.isAssignable(fieldData.typeMirror, arrayListType)) {
                                addStatementsForField(saveSpecBuilder, restoreSpecBuilder, fieldData, methodsPair, STATE, IN_STATE, OUT_STATE, "putParcelableArrayList",
                                        CodeBlock.builder().add("<$T>getParcelableArrayList", getGenericTypeOfSuperclass((DeclaredType) fieldData.typeMirror, arrayListType).get(0)).build().toString(),
                                        "set", "get", false);
                                saved = true;
                            }
                        }

                        if (!saved) {
                            DeclaredType sparseArrayType = typeUtils.getDeclaredType(
                                    elementUtils.getTypeElement("android.util.SparseArray"),
                                    typeUtils.getWildcardType(parcelableTypeElement.asType(), null));

                            if (typeUtils.isAssignable(fieldData.typeMirror, sparseArrayType)) {
                                addStatementsForField(saveSpecBuilder, restoreSpecBuilder, fieldData, methodsPair, STATE, IN_STATE, OUT_STATE, "putSparseParcelableArray",
                                        CodeBlock.builder().add("<$T>getSparseParcelableArray", getGenericTypeOfSuperclass((DeclaredType) fieldData.typeMirror, sparseArrayType).get(0)).build().toString(),
                                        "set", "get", false);
                                saved = true;
                            }
                        }

                        if (!saved) {
                            if (fieldData.typeMirror instanceof ArrayType
                                    && typeUtils.isAssignable(((ArrayType) fieldData.typeMirror).getComponentType(), parcelableTypeElement.asType())) {
                                addStatementsForField(saveSpecBuilder, restoreSpecBuilder, fieldData, methodsPair, STATE, IN_STATE, OUT_STATE, "putParcelableArray", "getParcelableArray", "set", "get", false);
                                saved = true;
                            }
                        }

                        if (!saved) {
                            addStatementsForField(saveSpecBuilder, restoreSpecBuilder, fieldData, methodsPair, STATE, IN_STATE, OUT_STATE, "putSerializable", "getSerializable", "set", "get", true);
                        }

                    }
                }

                saveSpecBuilder.addCode("\n");
                restoreSpecBuilder.addCode("\n");
            }

            if (!allPublic) {
                if (gettersCount != fieldsData.size()) {
                    saveSpecBuilder.addCode("} " + createCatchBlock(NoSuchFieldException.class));
                    saveSpecBuilder.addCode(" " + createCatchBlock(IllegalAccessException.class));
                    saveSpecBuilder.addCode("\n");
                }

                if (settersCount != fieldsData.size()) {
                    restoreSpecBuilder.addCode("} " + createCatchBlock(NoSuchFieldException.class));
                    restoreSpecBuilder.addCode(" " + createCatchBlock(IllegalAccessException.class));
                    restoreSpecBuilder.addCode("\n");
                }
            }
        }

        MethodSpec saveSpec = saveSpecBuilder.build();
        MethodSpec restoreSpec = restoreSpecBuilder.build();

        TypeSpec typeSpec = TypeSpec.classBuilder(name)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(saveSpec)
                .addMethod(restoreSpec)
                .build();

        JavaFile javaFile = JavaFile.builder(processingEnv.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString(), typeSpec).build();
        javaFile.writeTo(processingEnv.getFiler());
    }

    private void addStatementsForField(
            MethodSpec.Builder saveSpecBuilder,
            MethodSpec.Builder restoreSpecBuilder,
            FieldData fieldData,
            MethodsPair methodsPair,
            String stateVariableName,
            String saveStateName,
            String restoreStateName,
            String bundlePutMethod,
            String bundleGetMethod,
            String reflectionSetMethod,
            String reflectionGetMethod,
            boolean asSerializable) {

        VariableElement field = fieldData.variableElement;
        boolean isPublic = field.getModifiers().contains(Modifier.PUBLIC);

        TypeMirror saveType = asSerializable ? processingEnv.getElementUtils().getTypeElement(Serializable.class.getName()).asType() : fieldData.typeMirror;
        TypeMirror restoreType = fieldData.typeMirror;

        final String fieldName = field.getSimpleName().toString();
        final String fFieldName = "f" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        if (methodsPair.getterElement == null && !isPublic) {
            saveSpecBuilder.addStatement("$T $L = $L.class.getDeclaredField($S)", Field.class, fFieldName, getClassName(fieldData.typeData.typeElement), fieldName);
            saveSpecBuilder.addStatement("$L.setAccessible(true)", fFieldName);
        }

        if (methodsPair.setterElement == null && !isPublic) {
            restoreSpecBuilder.addStatement("$T $L = $L.class.getDeclaredField($S)", Field.class, fFieldName, getClassName(fieldData.typeData.typeElement), fieldName);
            restoreSpecBuilder.addStatement("$L.setAccessible(true)", fFieldName);
        }

        if (isPublic) {
            saveSpecBuilder.addStatement("$L.$L($S, ($T) $L.$L)", saveStateName, bundlePutMethod, fieldName, saveType, stateVariableName, fieldName);
        } else if (methodsPair.getterElement != null) {
            saveSpecBuilder.addStatement("$L.$L($S, ($T) $L.$L())", saveStateName, bundlePutMethod, fieldName, saveType, stateVariableName, methodsPair.getterElement.getSimpleName());
        } else {
            saveSpecBuilder.addStatement("$L.$L($S, ($T) $L.$L($L))", saveStateName, bundlePutMethod, fieldName, saveType, fFieldName, reflectionGetMethod, stateVariableName);
        }

        if (isPublic) {
            restoreSpecBuilder.addStatement("$L.$L = ($T) $L.$L($S)", stateVariableName, fieldName, restoreType, restoreStateName, bundleGetMethod, fieldName);
        } else if (methodsPair.setterElement != null) {
            restoreSpecBuilder.addStatement("$L.$L(($T) $L.$L($S))", stateVariableName, methodsPair.setterElement.getSimpleName(), restoreType, restoreStateName, bundleGetMethod, fieldName);
        } else {
            restoreSpecBuilder.addStatement("$L.$L($L, $L.$L($S))", fFieldName, reflectionSetMethod, stateVariableName, restoreStateName, bundleGetMethod, fieldName);
        }
    }

    private CodeBlock createCatchBlock(Class<? extends Throwable> ex) {
        CodeBlock.Builder builder = CodeBlock.builder();

        builder.add("catch ($T ex) {\n", ex).indent();
        {
            builder.addStatement("ex.printStackTrace()");
        }
        builder.unindent().add("}");

        return builder.build();
    }

    //--------------------------------Util methods----------------------------------------------//

    private String getClassName(TypeElement typeElement) {
        String name = typeElement.getQualifiedName().toString();
        if (typeElement.getTypeParameters().size() == 0) {
            return name;
        } else {
            int indexOf = name.indexOf("<");
            return name.substring(0, indexOf);
        }
    }

    private List<? extends TypeMirror> getGenericTypeOfSuperclass(DeclaredType fieldType, TypeMirror superClass) {
        final String superClassString = processingEnv.getTypeUtils().asElement(superClass).toString();

        if (fieldType.asElement().toString().equals(superClassString)) {
            return fieldType.getTypeArguments();
        } else {
            DeclaredType targetType = (DeclaredType) processingEnv.getTypeUtils().directSupertypes(fieldType).get(0);
            while (!targetType.asElement().toString().equals(superClassString)) {
                targetType = (DeclaredType) processingEnv.getTypeUtils().directSupertypes(targetType).get(0);
            }

            return targetType.getTypeArguments();
        }
    }

    private DeclaredType createDeclaredType(TypeElement typeElement) {
        if (typeElement.getTypeParameters().size() > 0) {
            TypeMirror[] types = new TypeMirror[typeElement.getTypeParameters().size()];
            for (int i = 0; i < types.length; i++) {
                types[i] = typeElement.getTypeParameters().get(i).asType();
            }
            return processingEnv.getTypeUtils().getDeclaredType(typeElement, types);
        } else {
            return processingEnv.getTypeUtils().getDeclaredType(typeElement);
        }
    }

    private Map<FieldData, MethodsPair> getAllAcceptableFieldsData(TypeElement typeElement, DeclaredType declaredType, boolean includeSuper) {
        Map<FieldData, MethodsPair> acceptableFields = new HashMap<>();

        TypeData typeData = new TypeData(typeElement, declaredType);

        for (Element element : typeElement.getEnclosedElements()) {
            if (element.getKind() == ElementKind.FIELD) {
                if (!element.getModifiers().contains(Modifier.STATIC)
                        && !element.getModifiers().contains(Modifier.FINAL)
                        && !element.getModifiers().contains(Modifier.TRANSIENT)
                        && element.getAnnotation(Ignore.class) == null) {

                    FieldData fieldData = new FieldData(
                            typeData,
                            (VariableElement) element,
                            processingEnv.getTypeUtils().asMemberOf(declaredType, element)
                    );

                    acceptableFields.put(fieldData, getAccessMethodsFor(fieldData));
                }
            }
        }

        if (includeSuper) {
            List<? extends TypeMirror> superTypes = processingEnv.getTypeUtils().directSupertypes(declaredType);
            for (int i = 0; i < superTypes.size(); i++) {
                TypeMirror mirror = superTypes.get(i);
                Element element = processingEnv.getTypeUtils().asElement(mirror);

                if (element.getKind() == ElementKind.CLASS) {
                    acceptableFields.putAll(getAllAcceptableFieldsData(
                            (TypeElement) element,
                            (DeclaredType) mirror,
                            includeSuper
                    ));
                }
            }
        }

        return acceptableFields;
    }

    private MethodsPair getAccessMethodsFor(FieldData fieldData) {
        ExecutableElement getterElement = null;
        ExecutableType getterType = null;

        ExecutableElement setterElement = null;
        ExecutableType setterType = null;

        for (Element methodElement : fieldData.typeData.typeElement.getEnclosedElements()) {
            if (methodElement.getKind() == ElementKind.METHOD && methodElement.getModifiers().contains(Modifier.PUBLIC)) {

                ExecutableElement executableElement = (ExecutableElement) methodElement;
                final String methodName = executableElement.getSimpleName().toString();

                if (methodName.length() > 3) {
                    String fieldName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);

                    if (fieldName.equals(fieldData.variableElement.getSimpleName().toString())) {
                        ExecutableType executableType = (ExecutableType) processingEnv.getTypeUtils().asMemberOf(fieldData.typeData.declaredType, executableElement);

                        if (methodName.startsWith("get") || methodName.startsWith("is")) {
                            if (getterElement == null) {
                                if (executableType.getParameterTypes().size() == 0 && processingEnv.getTypeUtils().isAssignable(executableType.getReturnType(), fieldData.typeMirror)) {
                                    getterElement = executableElement;
                                    getterType = executableType;
                                }
                            }
                        } else if (methodName.startsWith("set")) {
                            if (setterElement == null) {
                                if (executableType.getParameterTypes().size() == 1 && processingEnv.getTypeUtils().isAssignable(executableType.getParameterTypes().get(0), fieldData.typeMirror)) {
                                    setterElement = executableElement;
                                    setterType = executableType;
                                }
                            }
                        }

                        //cancel loop if
                        if (getterElement != null && setterElement != null) {
                            break;
                        }
                    }
                }
            }
        }

        return new MethodsPair(getterElement, getterType, setterElement, setterType);
    }

    private List<TypeName> convertTypeMirrorsToTypeNames(List<? extends TypeMirror> typeMirrors) {
        List<TypeName> typeVariableNames = new ArrayList<>();
        for (TypeMirror typeMirror : typeMirrors) {
            typeVariableNames.add(TypeVariableName.get(typeMirror));
        }
        return typeVariableNames;
    }

    private List<TypeVariableName> convertTypeVariablesToTypeVariableNames(List<? extends TypeVariable> typeVariables) {
        List<TypeVariableName> typeVariableNames = new ArrayList<>();
        for (TypeVariable typeVariable : typeVariables) {
            typeVariableNames.add(TypeVariableName.get(typeVariable));
        }
        return typeVariableNames;
    }

    private List<TypeVariableName> convertTypeParametersToTypeVariableNames(List<? extends TypeParameterElement> parameterElements) {
        List<TypeVariableName> typeVariableNames = new ArrayList<>();
        for (TypeParameterElement element : parameterElements) {
            typeVariableNames.add(TypeVariableName.get(element));
        }
        return typeVariableNames;
    }

    //--------------------------------Classes---------------------------------------------------//

    public static class TypeData {

        final TypeElement typeElement;
        final DeclaredType declaredType;

        public TypeData(TypeElement typeElement, DeclaredType declaredType) {
            this.typeElement = typeElement;
            this.declaredType = declaredType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TypeData typeData = (TypeData) o;

            if (!typeElement.equals(typeData.typeElement)) return false;
            return declaredType.equals(typeData.declaredType);

        }

        @Override
        public int hashCode() {
            int result = typeElement.hashCode();
            result = 31 * result + declaredType.hashCode();
            return result;
        }

    }

    public static class FieldData {

        final TypeData typeData;

        final VariableElement variableElement;
        final TypeMirror typeMirror;

        public FieldData(TypeData typeData, VariableElement variableElement, TypeMirror typeMirror) {
            this.typeData = typeData;
            this.variableElement = variableElement;
            this.typeMirror = typeMirror;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FieldData fieldData = (FieldData) o;

            if (!typeData.equals(fieldData.typeData)) return false;
            if (!variableElement.equals(fieldData.variableElement)) return false;
            return typeMirror.equals(fieldData.typeMirror);

        }

        @Override
        public int hashCode() {
            int result = typeData.hashCode();
            result = 31 * result + variableElement.hashCode();
            result = 31 * result + typeMirror.hashCode();
            return result;
        }

    }

    public static class MethodsPair {

        final ExecutableElement getterElement;
        final ExecutableType getterType;

        final ExecutableElement setterElement;
        final ExecutableType setterType;

        public MethodsPair(ExecutableElement getterElement, ExecutableType getterType, ExecutableElement setterElement, ExecutableType setterType) {
            this.getterElement = getterElement;
            this.getterType = getterType;
            this.setterElement = setterElement;
            this.setterType = setterType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MethodsPair that = (MethodsPair) o;

            if (getterElement != null ? !getterElement.equals(that.getterElement) : that.getterElement != null)
                return false;
            if (getterType != null ? !getterType.equals(that.getterType) : that.getterType != null)
                return false;
            if (setterElement != null ? !setterElement.equals(that.setterElement) : that.setterElement != null)
                return false;
            return setterType != null ? setterType.equals(that.setterType) : that.setterType == null;

        }

        @Override
        public int hashCode() {
            int result = getterElement != null ? getterElement.hashCode() : 0;
            result = 31 * result + (getterType != null ? getterType.hashCode() : 0);
            result = 31 * result + (setterElement != null ? setterElement.hashCode() : 0);
            result = 31 * result + (setterType != null ? setterType.hashCode() : 0);
            return result;
        }

    }

}
