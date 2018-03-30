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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
import javax.tools.Diagnostic;

@SupportedAnnotationTypes({"com.ufkoku.mvp.viewstate.autosavable.AutoSavable"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class AutosavableProcessor2 extends AbstractProcessor {

    private static final String SAVER_SUFFIX = "Saver";

    private static final String SAVE_METHOD = "save";

    private static final String RESTORE_METHOD = "restore";

    private static final String OUT_STATE_PARAM = "outState";

    private static final String IN_STATE_PARAM = "inState";

    private static final String STATE = "state";

    private static final String CLASS_NAME_BUNDLE = "android.os.Bundle";

    private static final String CLASS_NAME_PARCELABLE = "android.os.Parcelable";

    private TypeElement bundleTypeElement;

    private TypeElement parcelableTypeElement;

    @Override
    @SuppressWarnings("unchecked")
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        bundleTypeElement = processingEnv.getElementUtils().getTypeElement(CLASS_NAME_BUNDLE);
        parcelableTypeElement = processingEnvironment.getElementUtils().getTypeElement(
                CLASS_NAME_PARCELABLE);
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(AutoSavable.class);
            if (elements.size() > 0) {
                for (Element element : elements) {
                    if (element.getKind() == ElementKind.CLASS) {
                        AutoSavable annotation = element.getAnnotation(AutoSavable.class);
                        TypeElement typeElement = (TypeElement) element;
                        createSaverClass(typeElement,
                                         createDeclaredType(typeElement),
                                         annotation.includeSuper());
                    }
                }
            }
            return true;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void createSaverClass(TypeElement typeElement,
                                  DeclaredType declaredType,
                                  boolean includeSuper) throws IOException {
        String name = typeElement.getSimpleName() + SAVER_SUFFIX;

        List<TypeVariableName> methodsTypeVariables = convertTypeParametersToTypeVariableNames(
                typeElement.getTypeParameters());

        MethodSpec.Builder saveSpecBuilder = MethodSpec.methodBuilder(SAVE_METHOD)
                                                       .addModifiers(Modifier.PUBLIC,
                                                                     Modifier.STATIC)
                                                       .addTypeVariables(methodsTypeVariables)
                                                       .addParameter(TypeName.get(typeElement.asType()),
                                                                     STATE)
                                                       .addParameter(TypeName.get(bundleTypeElement.asType()),
                                                                     IN_STATE_PARAM)
                                                       .returns(void.class);

        MethodSpec.Builder restoreSpecBuilder = MethodSpec.methodBuilder(RESTORE_METHOD)
                                                          .addModifiers(Modifier.PUBLIC,
                                                                        Modifier.STATIC)
                                                          .addTypeVariables(methodsTypeVariables)
                                                          .addParameter(TypeName.get(typeElement.asType()),
                                                                        STATE)
                                                          .addParameter(
                                                                  TypeName.get(bundleTypeElement.asType()),
                                                                  OUT_STATE_PARAM)
                                                          .returns(void.class);

        List<FieldData> fieldsData = getAllAcceptableFieldsData(typeElement,
                                                                declaredType,
                                                                includeSuper);
        if (fieldsData.size() > 0) {
            generateMethodsBody(saveSpecBuilder, restoreSpecBuilder, fieldsData);
        }

        MethodSpec saveSpec = saveSpecBuilder.build();
        MethodSpec restoreSpec = restoreSpecBuilder.build();

        TypeSpec typeSpec = TypeSpec.classBuilder(name)
                                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                                    .addMethod(saveSpec)
                                    .addMethod(restoreSpec)
                                    .build();

        JavaFile javaFile = JavaFile.builder(processingEnv.getElementUtils()
                                                          .getPackageOf(typeElement)
                                                          .getQualifiedName()
                                                          .toString(),
                                             typeSpec)
                                    .build();

        javaFile.writeTo(processingEnv.getFiler());
    }

    private void generateMethodsBody(MethodSpec.Builder saveSpec,
                                     MethodSpec.Builder restoreSpec,
                                     List<FieldData> fieldsData) {
        boolean allPublic = true;
        int gettersCount = 0;
        int settersCount = 0;

        for (FieldData fieldData : fieldsData) {
            allPublic = allPublic && fieldData.variableElement.getModifiers().contains(Modifier.PUBLIC);

            if (fieldData.methodsPair.getterElement != null) {
                gettersCount++;
            }

            if (fieldData.methodsPair.setterElement != null) {
                settersCount++;
            }
        }

        if (!allPublic) {
            if (gettersCount != fieldsData.size()) {
                saveSpec.addCode("try {\n");
            }

            if (settersCount != fieldsData.size()) {
                restoreSpec.addCode("try {\n");
            }
        }

        StatementData.Builder statementBuilder = new StatementData.Builder()
                .setSaveSpecBuilder(saveSpec)
                .setRestoreSpecBuilder(restoreSpec)
                .setStateVariableName(STATE)
                .setSaveStateName(IN_STATE_PARAM)
                .setRestoreStateName(OUT_STATE_PARAM);

        for (FieldData fieldData : fieldsData) {

            Types typeUtils = processingEnv.getTypeUtils();
            Elements elementUtils = processingEnv.getElementUtils();

            statementBuilder.setFieldData(fieldData)
                            .setAsSerializable(false);

            if (fieldData.typeMirror instanceof PrimitiveType) {
                if (typeUtils.isSameType(fieldData.typeMirror,
                                         typeUtils.getPrimitiveType(TypeKind.BOOLEAN))) {
                    statementBuilder.setBundlePutMethod("putBoolean")
                                    .setBundleGetMethod("getBoolean")
                                    .setReflectionSetMethod("setBoolean")
                                    .setReflectionGetMethod("getBoolean");
                } else if (typeUtils.isSameType(fieldData.typeMirror,
                                                typeUtils.getPrimitiveType(TypeKind.BYTE))) {
                    statementBuilder.setBundlePutMethod("putByte")
                                    .setBundleGetMethod("getByte")
                                    .setReflectionSetMethod("setByte")
                                    .setReflectionGetMethod("getByte");
                } else if (typeUtils.isSameType(fieldData.typeMirror,
                                                typeUtils.getPrimitiveType(TypeKind.CHAR))) {
                    statementBuilder.setBundlePutMethod("putChar")
                                    .setBundleGetMethod("getChar")
                                    .setReflectionSetMethod("setChar")
                                    .setReflectionGetMethod("getChar");
                } else if (typeUtils.isSameType(fieldData.typeMirror,
                                                typeUtils.getPrimitiveType(TypeKind.FLOAT))) {
                    statementBuilder.setBundlePutMethod("putFloat")
                                    .setBundleGetMethod("getFloat")
                                    .setReflectionSetMethod("setFloat")
                                    .setReflectionGetMethod("getFloat");
                } else if (typeUtils.isSameType(fieldData.typeMirror,
                                                typeUtils.getPrimitiveType(TypeKind.INT))) {
                    statementBuilder.setBundlePutMethod("putInt")
                                    .setBundleGetMethod("getInt")
                                    .setReflectionSetMethod("setInt")
                                    .setReflectionGetMethod("getInt");
                } else if (typeUtils.isSameType(fieldData.typeMirror,
                                                typeUtils.getPrimitiveType(TypeKind.SHORT))) {
                    statementBuilder.setBundlePutMethod("putShort")
                                    .setBundleGetMethod("getShort")
                                    .setReflectionSetMethod("setShort")
                                    .setReflectionGetMethod("getShort");
                } else if (typeUtils.isSameType(fieldData.typeMirror,
                                                typeUtils.getPrimitiveType(TypeKind.LONG))) {
                    statementBuilder.setBundlePutMethod("putLong")
                                    .setBundleGetMethod("getLong")
                                    .setReflectionSetMethod("setLong")
                                    .setReflectionGetMethod("getLong");
                } else if (typeUtils.isSameType(fieldData.typeMirror,
                                                typeUtils.getPrimitiveType(TypeKind.DOUBLE))) {
                    statementBuilder.setBundlePutMethod("putDouble")
                                    .setBundleGetMethod("getDouble")
                                    .setReflectionSetMethod("setDouble")
                                    .setReflectionGetMethod("getDouble");
                }
            } else {

                statementBuilder.setReflectionSetMethod("set")
                                .setReflectionGetMethod("get");

                if (typeUtils.isSameType(fieldData.typeMirror, bundleTypeElement.asType())) {
                    statementBuilder.setBundlePutMethod("putBundle")
                                    .setBundleGetMethod("getBundle");
                } else if (typeUtils.isSameType(fieldData.typeMirror,
                                                typeUtils.getArrayType(
                                                        typeUtils.getPrimitiveType(TypeKind.BOOLEAN)))) {
                    statementBuilder.setBundlePutMethod("putBooleanArray")
                                    .setBundleGetMethod("getBooleanArray");
                } else if (typeUtils.isSameType(fieldData.typeMirror,
                                                typeUtils.getArrayType(
                                                        typeUtils.getPrimitiveType(TypeKind.BYTE)))) {
                    statementBuilder.setBundlePutMethod("putByteArray")
                                    .setBundleGetMethod("getByteArray");
                } else if (typeUtils.isSameType(fieldData.typeMirror,
                                                typeUtils.getArrayType(
                                                        typeUtils.getPrimitiveType(TypeKind.CHAR)))) {
                    statementBuilder.setBundlePutMethod("putCharArray")
                                    .setBundleGetMethod("getCharArray");
                } else if (typeUtils.isSameType(fieldData.typeMirror,
                                                typeUtils.getArrayType(
                                                        typeUtils.getPrimitiveType(TypeKind.FLOAT)))) {
                    statementBuilder.setBundlePutMethod("putFloatArray")
                                    .setBundleGetMethod("getFloatArray");
                } else if (typeUtils.isSameType(fieldData.typeMirror,
                                                typeUtils.getArrayType(
                                                        typeUtils.getPrimitiveType(TypeKind.INT)))) {
                    statementBuilder.setBundlePutMethod("putIntArray")
                                    .setBundleGetMethod("getIntArray");
                } else if (typeUtils.isSameType(fieldData.typeMirror,
                                                typeUtils.getArrayType(
                                                        typeUtils.getPrimitiveType(TypeKind.SHORT)))) {
                    statementBuilder.setBundlePutMethod("putShortArray")
                                    .setBundleGetMethod("getShortArray");
                } else if (typeUtils.isSameType(fieldData.typeMirror,
                                                typeUtils.getArrayType(
                                                        typeUtils.getPrimitiveType(TypeKind.LONG)))) {
                    statementBuilder.setBundlePutMethod("putLongArray")
                                    .setBundleGetMethod("getLongArray");
                } else if (typeUtils.isSameType(fieldData.typeMirror,
                                                typeUtils.getArrayType(
                                                        typeUtils.getPrimitiveType(TypeKind.DOUBLE)))) {
                    statementBuilder.setBundlePutMethod("putDoubleArray")
                                    .setBundleGetMethod("getDoubleArray");
                } else if (typeUtils.isAssignable(fieldData.typeMirror,
                                                  elementUtils.getTypeElement(CharSequence.class.getCanonicalName())
                                                              .asType())) {
                    statementBuilder.setBundlePutMethod("putCharSequence")
                                    .setBundleGetMethod("getCharSequence");
                } else if (typeUtils.isAssignable(fieldData.typeMirror,
                                                  parcelableTypeElement.asType())) {
                    statementBuilder.setBundlePutMethod("putParcelable")
                                    .setBundleGetMethod("getParcelable");
                } else {
                    boolean saved = false;

                    if (!saved) {
                        DeclaredType arrayListType = typeUtils.getDeclaredType(
                                elementUtils.getTypeElement("java.util.ArrayList"),
                                typeUtils.getWildcardType(parcelableTypeElement.asType(), null));

                        if (typeUtils.isAssignable(fieldData.typeMirror, arrayListType)) {
                            statementBuilder.setBundlePutMethod("putParcelableArrayList")
                                            .setBundleGetMethod(CodeBlock.builder()
                                                                         .add("<$T>getParcelableArrayList",
                                                                              getGenericTypeOfSuperclass(
                                                                                      (DeclaredType) fieldData.typeMirror,
                                                                                      arrayListType)
                                                                                      .get(0)
                                                                             )
                                                                         .build()
                                                                         .toString()
                                                               );
                            saved = true;
                        }
                    }

                    if (!saved) {
                        DeclaredType sparseArrayType = typeUtils.getDeclaredType(
                                elementUtils.getTypeElement("android.util.SparseArray"),
                                typeUtils.getWildcardType(parcelableTypeElement.asType(), null));

                        if (typeUtils.isAssignable(fieldData.typeMirror, sparseArrayType)) {
                            statementBuilder.setBundlePutMethod("putSparseParcelableArray")
                                            .setBundleGetMethod(CodeBlock.builder()
                                                                         .add("<$T>getSparseParcelableArray",
                                                                              getGenericTypeOfSuperclass(
                                                                                      (DeclaredType) fieldData.typeMirror,
                                                                                      sparseArrayType)
                                                                                      .get(0))
                                                                         .build()
                                                                         .toString()
                                                               );
                            saved = true;
                        }
                    }

                    if (!saved) {
                        if (fieldData.typeMirror instanceof ArrayType
                            && typeUtils.isAssignable(((ArrayType) fieldData.typeMirror).getComponentType(),
                                                      parcelableTypeElement.asType())) {

                            statementBuilder.setBundlePutMethod("putParcelableArray")
                                            .setBundleGetMethod("getParcelableArray");
                            saved = true;
                        }
                    }

                    if (!saved) {
                        if (typeUtils.isAssignable(fieldData.typeMirror,
                                                   elementUtils.getTypeElement(Serializable.class.getCanonicalName()).asType())) {

                            statementBuilder.setBundlePutMethod("putSerializable")
                                            .setBundleGetMethod("getSerializable")
                                            .setAsSerializable(true);
                            saved = true;
                        }
                    }

                    if (!saved) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                                                 "Unable to save this field to bundle. Please mark it with @Ignore annotation and save it manually",
                                                                 fieldData.variableElement);
                        continue;
                    }

                }
            }

            addStatementsForField(statementBuilder.build());

            saveSpec.addCode("\n");
            restoreSpec.addCode("\n");
        }

        if (!allPublic) {
            if (gettersCount != fieldsData.size()) {
                saveSpec.addCode("} " + createCatchBlock(NoSuchFieldException.class));
                saveSpec.addCode(" " + createCatchBlock(IllegalAccessException.class));
                saveSpec.addCode("\n");
            }

            if (settersCount != fieldsData.size()) {
                restoreSpec.addCode("} " + createCatchBlock(NoSuchFieldException.class));
                restoreSpec.addCode(" " + createCatchBlock(IllegalAccessException.class));
                restoreSpec.addCode("\n");
            }
        }
    }

    private void addStatementsForField(StatementData data) {
        VariableElement field = data.fieldData.variableElement;
        boolean isPublic = field.getModifiers().contains(Modifier.PUBLIC);

        TypeMirror saveType = data.asSerializable
                              ? processingEnv.getElementUtils()
                                             .getTypeElement(Serializable.class.getName())
                                             .asType()
                              : data.fieldData.typeMirror;

        TypeMirror restoreType = data.fieldData.typeMirror;

        final String fieldName = field.getSimpleName().toString();
        final String fFieldName = "f" + fieldName.substring(0,
                                                            1).toUpperCase() + fieldName.substring(1);

        if (isPublic) {
            data.saveSpecBuilder.addStatement("$L.$L($S, ($T) $L.$L)",
                                              data.saveStateName,
                                              data.bundlePutMethod,
                                              fieldName,
                                              saveType,
                                              data.stateVariableName,
                                              fieldName);
        } else if (data.fieldData.methodsPair.getterElement != null) {
            data.saveSpecBuilder.addStatement("$L.$L($S, ($T) $L.$L())",
                                              data.saveStateName,
                                              data.bundlePutMethod,
                                              fieldName,
                                              saveType,
                                              data.stateVariableName,
                                              data.fieldData.methodsPair.getterElement.getSimpleName());
        } else {
            data.saveSpecBuilder.addStatement("$T $L = $L.class.getDeclaredField($S)",
                                              Field.class,
                                              fFieldName,
                                              data.fieldData.typeData.typeElement.getQualifiedName().toString(),
                                              fieldName);

            data.saveSpecBuilder.addStatement("$L.setAccessible(true)", fFieldName);

            data.saveSpecBuilder.addStatement("$L.$L($S, ($T) $L.$L($L))",
                                              data.saveStateName,
                                              data.bundlePutMethod,
                                              fieldName,
                                              saveType,
                                              fFieldName,
                                              data.reflectionGetMethod,
                                              data.stateVariableName);
        }

        if (isPublic) {
            data.restoreSpecBuilder.addStatement("$L.$L = ($T) $L.$L($S)",
                                                 data.stateVariableName,
                                                 fieldName,
                                                 restoreType,
                                                 data.restoreStateName,
                                                 data.bundleGetMethod,
                                                 fieldName);
        } else if (data.fieldData.methodsPair.setterElement != null) {
            data.restoreSpecBuilder.addStatement("$L.$L(($T) $L.$L($S))",
                                                 data.stateVariableName,
                                                 data.fieldData.methodsPair.setterElement.getSimpleName(),
                                                 restoreType,
                                                 data.restoreStateName,
                                                 data.bundleGetMethod,
                                                 fieldName);
        } else {
            data.restoreSpecBuilder.addStatement("$T $L = $L.class.getDeclaredField($S)",
                                                 Field.class,
                                                 fFieldName,
                                                 data.fieldData.typeData.typeElement.getQualifiedName().toString(),
                                                 fieldName);

            data.restoreSpecBuilder.addStatement("$L.setAccessible(true)", fFieldName);

            data.restoreSpecBuilder.addStatement("$L.$L($L, $L.$L($S))",
                                                 fFieldName,
                                                 data.reflectionSetMethod,
                                                 data.stateVariableName,
                                                 data.restoreStateName,
                                                 data.bundleGetMethod,
                                                 fieldName);
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

    private List<? extends TypeMirror> getGenericTypeOfSuperclass(DeclaredType fieldType,
                                                                  TypeMirror superClass) {
        final String superClassString = processingEnv.getTypeUtils().asElement(superClass).toString();

        if (fieldType.asElement().toString().equals(superClassString)) {
            return fieldType.getTypeArguments();
        } else {
            DeclaredType targetType = (DeclaredType) processingEnv.getTypeUtils()
                                                                  .directSupertypes(fieldType).get(0);

            while (!targetType.asElement().toString().equals(superClassString)) {
                targetType = (DeclaredType) processingEnv.getTypeUtils()
                                                         .directSupertypes(targetType)
                                                         .get(0);
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

    private List<FieldData> getAllAcceptableFieldsData(TypeElement typeElement,
                                                       DeclaredType declaredType,
                                                       boolean includeSuper) {
        List<FieldData> acceptableFields = new ArrayList<>();

        TypeData typeData = new TypeData(typeElement, declaredType);

        for (Element element : typeElement.getEnclosedElements()) {
            if (element.getKind() == ElementKind.FIELD) {
                if (!element.getModifiers().contains(Modifier.STATIC)
                    && !element.getModifiers().contains(Modifier.FINAL)
                    && !element.getModifiers().contains(Modifier.TRANSIENT)
                    && element.getAnnotation(DontSave.class) == null) {

                    FieldData.Builder builder = new FieldData.Builder()
                            .setTypeData(typeData)
                            .setVariableElement((VariableElement) element)
                            .setTypeMirror(processingEnv.getTypeUtils()
                                                        .asMemberOf(declaredType, element));

                    fillAccessMethods(builder);

                    FieldData fieldData = builder.build();

                    acceptableFields.add(fieldData);
                }
            }
        }

        if (includeSuper) {
            List<? extends TypeMirror> superTypes = processingEnv.getTypeUtils()
                                                                 .directSupertypes(declaredType);
            for (int i = 0; i < superTypes.size(); i++) {
                TypeMirror mirror = superTypes.get(i);
                Element element = processingEnv.getTypeUtils().asElement(mirror);

                if (element.getKind() == ElementKind.CLASS) {
                    acceptableFields.addAll(getAllAcceptableFieldsData((TypeElement) element,
                                                                       (DeclaredType) mirror,
                                                                       true
                                                                      ));
                }
            }
        }

        return acceptableFields;
    }

    private void fillAccessMethods(FieldData.Builder dataBuilder) {
        ExecutableElement getterElement = null;
        ExecutableType getterType = null;

        ExecutableElement setterElement = null;
        ExecutableType setterType = null;

        for (Element methodElement : dataBuilder.getTypeData().typeElement.getEnclosedElements()) {
            if (methodElement.getKind() == ElementKind.METHOD
                && methodElement.getModifiers().contains(Modifier.PUBLIC)) {

                ExecutableElement executableElement = (ExecutableElement) methodElement;
                final String methodName = executableElement.getSimpleName().toString();

                if (methodName.length() > 3) {
                    String fieldName = methodName.substring(3, 4)
                                                 .toLowerCase() + methodName.substring(4);

                    if (fieldName.equals(dataBuilder.getVariableElement().getSimpleName().toString())) {
                        ExecutableType executableType =
                                (ExecutableType) processingEnv.getTypeUtils()
                                                              .asMemberOf(dataBuilder.getTypeData().declaredType,
                                                                          executableElement);

                        if (methodName.startsWith("get") || methodName.startsWith("is")) {
                            if (getterElement == null) {
                                if (executableType.getParameterTypes().size() == 0
                                    && processingEnv.getTypeUtils().isAssignable(executableType.getReturnType(),
                                                                                 dataBuilder.getTypeMirror())) {
                                    getterElement = executableElement;
                                    getterType = executableType;
                                }
                            }
                        } else if (methodName.startsWith("set")) {
                            if (setterElement == null) {
                                if (executableType.getParameterTypes().size() == 1
                                    && processingEnv.getTypeUtils().isAssignable(executableType.getParameterTypes()
                                                                                               .get(0),
                                                                                 dataBuilder.getTypeMirror())) {
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

        MethodsPair pair = new MethodsPair(getterElement, getterType, setterElement, setterType);
        dataBuilder.setMethodsPair(pair);
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
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            TypeData typeData = (TypeData) o;

            if (!typeElement.equals(typeData.typeElement)) {
                return false;
            }
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

        final MethodsPair methodsPair;

        public FieldData(TypeData typeData,
                         VariableElement variableElement,
                         TypeMirror typeMirror,
                         MethodsPair methodsPair) {
            this.typeData = typeData;
            this.variableElement = variableElement;
            this.typeMirror = typeMirror;
            this.methodsPair = methodsPair;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof FieldData)) {
                return false;
            }
            FieldData fieldData = (FieldData) o;
            return Objects.equals(typeData, fieldData.typeData) &&
                   Objects.equals(variableElement, fieldData.variableElement) &&
                   Objects.equals(typeMirror, fieldData.typeMirror) &&
                   Objects.equals(methodsPair, fieldData.methodsPair);
        }

        @Override
        public int hashCode() {
            return Objects.hash(typeData, variableElement, typeMirror, methodsPair);
        }

        public static class Builder {

            private TypeData typeData;

            private VariableElement variableElement;

            private TypeMirror typeMirror;

            private MethodsPair methodsPair;

            public Builder setTypeData(TypeData typeData) {
                this.typeData = typeData;
                return this;
            }

            public Builder setVariableElement(VariableElement variableElement) {
                this.variableElement = variableElement;
                return this;
            }

            public Builder setTypeMirror(TypeMirror typeMirror) {
                this.typeMirror = typeMirror;
                return this;
            }

            public Builder setMethodsPair(MethodsPair methodsPair) {
                this.methodsPair = methodsPair;
                return this;
            }

            public TypeData getTypeData() {
                return typeData;
            }

            public VariableElement getVariableElement() {
                return variableElement;
            }

            public TypeMirror getTypeMirror() {
                return typeMirror;
            }

            public MethodsPair getMethodsPair() {
                return methodsPair;
            }

            public FieldData build() {
                return new FieldData(typeData, variableElement, typeMirror, methodsPair);
            }

        }

    }

    public static class MethodsPair {

        final ExecutableElement getterElement;

        final ExecutableType getterType;

        final ExecutableElement setterElement;

        final ExecutableType setterType;

        public MethodsPair(ExecutableElement getterElement,
                           ExecutableType getterType,
                           ExecutableElement setterElement,
                           ExecutableType setterType) {
            this.getterElement = getterElement;
            this.getterType = getterType;
            this.setterElement = setterElement;
            this.setterType = setterType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            MethodsPair that = (MethodsPair) o;

            if (getterElement != null
                ? !getterElement.equals(that.getterElement)
                : that.getterElement != null) {
                return false;
            }
            if (getterType != null
                ? !getterType.equals(that.getterType)
                : that.getterType != null) {
                return false;
            }
            if (setterElement != null
                ? !setterElement.equals(that.setterElement)
                : that.setterElement != null) {
                return false;
            }
            return setterType != null
                   ? setterType.equals(that.setterType)
                   : that.setterType == null;

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

    public static class StatementData {

        final MethodSpec.Builder saveSpecBuilder;

        final MethodSpec.Builder restoreSpecBuilder;

        final FieldData fieldData;

        final String stateVariableName;

        final String saveStateName;

        final String restoreStateName;

        final String bundlePutMethod;

        final String bundleGetMethod;

        final String reflectionSetMethod;

        final String reflectionGetMethod;

        final boolean asSerializable;

        public StatementData(MethodSpec.Builder saveSpecBuilder,
                             MethodSpec.Builder restoreSpecBuilder,
                             FieldData fieldData,
                             String stateVariableName,
                             String saveStateName,
                             String restoreStateName,
                             String bundlePutMethod,
                             String bundleGetMethod,
                             String reflectionSetMethod,
                             String reflectionGetMethod, boolean asSerializable) {
            this.saveSpecBuilder = saveSpecBuilder;
            this.restoreSpecBuilder = restoreSpecBuilder;
            this.fieldData = fieldData;
            this.stateVariableName = stateVariableName;
            this.saveStateName = saveStateName;
            this.restoreStateName = restoreStateName;
            this.bundlePutMethod = bundlePutMethod;
            this.bundleGetMethod = bundleGetMethod;
            this.reflectionSetMethod = reflectionSetMethod;
            this.reflectionGetMethod = reflectionGetMethod;
            this.asSerializable = asSerializable;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof StatementData)) {
                return false;
            }
            StatementData that = (StatementData) o;
            return asSerializable == that.asSerializable &&
                   Objects.equals(saveSpecBuilder, that.saveSpecBuilder) &&
                   Objects.equals(restoreSpecBuilder, that.restoreSpecBuilder) &&
                   Objects.equals(fieldData, that.fieldData) &&
                   Objects.equals(stateVariableName, that.stateVariableName) &&
                   Objects.equals(saveStateName, that.saveStateName) &&
                   Objects.equals(restoreStateName, that.restoreStateName) &&
                   Objects.equals(bundlePutMethod, that.bundlePutMethod) &&
                   Objects.equals(bundleGetMethod, that.bundleGetMethod) &&
                   Objects.equals(reflectionSetMethod, that.reflectionSetMethod) &&
                   Objects.equals(reflectionGetMethod, that.reflectionGetMethod);
        }

        @Override
        public int hashCode() {

            return Objects.hash(saveSpecBuilder,
                                restoreSpecBuilder,
                                fieldData,
                                stateVariableName,
                                saveStateName,
                                restoreStateName,
                                bundlePutMethod,
                                bundleGetMethod,
                                reflectionSetMethod,
                                reflectionGetMethod,
                                asSerializable);
        }

        public static class Builder {

            private MethodSpec.Builder saveSpecBuilder;

            private MethodSpec.Builder restoreSpecBuilder;

            private FieldData fieldData;

            private String stateVariableName;

            private String saveStateName;

            private String restoreStateName;

            private String bundlePutMethod;

            private String bundleGetMethod;

            private String reflectionSetMethod;

            private String reflectionGetMethod;

            private boolean asSerializable;

            public Builder setSaveSpecBuilder(MethodSpec.Builder saveSpecBuilder) {
                this.saveSpecBuilder = saveSpecBuilder;
                return this;
            }

            public Builder setRestoreSpecBuilder(MethodSpec.Builder restoreSpecBuilder) {
                this.restoreSpecBuilder = restoreSpecBuilder;
                return this;
            }

            public Builder setFieldData(FieldData fieldData) {
                this.fieldData = fieldData;
                return this;
            }

            public Builder setStateVariableName(String stateVariableName) {
                this.stateVariableName = stateVariableName;
                return this;
            }

            public Builder setSaveStateName(String saveStateName) {
                this.saveStateName = saveStateName;
                return this;
            }

            public Builder setRestoreStateName(String restoreStateName) {
                this.restoreStateName = restoreStateName;
                return this;
            }

            public Builder setBundlePutMethod(String bundlePutMethod) {
                this.bundlePutMethod = bundlePutMethod;
                return this;
            }

            public Builder setBundleGetMethod(String bundleGetMethod) {
                this.bundleGetMethod = bundleGetMethod;
                return this;
            }

            public Builder setReflectionSetMethod(String reflectionSetMethod) {
                this.reflectionSetMethod = reflectionSetMethod;
                return this;
            }

            public Builder setReflectionGetMethod(String reflectionGetMethod) {
                this.reflectionGetMethod = reflectionGetMethod;
                return this;
            }

            public Builder setAsSerializable(boolean asSerializable) {
                this.asSerializable = asSerializable;
                return this;
            }

            public StatementData build() {
                return new StatementData(saveSpecBuilder,
                                         restoreSpecBuilder,
                                         fieldData,
                                         stateVariableName,
                                         saveStateName,
                                         restoreStateName,
                                         bundlePutMethod,
                                         bundleGetMethod,
                                         reflectionSetMethod,
                                         reflectionGetMethod,
                                         asSerializable
                );
            }

        }

    }

}
