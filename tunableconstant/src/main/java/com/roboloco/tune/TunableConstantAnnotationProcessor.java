package com.roboloco.tune;

import com.squareup.javapoet.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class TunableConstantAnnotationProcessor extends AbstractProcessor{
    private static String getPackageName(Element e) {
        while (e != null) {
          if (e.getKind().equals(ElementKind.PACKAGE)) {
            return ((PackageElement) e).getQualifiedName().toString();
          }
          e = e.getEnclosingElement();
        }
    
        return null;
      }
    private static final TypeName TUNABLE_CONSTANT_TYPE = ClassName.get("com.roboloco.tune",
    "TunableConstant");
    private static final HashSet<String> TUNABLE_TYPES = new HashMap<>(){{
        put("boolean");
        put("double");
        put("float");
        put("int");
        put("long");
        put("java.lang.String");
    }};
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv){
        Optional<? extends TypeElement> annotationOptional = annotations.stream()
        .filter((te) -> te.getSimpleName().toString().equals("IsTunableConstant")).findFirst();
        if (!annotationOptional.isPresent()) {
            return false;
        }
        TypeElement annotation = annotationOptional.get();
        roundEnv.getElementsAnnotatedWith(annotation).forEach(classElement -> {
            String tunableConstantsClassName = "Tunable" + classElement.getSimpleName();
            String tuanbleConstantsPackage = getPackageName(classElement);

            MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);
            MethodSpec.Builder reloadBuilder = MethodSpec.methodBuilder("reload").addAnnotation(Override.class).addModifiers(Modifier.PUBLIC);
            Types util = processingEnv.getTypeUtils();
            TypeElement typeElement = (TypeElement) classElement;
            boolean isSuperClass = false;
            while(typeElement!=null){
                final TypeElement finalTypeElement = typeElement;
                final boolean finalIsSuperclass = isSuperclass;
                typeElement.getEnclosedElements().stream().filter(f -> f.getKind().equals(ElementKind.FIELD)).forEach(fieldElement -> {
                    if(finalIsSuperclass && fieldElement.getModifiers().contains(Modifier.PRIVATE)) return;

                    String simpleName = fieldElement.getSimpleName().toString();
                    String fieldType=fieldElement.asType().toString();
                    
                    if(!LOGGABLE_TYPES.contains(fieldType)){
                        throw new RuntimeException("[IsTunableConstant] Type \"" + simpleName + "\" from \"" + finalTypeElement.getSimpleName() +"\" is not loggable.");
                    }
                    String preferencesName = (fieldType.contains("String"))? "String": fieldType.substring(0, 1).toUpperCase() + fieldType.substring(1);
                    Object defaultFieldValue = getFieldValue(classElement, util);
                    constructorBuilder.addCode("Preferences.init" + preferencesName + "(" + simpleName +"," + defaultFieldValue + ");\n");
                    reloadBuilder.addCode(simpleName + " = Preferences.get" + preferencesName + "(" + simpleName +"," + defaultFieldValue + ");\n");

                });
                TypeMirror mirror = (typeElement).getSuperclass();
                if(mirror.getKind() == TypeKind.DECLARED){
                    typeElement = (TypeElement) util.asElement(mirror);
                    isSuperClass=true;
                }
                else typeElement=null;
            }
            TypeSpec type = TypeSpec.classBuilder(tunableConstantsClassName).addModifiers(Modifier.PUBLIC).addSuperinterface(TUNABLE_CONSTANT_TYPE).superclass(classElement.asType()).addMethod(constructorBuilder.build()).addMethod(reloadBuilder.build()).build();
            JavaFile file = JavaFile.builder(tuanbleConstantsPackage, type).build();
            try {
                file.writeTo(processingEnv.getFiler());
              } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to write class", classElement);
                e.printStackTrace();
              }
        });
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
      return SourceVersion.latestSupported();
    }
  
    @Override
    public Set<String> getSupportedAnnotationTypes() {
      return Set.of("com.roboloco.tune.IsTunableConstant");
    }

    public static Object getFieldValue(Element element, Elements elementUtils) {
        if (!(element instanceof VariableElement)) {
            throw new IllegalArgumentException("Element is not a field.");
        }
        
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        String className = elementUtils.getBinaryName(enclosingElement).toString();
        String fieldName = element.getSimpleName().toString();

        try {
            Class<?> clazz = Class.forName(className);
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);

            return field.get(null);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
