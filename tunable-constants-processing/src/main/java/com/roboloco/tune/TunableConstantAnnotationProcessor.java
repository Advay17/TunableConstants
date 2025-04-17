package com.roboloco.tune;

import com.squareup.javapoet.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

public class TunableConstantAnnotationProcessor extends AbstractProcessor {

	private static final TypeName TUNABLE_CONSTANT_TYPE = ClassName.get("com.roboloco.tune", "TunableConstants");
	private static final ClassName PREFERENCES_CLASS = ClassName.get("edu.wpi.first.wpilibj", "Preferences");
	private static final TypeName TUNABLE_LIST_CLASS = ParameterizedTypeName.get(ClassName.get(LinkedList.class),
			ClassName.get("com.roboloco.tune.tunable", "Tunable"));
	private static final HashSet<String> BASE_TUNABLE_TYPES = new HashSet<String>() {
		{
			add("boolean");
			add("double");
			add("float");
			add("int");
			add("long");
			add("java.lang.String");
		}
	};
	private static final LinkedList<String> TUNABLE_PACKAGES = new LinkedList<>() {
		{
			add("frc.robot.util.tunable");
			add("com.roboloco.tune.tunable");
		}
	};

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
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
			constructorBuilder.addStatement("TUNABLES = new $T()", TUNABLE_LIST_CLASS);
			MethodSpec.Builder reloadBuilder = MethodSpec.methodBuilder("reload").addAnnotation(Override.class)
					.addModifiers(Modifier.PUBLIC);
			Types typeUtil = processingEnv.getTypeUtils();
			Elements elementUtil = processingEnv.getElementUtils();
			TypeMirror tunableTypeMirror = typeUtil
					.erasure(elementUtil.getTypeElement("com.roboloco.tune.tunable.Tunable").asType());
			TypeElement typeElement = (TypeElement) classElement;
			boolean isSuperClass = false;
			while (typeElement != null) {
				final boolean finalIsSuperclass = isSuperClass;
				typeElement.getEnclosedElements().stream().filter(f -> f.getKind().equals(ElementKind.FIELD))
						.forEach(fieldElement -> {
							if (finalIsSuperclass && fieldElement.getModifiers().contains(Modifier.PRIVATE)
									|| fieldElement.getModifiers().contains(Modifier.FINAL))
								return;
							String simpleName = fieldElement.getSimpleName().toString();
							String fieldType = fieldElement.asType().toString();
							if (BASE_TUNABLE_TYPES.contains(fieldType)) {
								String preferencesName = fieldType.substring(fieldType.lastIndexOf('.') + 1)
										.substring(0, 1).toUpperCase() + fieldType.substring(1);
								constructorBuilder.addStatement("$T.init$L($S, $L)", PREFERENCES_CLASS, preferencesName,
										classElement.getSimpleName() + "/" + simpleName, simpleName);
								reloadBuilder.addStatement("$L = $T.get$L($S, $L)", simpleName, PREFERENCES_CLASS,
										preferencesName, classElement.getSimpleName() + "/" + simpleName, simpleName);
								return;
							}
							for (String tunablePackage : TUNABLE_PACKAGES)
								if (elementUtil.getPackageElement(tunablePackage) != null)
									elementUtil.getPackageElement(tunablePackage).getEnclosedElements()
											.forEach(tunable -> {
												if (typeUtil.isSubtype(tunable.asType(), tunableTypeMirror)
														&& ((DeclaredType) (typeUtil.directSupertypes(tunable.asType())
																.get(0))).getTypeArguments().size() > 0
														&& typeUtil.isSameType(
																((DeclaredType) (typeUtil
																		.directSupertypes(tunable.asType()).get(0)))
																				.getTypeArguments().get(0),
																fieldElement.asType())) {
													constructorBuilder.addStatement("TUNABLES.add(new $T($L, $S))",
															ClassName.get(tunable.asType()), simpleName,
															classElement.getSimpleName() + "/" + simpleName + "/");
													return;
												}
											});

						});
				TypeMirror mirror = (typeElement).getSuperclass();
				if (mirror.getKind() == TypeKind.DECLARED) {
					typeElement = (TypeElement) typeUtil.asElement(mirror);
					isSuperClass = true;
				} else
					typeElement = null;
			}
			reloadBuilder.addStatement("TUNABLES.forEach(Tunable::reload)");
			TypeSpec type = TypeSpec.classBuilder(tunableConstantsClassName).addModifiers(Modifier.PUBLIC)
					.addAnnotation(
							AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "\"rawtypes\"").build())
					.addField(TUNABLE_LIST_CLASS, "TUNABLES", Modifier.PRIVATE).addSuperinterface(TUNABLE_CONSTANT_TYPE)
					.superclass(classElement.asType()).addMethod(constructorBuilder.build())
					.addMethod(reloadBuilder.build()).build();
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

	private static String getPackageName(Element e) {
		while (e != null) {
			if (e.getKind().equals(ElementKind.PACKAGE)) {
				return ((PackageElement) e).getQualifiedName().toString();
			}
			e = e.getEnclosingElement();
		}

		return null;
	}
}
