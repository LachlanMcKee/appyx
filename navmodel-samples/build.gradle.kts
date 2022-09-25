plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("appyx-lint")
    id("appyx-detekt")
}

android {
    namespace = "com.bumble.appyx.navmodel"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    api(project(":core"))
    api(libs.compose.ui.ui)

    implementation(libs.androidx.lifecycle.java8)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.foundation.layout)
    implementation(libs.compose.foundation)

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
}
