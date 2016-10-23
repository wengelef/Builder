package com.example.processor;

import com.example.Builder;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

public class BuilderProcessor extends AbstractProcessor {

    private Messager messager;
    private Filer filer;
    private Types typeUtils;

    public BuilderProcessor() {
        super();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        typeUtils = processingEnv.getTypeUtils();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(Builder.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        // Iterate Elements annotated with @Builder
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Builder.class)) {
            if (annotatedElement.getKind() != ElementKind.CLASS) {
                onError(annotatedElement, "Only classes can be annotated with @Builder");
                return true;
            }

            TypeElement typeElement = (TypeElement) annotatedElement;

            if (!typeElement.getModifiers().contains(Modifier.PUBLIC)) {
                onError(typeElement, "Class is not public");
                return true;
            }

            if (typeElement.getModifiers().contains(Modifier.ABSTRACT)) {
                onError(typeElement, "Class must not be abstract");
                return true;
            }

            String builderName = String.format("%sBuilder", typeElement.getSimpleName());
            ClassName builderType = ClassName.get(typeElement.getEnclosingElement().toString(), builderName);

            List<FieldSpec> fieldSpecs = new ArrayList<>();
            List<MethodSpec> methodSpecs = new ArrayList<>();

            for (VariableElement variableElement : ElementFilter.fieldsIn(annotatedElement.getEnclosedElements())) {
                String fieldname = variableElement.getSimpleName().toString();

                FieldSpec fieldSpec = FieldSpec.builder(
                            TypeName.get(variableElement.asType()),
                            fieldname,
                            Modifier.PRIVATE)
                        .build();

                MethodSpec methodSpec = MethodSpec.methodBuilder(fieldname)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(builderType)
                        .addParameter(TypeName.get(variableElement.asType()), fieldname)
                        .addStatement("this.$1N = $1N", fieldname)
                        .addStatement("return this")
                        .build();

                fieldSpecs.add(fieldSpec);
                methodSpecs.add(methodSpec);
            }

            String targetName = typeElement.getSimpleName().toString().toLowerCase();

            MethodSpec.Builder builder = MethodSpec.methodBuilder("build")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.get(typeElement.asType()))
                    .addStatement("$1T $2N = new $1T()", TypeName.get(typeElement.asType()), targetName);

            for (FieldSpec field : fieldSpecs) {
                builder.addStatement("$1N.$2N = this.$2N", targetName, field);
            }

            builder.addStatement("return $N", targetName);

            methodSpecs.add(builder.build());

            TypeSpec typeSpec = TypeSpec.classBuilder(builderType)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addFields(fieldSpecs)
                    .addMethods(methodSpecs)
                    .build();

            JavaFile file = JavaFile.builder(builderType.packageName(), typeSpec).build();

            try {
                file.writeTo(filer);
            } catch (IOException e) {
                onError(annotatedElement, "Error writing File");
            }
        }
        return true;
    }

    private void onError(Element element, String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message);
    }

    private void debug(String message) {
        messager.printMessage(Diagnostic.Kind.NOTE, message);
    }
}
