package com.roboloco.tune;

import com.squareup.javapoet.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.HashSet;
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
    "TunableConstants");
    private static final ClassName PREFERENCES_CLASS = ClassName.get("edu.wpi.first.wpilibj", "Preferences");
    private static final HashSet<String> TUNABLE_TYPES = new HashSet<String>();
    static {
        TUNABLE_TYPES.add("boolean");
        TUNABLE_TYPES.add("double");
        TUNABLE_TYPES.add("float");
        TUNABLE_TYPES.add("int");
        TUNABLE_TYPES.add("long");
        TUNABLE_TYPES.add("java.lang.String");
    }
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv){
        Optional<? extends TypeElement> annotationOptional = annotations.stream()
        .filter((te) -> te.getSimpleName().toString().equals("IsTunableConstants")).findFirst();
        if (!annotationOptional.isPresent()) {
            return false;
        }
        TypeElement annotation = annotationOptional.get();
        roundEnv.getElementsAnnotatedWith(annotation).forEach(classElement -> {
            String tunableConstantsClassName = "Tunable" + classElement.getSimpleName();
            String tuanbleConstantsPackage = getPackageName(classElement);

            MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);
            MethodSpec.Builder reloadBuilder = MethodSpec.methodBuilder("reload").addAnnotation(Override.class).addModifiers(Modifier.PUBLIC);
            Types typeUtil = processingEnv.getTypeUtils();
            Elements elementUtil = processingEnv.getElementUtils();
            TypeElement typeElement = (TypeElement) classElement;
            boolean isSuperClass = false;
            while(typeElement!=null){
                final boolean finalIsSuperclass = isSuperClass;
                typeElement.getEnclosedElements().stream().filter(f -> f.getKind().equals(ElementKind.FIELD)).forEach(fieldElement -> {
                    if(finalIsSuperclass && fieldElement.getModifiers().contains(Modifier.PRIVATE) || fieldElement.getModifiers().contains(Modifier.FINAL)) return;

                    String simpleName = fieldElement.getSimpleName().toString();
                    String fieldType=fieldElement.asType().toString();
                    
                    if(TUNABLE_TYPES.contains(fieldType)){
                      String preferencesName = fieldType.substring(0, 1).toUpperCase() + fieldType.substring(1);
                      constructorBuilder.addStatement("$T.init" + preferencesName + "(\"" + classElement.getSimpleName() + "/" + simpleName +"\"," + simpleName + ")", PREFERENCES_CLASS);
                      reloadBuilder.addStatement(simpleName + " = $T.get" + preferencesName + "(\"" + classElement.getSimpleName() + "/" + simpleName +"\"," + simpleName + ")", PREFERENCES_CLASS);
                    }
                    else if (classExists("Tunable" + fieldType) && typeUtil.isAssignable(elementUtil.getTypeElement("Tunable" + fieldType).asType(), elementUtil.getTypeElement("com.roboloco.tune.Tunable").asType())){
                      constructorBuilder.addStatement("$S=new Tunable", simpleName);
                    }
                    else{
                        return;
                    }

                });
                TypeMirror mirror = (typeElement).getSuperclass();
                if(mirror.getKind() == TypeKind.DECLARED){
                    typeElement = (TypeElement) typeUtil.asElement(mirror);
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
      return Set.of("com.roboloco.tune.IsTunableConstants");
    }

    public static boolean classExists(String classPath){
        try {
            Class.forName(classPath);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
