plugins {
  `maven-publish`
  `signing`
}

group = "org.jetbrains.intellij"
val buildNumber = if (hasProperty("mavenCentralUsername")) {
   System.getenv("BUILD_NUMBER") ?: "SNAPSHOT"
} else {
  "SNAPSHOT"
}
version = "1.0.$buildNumber"

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
  withSourcesJar()
  withJavadocJar()
}

publishing {
  publications {
    fun MavenPublication.configurePom() {
      pom {
        name.set("JetBrains Blockmap")
        description.set("Library to chunk files and build metadata for patch updates. Based on the FastCDC algorithm.")
        url.set("https://github.com/JetBrains/plugin-blockmap-patches")
        licenses {
          license {
            name.set("The Apache Software License, Version 2.0")
            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
          }
        }
        developers {
          developer {
            id.set("chrkv")
            name.set("Ivan Chirkov")
            organization.set("JetBrains")
          }
          developer {
            id.set("satamas")
            name.set("Semyon Atamas")
            organization.set("JetBrains")
          }
          developer {
            id.set("AlexanderPrendota")
            name.set("Alexander Prendota")
            organization.set("JetBrains")
          }
          developer {
            id.set("TAJlOS")
            name.set("Ivan Petrov")
          }
        }
        scm {
          connection.set("scm:git:git://github.com/JetBrains/plugin-blockmap-patches.git")
          developerConnection.set("scm:git:ssh://github.com/JetBrains/plugin-blockmap-patches.git")
          url.set("https://github.com/JetBrains/plugin-blockmap-patches")
        }
      }
    }

    create<MavenPublication>("blockmap-maven") {
      groupId = "org.jetbrains.intellij"
      artifactId = "blockmap"
      version = project.version.toString()
      from(project(":services:plugin-blockmap-patches:blockmap").components["java"])
      configurePom()
    }
  }

  repositories {
    maven {
      url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")

      credentials {
        username = findProperty("mavenCentralUsername").toString()
        password = findProperty("mavenCentralPassword").toString()
      }
    }
  }
}

signing {
  isRequired = hasProperty("mavenCentralUsername")

  useInMemoryPgpKeys(findProperty("signingKey").toString(), findProperty("signingPassword").toString())
  sign(publishing.publications["blockmap-maven"])
}
