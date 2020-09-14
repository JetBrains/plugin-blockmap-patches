plugins {
  `maven-publish`
  id("com.jfrog.bintray")
}

group = "org.jetbrains.intellij"
val buildNumber = System.getenv("BUILD_NUMBER") ?: "SNAPSHOT"
version = "1.0.$buildNumber"

java {
  withSourcesJar()
}

publishing {
  publications {
    create<MavenPublication>("blockmap-maven") {
      from(project(":services:plugin-blockmap-patches:blockmap").components["java"])
    }
  }
}

if (hasProperty("bintrayUser")) {
  publishTo("intellij-plugin-service")
  publishTo("intellij-third-party-dependencies")
}

fun publishTo(repository: String){
  bintray {
    user = project.findProperty("bintrayUser").toString()
    key = project.findProperty("bintrayApiKey").toString()
    publish = true
    setPublications("blockmap-maven")
    pkg.apply {
      userOrg = "jetbrains"
      repo = repository
      name = "blockmap-library"
      setLicenses("Apache-2.0")
      vcsUrl = "git"
      version.apply {
        name = project.version.toString()
      }
    }
  }
}
