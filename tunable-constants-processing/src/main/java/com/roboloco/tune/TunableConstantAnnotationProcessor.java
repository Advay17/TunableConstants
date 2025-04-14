package com.roboloco.tune;

import com.squareup.javapoet.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

public class TunableConstantAnnotationProcessor extends AbstractProcessor {
  private static String getPackageName(Element e) {
    while (e != null) {
      if (e.getKind().equals(ElementKind.PACKAGE)) {
        return ((PackageElement) e).getQualifiedName().toString();
      }
      e = e.getEnclosingElement();
    }

    return null;
  }

  private static final TypeName TUNABLE_CONSTANT_TYPE =
      ClassName.get("com.roboloco.tune", "TunableConstants");
  private static final ClassName PREFERENCES_CLASS =
      ClassName.get("edu.wpi.first.wpilibj", "Preferences");
  private static final TypeName TUNABLE_LIST_CLASS =
      ParameterizedTypeName.get(
          ClassName.get(LinkedList.class), ClassName.get("com.roboloco.tune.tunable", "Tunable"));
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
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    Optional<? extends TypeElement> annotationOptional =
        annotations.stream()
            .filter((te) -> te.getSimpleName().toString().equals("IsTunableConstants"))
            .findFirst();
    if (!annotationOptional.isPresent()) {
      return false;
    }
    TypeElement annotation = annotationOptional.get();
    roundEnv
        .getElementsAnnotatedWith(annotation)
        .forEach(
            classElement -> {
              String tunableConstantsClassName = "Tunable" + classElement.getSimpleName();
              String tuanbleConstantsPackage = getPackageName(classElement);

              MethodSpec.Builder constructorBuilder =
                  MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);
              constructorBuilder.addStatement(
                  "$T TUNABLES = new $T()", TUNABLE_LIST_CLASS, TUNABLE_LIST_CLASS);
              MethodSpec.Builder reloadBuilder =
                  MethodSpec.methodBuilder("reload")
                      .addAnnotation(Override.class)
                      .addModifiers(Modifier.PUBLIC);
              Types typeUtil = processingEnv.getTypeUtils();
              Elements elementUtil = processingEnv.getElementUtils();
              TypeElement typeElement = (TypeElement) classElement;
              boolean isSuperClass = false;
              while (typeElement != null) {
                final boolean finalIsSuperclass = isSuperClass;
                typeElement.getEnclosedElements().stream()
                    .filter(f -> f.getKind().equals(ElementKind.FIELD))
                    .forEach(
                        fieldElement -> {
                          if (finalIsSuperclass
                                  && fieldElement.getModifiers().contains(Modifier.PRIVATE)
                              || fieldElement.getModifiers().contains(Modifier.FINAL)) return;

                          String simpleName = fieldElement.getSimpleName().toString();
                          String fieldType = fieldElement.asType().toString();

                          if (TUNABLE_TYPES.contains(fieldType)) {
                            String preferencesName =
                                fieldType.substring(0, 1).toUpperCase() + fieldType.substring(1);
                            constructorBuilder.addStatement(
                                "$T.init"
                                    + preferencesName
                                    + "(\""
                                    + classElement.getSimpleName()
                                    + "/"
                                    + simpleName
                                    + "\","
                                    + simpleName
                                    + ")",
                                PREFERENCES_CLASS);
                            reloadBuilder.addStatement(
                                simpleName
                                    + " = $T.get"
                                    + preferencesName
                                    + "(\""
                                    + classElement.getSimpleName()
                                    + "/"
                                    + simpleName
                                    + "\","
                                    + simpleName
                                    + ")",
                                PREFERENCES_CLASS);
                          } else if ((elementUtil.getTypeElement(
                                      "com.roboloco.tune.tunable.Tunable" + fieldType)
                                  != null
                              && typeUtil.isAssignable(
                                  elementUtil
                                      .getTypeElement(
                                          "com.roboloco.tune.tunable.Tunable" + fieldType)
                                      .asType(),
                                  elementUtil
                                      .getTypeElement("com.roboloco.tune.tunable.Tunable")
                                      .asType()))) {
                            constructorBuilder.addStatement(
                                "TUNABLES.add(new $T($S, $S))",
                                ClassName.get(
                                    "com.roboloco.tune.tunable",
                                    "Tunable" + fieldType,
                                    simpleName,
                                    classElement.getSimpleName() + "/" + simpleName + "/"));
                          } else if ((elementUtil.getTypeElement(
                                      "frc.robot.util.tunable.Tunable" + fieldType)
                                  != null
                              && typeUtil.isAssignable(
                                  elementUtil
                                      .getTypeElement("frc.robot.util.tunable.Tunable" + fieldType)
                                      .asType(),
                                  elementUtil
                                      .getTypeElement("com.roboloco.tune.tunable.Tunable")
                                      .asType()))) {
                            constructorBuilder.addStatement(
                                "TUNABLES.add(new $T($S, $S))",
                                ClassName.get(
                                    "frc.robot.util.tunable.tunable",
                                    "Tunable" + fieldType,
                                    simpleName,
                                    classElement.getSimpleName() + "/" + simpleName + "/"));
                          } else {
                            return;
                          }
                        });
                TypeMirror mirror = (typeElement).getSuperclass();
                if (mirror.getKind() == TypeKind.DECLARED) {
                  typeElement = (TypeElement) typeUtil.asElement(mirror);
                  isSuperClass = true;
                } else typeElement = null;
              }
              reloadBuilder.addStatement("TUNABLES.forEach(Tunable::reload)");
              TypeSpec type =
                  TypeSpec.classBuilder(tunableConstantsClassName)
                      .addModifiers(Modifier.PUBLIC)
                      .addAnnotation(
                          AnnotationSpec.builder(SuppressWarnings.class)
                              .addMember("value", "\"unchecked\"")
                              .build())
                      .addField(TUNABLE_LIST_CLASS, "TUNABLES", Modifier.PRIVATE)
                      .addSuperinterface(TUNABLE_CONSTANT_TYPE)
                      .superclass(classElement.asType())
                      .addMethod(constructorBuilder.build())
                      .addMethod(reloadBuilder.build())
                      .build();
              JavaFile file = JavaFile.builder(tuanbleConstantsPackage, type).build();
              try {
                file.writeTo(processingEnv.getFiler());
              } catch (IOException e) {
                processingEnv
                    .getMessager()
                    .printMessage(Diagnostic.Kind.ERROR, "Failed to write class", classElement);
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
}
