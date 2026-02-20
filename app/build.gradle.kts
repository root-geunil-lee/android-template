fun toBuildConfigString(value: String): String {
  return "\"${value.replace("\\", "\\\\").replace("\"", "\\\"")}\""
}

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
}

android {
  namespace = "com.example.androidtemplate"
  compileSdk = 35
  val authBaseUrl = providers.gradleProperty("AUTH_BASE_URL").orElse("").get()

  defaultConfig {
    applicationId = "com.example.androidtemplate"
    minSdk = 26
    targetSdk = 35
    versionCode = 1
    versionName = "1.0"

    buildConfigField("String", "AUTH_BASE_URL", toBuildConfigString(authBaseUrl))

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro",
      )
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  kotlinOptions {
    jvmTarget = "17"
  }

  buildFeatures {
    buildConfig = true
    compose = true
  }

  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

dependencies {
  implementation(project(":core:contracts"))
  implementation(project(":core:navigation"))
  implementation(project(":core:storage"))
  implementation(project(":core:ui"))
  implementation(project(":features:auth"))
  implementation(project(":features:billing"))
  implementation(project(":features:mypage"))

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.security.crypto)

  testImplementation(libs.junit4)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  debugImplementation(libs.androidx.compose.ui.tooling)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
}
