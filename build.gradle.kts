import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
  mavenCentral()
}

plugins {
  kotlin("jvm")
  kotlin("plugin.serialization") version "1.3.70"
}

tasks.withType<KotlinCompile> {
  kotlinOptions{
    jvmTarget = "1.8"
  }
}

dependencies {
  api("software.amazon.awssdk:s3:2.10.78")
  testImplementation("junit:junit:4.12")
  testImplementation(project(":services:plugin-blockmap-patches"))
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")
}
