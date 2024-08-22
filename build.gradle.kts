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
  api(libs.aws.sdk.s3)
  implementation(project(":services:plugin-blockmap-patches:blockmap"))
  implementation(libs.jackson.datatypeJsr310)

  testImplementation(project(":services:plugin-blockmap-patches"))
  testImplementation ("org.jetbrains.kotlin:kotlin-test")
}
