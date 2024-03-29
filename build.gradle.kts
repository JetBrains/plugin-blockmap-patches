import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
  mavenCentral()
}

plugins {
  alias(builds.plugins.kotlin.jvm)
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    jvmTarget = "11"
  }
}

tasks.test {
  useJUnitPlatform()
  testLogging {
    showStandardStreams = true
    exceptionFormat = TestExceptionFormat.FULL
  }
}

dependencies {
  api("software.amazon.awssdk:s3:2.10.78") {
    dependencies {
      implementation("commons-codec:commons-codec:1.16.0") {
        because("there is a vulnerability in 1.11")
      }
    }
  }
  testImplementation(libs.junit)
  testImplementation(project(":services:plugin-blockmap-patches"))
  implementation(project(":services:plugin-blockmap-patches:blockmap"))
  implementation(libs.jackson.datatypeJsr310)
}
