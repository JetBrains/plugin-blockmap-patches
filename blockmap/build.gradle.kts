plugins {
  `maven-publish`
  id("com.jfrog.bintray")
}

group = "org.jetbrains.intellij"
val buildNumber = System.getenv("BUILD_NUMBER") ?: "SNAPSHOT"
version = "1.0.$buildNumber"


publishing {
  publications {
    create<MavenPublication>("blockmap-maven") {
      from(project(":services:plugin-blockmap-patches:blockmap").components["java"])
    }
  }
}

if (hasProperty("bintrayUser")) {
  bintray {
    user = project.findProperty("bintrayUser").toString()
    key = project.findProperty("bintrayApiKey").toString()
    publish = true
    setPublications("blockmap-maven")
    pkg.apply {
      userOrg = "jetbrains"
      repo = "intellij-third-party-dependencies"
      name = "blockmap-library"
      setLicenses("Apache-2.0")
      vcsUrl = "git"
      version.apply {
        name = project.version.toString()
      }
    }
  }
}
