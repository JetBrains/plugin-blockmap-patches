import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
  mavenCentral()
}

plugins {
  kotlin("jvm")
}

tasks.withType<KotlinCompile> {
  kotlinOptions{
    jvmTarget = "1.8"
  }
}

dependencies {
  api("software.amazon.awssdk:s3:2.10.78")
  api("com.fasterxml.jackson.core:jackson-annotations:${rootProject.ext["jacksonVersion"]}")
  api(project(":common"))

  implementation("org.slf4j:jcl-over-slf4j:1.7.30")
  implementation("org.jetbrains.intellij.plugins:structure-intellij:${rootProject.ext["structureLibVersion"]}")
  implementation("org.jetbrains.intellij.plugins:structure-hub:${rootProject.ext["structureLibVersion"]}")
  implementation("org.jetbrains.intellij.plugins:structure-teamcity:${rootProject.ext["structureLibVersion"]}")
  implementation("org.jetbrains.intellij.plugins:structure-dotnet:${rootProject.ext["structureLibVersion"]}")
}
