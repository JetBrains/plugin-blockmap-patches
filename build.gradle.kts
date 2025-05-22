import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

repositories {
  mavenCentral()
}

plugins {
  alias(builds.plugins.kotlin.jvm)
}

val javaVersion = "11"
java {
  val jdkVersion = JavaVersion.toVersion(javaVersion)
  sourceCompatibility = jdkVersion
  targetCompatibility = jdkVersion
}

kotlin {
  compilerOptions {
    jvmTarget.set(JvmTarget.fromTarget(javaVersion))
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
  implementation(libs.bundles.logback)

  testImplementation(project(":services:plugin-blockmap-patches"))
  testImplementation ("org.jetbrains.kotlin:kotlin-test")
}
