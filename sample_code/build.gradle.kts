plugins {
    application
    kotlin("jvm") version "1.6.10"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.lisa-analyzer:lisa-core:0.1b7")
    implementation("io.github.lisa-analyzer:lisa-sdk:0.1b9")
    implementation("io.github.lisa-analyzer:lisa-imp:0.1b9")
    implementation("io.github.lisa-analyzer:lisa-program:0.1b9")
    implementation ("com.github.javaparser:javaparser-symbol-solver-core:3.25.8")
    implementation ("com.github.javaparser:javaparser-core:3.15.13")
    implementation(kotlin("stdlib"))
}

application {
    mainClass.set("test.app.App")
}