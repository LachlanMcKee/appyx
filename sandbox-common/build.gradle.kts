import org.jetbrains.compose.compose

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
                api(project(":core"))
                api(project(":interop-rx2"))
                api(compose.material)

                // Duplicate?
                api(compose.runtime)
                api(compose.foundation)
            }
        }
//        val commonTest by getting {
////            dependencies {
////            }
//        }
        val androidMain by getting {
            dependencies {
            }
        }
//        val androidTest by getting {
////            dependencies {
////            }
//        }
        val desktopMain by getting {
            dependencies {
            }
        }
//        val desktopTest by getting
    }
}

android {
    namespace = "com.bumble.appyx.sandbox.common"
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
