plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kotlin.serialization)
}

kotlin {
  jvmToolchain(17)
}

dependencies {
  implementation(project(":core:contracts"))
  implementation(project(":core:network"))
  implementation(project(":core:ui"))
  implementation(libs.okhttp)
  implementation(libs.serialization.json)
  implementation(libs.coroutines.core)

  testImplementation(libs.junit4)
  testImplementation(libs.truth)
  testImplementation(libs.mockwebserver)
  testImplementation(libs.coroutines.core)
}
