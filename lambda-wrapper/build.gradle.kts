import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val awsLambdaCoreVersion = rootProject.ext["awsLambdaCoreVersion"]

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

dependencies {
  implementation("com.amazonaws:aws-lambda-java-core:$awsLambdaCoreVersion")
  implementation(libs.jackson.datatypeJsr310)
  implementation("software.amazon.awssdk:url-connection-client:2.10.78")
  implementation(project(":services:plugin-blockmap-patches"))
  runtimeOnly("org.apache.logging.log4j:log4j-slf4j18-impl:2.17.0")
  runtimeOnly("com.amazonaws:aws-lambda-java-log4j2:1.5.1")
}


buildDir = File(rootProject.projectDir, "build/plugin-blockmap-lambda")

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
