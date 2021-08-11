import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
  mavenCentral()
}

plugins {
  kotlin("jvm")
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    jvmTarget = "11"
  }
}

dependencies {
  implementation("com.amazonaws:aws-lambda-java-core:1.2.1")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${rootProject.properties["jacksonVersion"]}")
  implementation("software.amazon.awssdk:url-connection-client:2.10.78")
  implementation(project(":services:plugin-blockmap-patches"))
  runtimeOnly("org.apache.logging.log4j:log4j-slf4j18-impl:2.13.0")
  runtimeOnly("com.amazonaws:aws-lambda-java-log4j2:1.2.0")
}


buildDir = File(rootProject.projectDir, "build/plugin-blockmap-lambda")

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
