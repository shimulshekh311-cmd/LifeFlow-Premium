import com.google.gms.googleservices.GoogleServicesPlugin.MissingGoogleServicesStrategy
import java.util.Base64

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.secrets)
  alias(libs.plugins.google.services)
}

// Automatically decode debug.keystore from embedded base64 string if it is missing
val keystoreFile = file("${rootDir}/debug.keystore")
if (!keystoreFile.exists()) {
  try {
    val base64Content = "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"
    val decodedBytes = Base64.getDecoder().decode(base64Content)
    keystoreFile.writeBytes(decodedBytes)
    logger.lifecycle("Successfully generated debug.keystore at ${keystoreFile.absolutePath}")
  } catch (e: java.lang.Exception) {
    throw GradleException("Failed to decode embedded debug.keystore: ${e.message}", e)
  }
}

android {
  namespace = "com.example"
  compileSdk = 35

  defaultConfig {
    applicationId = "com.aistudio.lifeflow.wkvzts"
    minSdk = 24
    targetSdk = 35
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
      signingConfig = signingConfigs.getByName("debugConfig")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
}

// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
}

googleServices {
  missingGoogleServicesStrategy = MissingGoogleServicesStrategy.WARN
}


// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  // implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  // implementation(libs.androidx.camera.camera2)
  // implementation(libs.androidx.camera.core)
  // implementation(libs.androidx.camera.lifecycle)
  // implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  // implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  // implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  // implementation(libs.coil.compose)
  implementation(libs.converter.moshi)
  implementation(libs.firebase.ai)
  implementation(libs.firebase.appcheck.recaptcha)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  // implementation(libs.play.services.location)
  implementation(libs.retrofit)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  compilerOptions {
    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
  }
}
