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

tasks.test {
  useJUnitPlatform()
}

dependencies {
  api("software.amazon.awssdk:s3:2.10.78") {
    dependencies {
      implementation("commons-codec:commons-codec:1.16.0") {
        because("there is a vulnerability in 1.11")
      }
    }
  }
  testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
  testImplementation(project(":services:plugin-blockmap-patches"))
  implementation(project(":services:plugin-blockmap-patches:blockmap"))
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${rootProject.ext["jacksonVersion"]}")
}
