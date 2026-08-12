import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

// Signing credentials come from a git-ignored keystore.properties locally, or
// from environment variables in CI (release.yml decodes SIGNING_KEY_BASE64 to a
// file and exports SIGNING_KEYSTORE_PATH).
//
// When neither is present the release variant is left UNSIGNED on purpose.
// An earlier version fell back to the debug signing config so a fresh clone
// would still produce an installable APK. That was a trap: CI has no signing
// secrets, so every "release" APK it published was signed with the runner's
// auto-generated debug keystore -- a DIFFERENT key each run. Two consecutive
// CI releases were not upgrade-compatible with each other, and nothing said so.
// Unsigned fails loudly at install time; silently-differently-signed does not.
val keystorePropsFile = rootProject.file("keystore.properties")
val keystoreProps = Properties().apply {
    if (keystorePropsFile.exists()) keystorePropsFile.inputStream().use { load(it) }
}
fun signingValue(key: String, env: String): String? =
    keystoreProps.getProperty(key) ?: System.getenv(env)

val releaseStoreFile = signingValue("storeFile", "SIGNING_KEYSTORE_PATH")
val hasReleaseSigning = releaseStoreFile != null && rootProject.file(releaseStoreFile).exists()

android {
    namespace = "com.peersignal.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.peersignal.app"
        // Android 9. Adaptive icons need 26, so mipmap-anydpi-v26 covers every
        // supported device and no PNG fallbacks are required.
        // Note for Track B: getCurrentThermalStatus() is API 29, so the thermal
        // governor must be guarded with Build.VERSION.SDK_INT >= 29 and fall
        // back to a fixed conservative duty cycle on 28 rather than training
        // unthrottled.
        minSdk = 28
        targetSdk = 34
        // release.yml derives these from the git tag and passes them as
        // ORG_GRADLE_PROJECT_versionCode / _versionName. The literals below are
        // the local-development fallback only -- every published build carries
        // the tag's version, so a shipped APK can always be traced to a tag.
        versionCode = (findProperty("versionCode") as String?)?.toInt() ?: 1
        versionName = (findProperty("versionName") as String?) ?: "0.0.1-dev"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = rootProject.file(releaseStoreFile!!)
                storePassword = signingValue("storePassword", "SIGNING_KEYSTORE_PASSWORD")
                keyAlias = signingValue("keyAlias", "SIGNING_KEY_ALIAS")
                keyPassword = signingValue("keyPassword", "SIGNING_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        debug {
            // So a debug build and a release build can coexist on one device.
            // Previously both used com.peersignal.app and could not.
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = true
            // Kotlin DSL name; the Groovy form is `shrinkResources`.
            isShrinkResources = true
            // No fallback. Without credentials this stays null and AGP emits
            // app-release-unsigned.apk, which Android refuses to install --
            // an honest failure rather than a differently-signed surprise.
            signingConfig = if (hasReleaseSigning) signingConfigs.getByName("release") else null
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    
    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
