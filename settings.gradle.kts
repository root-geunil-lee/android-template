pluginManagement {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
  }
}

rootProject.name = "android-template"

include(":app")
include(":core:contracts")
include(":core:navigation")
include(":core:network")
include(":core:storage")
include(":core:ui")
include(":features:auth")
include(":features:billing")
