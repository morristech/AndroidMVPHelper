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

package com.ufkoku.mvp.view.wrap;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
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
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes({"com.ufkoku.mvp.view.wrap.Wrap"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ViewWrapAnnotationProcessor extends AbstractProcessor {

    private static final String WRAP_SUFFIX = "Wrap";

    private static final String FIELD_NAME_WRAPPED = "wrappedView";

    private static final String FIELD_NAME_MAIN_HANDLER = "mainHandler";

    private static final String CLASS_NAME_HANDLER = "android.os.Handler";
    private static final String CLASS_NAME_LOOPER = "android.os.Looper";

    private TypeElement typElementHandler;
    private TypeElement typeElementLooper;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        typElementHandler = processingEnv.getElementUtils().getTypeElement(CLASS_NAME_HANDLER);
        typeElementLooper = processingEnv.getElementUtils().getTypeElement(CLASS_NAME_LOOPER);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        try {
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith((Class<? extends Annotation>) Class.forName("com.ufkoku.mvp.view.wrap.Wrap"));
            if (elements.size() > 0) {
                for (Element element : elements) {
                    if (element.getKind() == ElementKind.INTERFACE) {
                        TypeElement typeElement = (TypeElement) element;
                        createWrap(typeElement);
                    } else {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Wrap annotation can be applied only on interfaces");
                    }
                }
            }

            return true;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void createWrap(TypeElement wrappedElement) throws IOException {
        DeclaredType wrappedDeclaredType = createDeclaredType(wrappedElement);

        FieldSpec wrappedFieldSpec = FieldSpec.builder(
                TypeName.get(wrappedElement.asType()),
                FIELD_NAME_WRAPPED,
                Modifier.PRIVATE, Modifier.FINAL).build();

        FieldSpec mainHandlerFieldSpec = FieldSpec.builder(
                TypeName.get(typElementHandler.asType()),
                FIELD_NAME_MAIN_HANDLER,
                Modifier.PRIVATE, Modifier.FINAL)
                .initializer("new Handler(($T.getMainLooper()))", typeElementLooper.asType())
                .build();

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(wrappedElement.asType()), FIELD_NAME_WRAPPED)
                .addStatement("this.$L = $L", FIELD_NAME_WRAPPED, FIELD_NAME_WRAPPED)
                .build();

        List<MethodSpec> methodSpecs = createMethodWrapsForDeclaredType(wrappedElement, wrappedDeclaredType);

        List<TypeVariableName> typeVariableNames = convertTypeParametersToTypeVariableNames(wrappedElement.getTypeParameters());

        TypeSpec typeSpec = TypeSpec.classBuilder(wrappedElement.getSimpleName() + WRAP_SUFFIX)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addTypeVariables(typeVariableNames)
                .addSuperinterface(TypeName.get(wrappedElement.asType()))
                .addField(wrappedFieldSpec)
                .addField(mainHandlerFieldSpec)
                .addMethod(constructor)
                .addMethods(methodSpecs)
                .build();

        JavaFile file = JavaFile.builder(processingEnv.getElementUtils().getPackageOf(wrappedElement).getQualifiedName().toString(), typeSpec).build();
        file.writeTo(processingEnv.getFiler());
    }

    private List<MethodSpec> createMethodWrapsForDeclaredType(TypeElement typeElement, DeclaredType declaredType) {
        List<MethodSpec> methodSpecs = new ArrayList<>();

        for (Element element : typeElement.getEnclosedElements()) {
            if (element.getKind() == ElementKind.METHOD) {
                methodSpecs.add(createMethodWrap(
                        (ExecutableElement) element,
                        (ExecutableType) processingEnv.getTypeUtils().asMemberOf(declaredType, element)
                ));
            }
        }

        for (TypeMirror mirror : processingEnv.getTypeUtils().directSupertypes(declaredType)) {
            TypeElement element = (TypeElement) processingEnv.getTypeUtils().asElement(mirror);
            if (element.getKind() == ElementKind.INTERFACE) {
                methodSpecs.addAll(createMethodWrapsForDeclaredType(
                        (TypeElement) processingEnv.getTypeUtils().asElement(mirror),
                        (DeclaredType) mirror
                ));
            }
        }

        return methodSpecs;
    }

    private MethodSpec createMethodWrap(ExecutableElement executableElement, ExecutableType executableType) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(executableElement.getSimpleName().toString());

        builder.addModifiers(Modifier.PUBLIC);
        builder.addTypeVariables(convertTypeVariablesToTypeVariableNames(executableType.getTypeVariables()));
        builder.returns(TypeName.get(executableType.getReturnType()));
        for (int i = 0; i < executableElement.getParameters().size(); i++) {
            builder.addParameter(
                    TypeName.get(executableType.getParameterTypes().get(i)),
                    executableElement.getParameters().get(i).getSimpleName().toString(),
                    Modifier.FINAL
            );
        }
        builder.addExceptions(convertTypeMirrorsToTypeNames(executableType.getThrownTypes()));
        builder.addCode(createMainThreadMethodBody(executableElement, executableType));

        return builder.build();
    }

    private CodeBlock createMainThreadMethodBody(ExecutableElement executableElement, ExecutableType executableType) {
        TypeMirror returnType = executableType.getReturnType();
        String returnTypeString = returnType.toString();
        boolean returnTypeIsVoid = returnTypeString.equals("void") || returnTypeString.equals(Void.class.getName());

        String callString;
        {
            StringBuilder callBuilder = new StringBuilder();
            callBuilder
                    .append(FIELD_NAME_WRAPPED)
                    .append(".")
                    .append(executableElement.getSimpleName())
                    .append("(");
            for (int i = 0; i < executableElement.getParameters().size(); i++) {
                callBuilder.append(executableElement.getParameters().get(i).getSimpleName());
                if (i != executableElement.getParameters().size() - 1) {
                    callBuilder.append(",");
                }
            }
            callBuilder.append(")");
            callString = callBuilder.toString();
        }

        CodeBlock.Builder codeBuilder = CodeBlock.builder();
        codeBuilder.beginControlFlow("if ($T.myLooper() == $T.getMainLooper())", typeElementLooper, typeElementLooper);
        {
            codeBuilder.addStatement((returnTypeIsVoid ? "" : "return ") + callString);
        }
        codeBuilder.nextControlFlow(" else ");
        {
            codeBuilder.addStatement("final $T lockObject = new $T()", Object.class, Object.class);
            if (!returnTypeIsVoid) {
                if (returnType instanceof PrimitiveType) {
                    codeBuilder.addStatement("final $T[] returnValueArray = new $T[1]", returnType, returnType);
                } else {
                    codeBuilder.addStatement("final $T[] returnValueArray = new $T[1]", Object.class, Object.class);
                }
            }
            codeBuilder.add("$L.post(new $T() {\n", FIELD_NAME_MAIN_HANDLER, Runnable.class).indent();
            {
                codeBuilder.add("@$T\n", Override.class);
                codeBuilder.add("public void run() {\n").indent();
                {
                    if (returnTypeIsVoid) {
                        codeBuilder.addStatement(callString);
                    } else {
                        codeBuilder.addStatement("returnValueArray[0] = $L", callString);
                    }
                    codeBuilder.add("synchronized(lockObject) {\n").indent();
                    {
                        codeBuilder.addStatement("lockObject.notify()");
                    }
                    codeBuilder.unindent().add("}");
                }
                codeBuilder.unindent().add("}\n");
            }
            codeBuilder.unindent().addStatement("})");
            codeBuilder.add("synchronized(lockObject) {\n").indent();
            {
                codeBuilder.add("try {\n").indent();
                {
                    codeBuilder.addStatement("lockObject.wait()");
                }
                codeBuilder.unindent().add("} catch ($T ex) {\n", InterruptedException.class).indent();
                {
                    codeBuilder.addStatement("ex.printStackTrace()");
                    codeBuilder.addStatement("throw new $T(ex)", RuntimeException.class);
                }
                codeBuilder.unindent().add("}");
            }
            codeBuilder.unindent().add("}\n");
            if (!returnTypeIsVoid) {
                codeBuilder.addStatement("return ($T) returnValueArray[0]", returnType);
            }
            codeBuilder.endControlFlow();
        }

        return codeBuilder.build();
    }

    //--------------------------------Util methods----------------------------------------------//

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

}
