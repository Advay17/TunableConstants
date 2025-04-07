plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.squareup:javapoet:1.13.0")
    implementation("org.reflections:reflections:0.10.2")
}