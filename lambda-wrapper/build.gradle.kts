import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
  mavenCentral()
}

plugins {
  kotlin("jvm")
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    jvmTarget = "1.8"
  }
}

dependencies {
  implementation("com.amazonaws:aws-lambda-java-log4j:1.0.0")
  implementation("com.amazonaws:aws-lambda-java-core:1.2.0")
  implementation("org.slf4j:slf4j-log4j12:1.7.30")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${rootProject.properties["jacksonVersion"]}")
  implementation("software.amazon.awssdk:url-connection-client:2.10.78")
  implementation(project(":services:plugin-blockmap-patches"))
}


buildDir = File(rootProject.projectDir, "build/plugin-blockmap-patches")

tasks.register<Zip>("buildPreviewZip") {
  from(project.tasks["compileKotlin"])
  from(project.tasks["processResources"])
  archiveName = "plugin-blockmap-patches.zip"
  into("lib") {
    from(configurations.runtimeClasspath)
  }
}

tasks.named("assemble") {
  dependsOn("buildPreviewZip")
}
