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

dependencies {
  implementation(libs.aws.lambda.core)
  implementation(libs.aws.sdk.url.connection)
  implementation(libs.jackson.datatypeJsr310)
  implementation(project(":services:plugin-blockmap-patches"))
  runtimeOnly(libs.log4j.slf4j18)
  runtimeOnly(libs.aws.lambda.log4j2)
}


project.layout.buildDirectory.set(File(rootProject.projectDir, "build/plugin-blockmap-lambda"))

tasks.register<Zip>("buildPreviewZip") {
  from(project.tasks["compileKotlin"])
  from(project.tasks["processResources"])
  archiveFileName.set("plugin-blockmap-patches.zip")
  into("lib") {
    from(configurations.runtimeClasspath)
  }
}

tasks.named("assemble") {
  dependsOn("buildPreviewZip")
}
