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
                api(project(":customisations"))
                api(compose.runtime)
                api(compose.foundation)

//                testImplementation project(':testing-junit4')
//                testImplementation libs.androidx.arch.core.testing
//                testImplementation libs.junit
//                testImplementation libs.kotlin.coroutines.test
//
//                androidTestImplementation libs.androidx.test.espresso.core
//                androidTestImplementation libs.androidx.test.junit
//                androidTestImplementation libs.compose.ui.test.junit4
            }
        }
//        val commonTest by getting {
////            dependencies {
////                implementation(kotlin("test"))
////            }
//        }
        val androidMain by getting {
            dependencies {
                api(libs.compose.animation.core)
                api(libs.androidx.lifecycle.common)
                api(libs.kotlin.coroutines.android)
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.core)
                implementation(libs.androidx.appcompat)
                implementation(libs.androidx.lifecycle.java8)
                implementation(libs.androidx.lifecycle.runtime)
            }
        }
//        val androidTest by getting {
////            dependencies {
////                implementation("junit:junit:4.13.2")
////            }
//        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview)
                api(libs.kotlin.coroutines.core)
            }
        }
//        val desktopTest by getting
    }
}

android {
    namespace = "com.bumble.appyx.core"
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
