import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
  mavenCentral()
}

plugins {
  alias(builds.plugins.kotlin.jvm)
}

val jdkVersion = JavaVersion.VERSION_11
java {
  sourceCompatibility = jdkVersion
  targetCompatibility = jdkVersion
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = jdkVersion.toString()
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
