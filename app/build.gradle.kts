import java.util.Properties
import java.io.FileInputStream

// Load secrets from secrets.properties file
val secretsProperties = Properties().apply {
    val secretsFile = rootProject.file("secrets.properties")
    if (secretsFile.exists()) {
        load(FileInputStream(secretsFile))
    } else {
        println("WARNING: secrets.properties file not found!")
    }
}

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.halalbites"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.halalbites"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // ✅ Inject API & Database Secrets Securely as BuildConfig fields
        buildConfigField("String", "BASE_URL", "\"${secretsProperties["BASE_URL"] ?: "http://10.0.2.2:8080/"}\"")
        buildConfigField("String", "POSTGRES_DB_URL", "\"${secretsProperties["POSTGRES_DB_URL"] ?: ""}\"")
        buildConfigField("String", "POSTGRES_USER", "\"${secretsProperties["POSTGRES_USER"] ?: ""}\"")
        buildConfigField("String", "POSTGRES_PASSWORD", "\"${secretsProperties["POSTGRES_PASSWORD"] ?: ""}\"")

        // ✅ Secure Google Maps API Key
        buildConfigField("String", "GOOGLE_MAPS_API_KEY", "\"${secretsProperties["GOOGLE_MAPS_API_KEY"] ?: ""}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true // ✅ Enable BuildConfig to use API Keys
    }
}

dependencies {
    // AndroidX Libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Google Services (Google Maps & Places SDK)
    implementation(libs.places) // Google Places SDK
    implementation(libs.play.services.base) // Google Play Services Base
    implementation(libs.play.services.maps) // Google Maps SDK

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Networking - Retrofit & OkHttp
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // OkHttp - Logging Interceptor (for debugging API calls)
    implementation(libs.logging.interceptor)
}
