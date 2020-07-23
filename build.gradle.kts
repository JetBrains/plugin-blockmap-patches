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
  api("software.amazon.awssdk:s3:2.10.78")
  testImplementation("junit:junit:4.12")
  testImplementation(project(":services:plugin-blockmap-patches"))
  implementation(project(":services:plugin-blockmap-patches:blockmap"))
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${rootProject.properties["jacksonVersion"]}")
}

