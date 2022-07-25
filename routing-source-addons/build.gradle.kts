plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.2.0-alpha01-dev731"
    id("com.android.library")
    id("kotlin-parcelize")
}

kotlin {
    android()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":core"))

                implementation(libs.rxjava2)
                implementation(libs.rxrelay)

//                testImplementation(libs.junit.api)
//                testImplementation(libs.junit.params)
//                testRuntimeOnly(libs.junit.engine)
            }
        }
    }
}

android {
    namespace = "com.bumble.appyx.routingsource"
    compileSdkVersion(32)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(32)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
