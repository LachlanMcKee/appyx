plugins {
    id("com.bumble.appyx.android.library")
    id("appyx-publish-android")
}

publishingPlugin {
    artifactId = "utils-interop-rx3"
}

android {
    namespace = "com.bumble.appyx.utils.interop.rx3"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    api(project(":appyx-navigation:appyx-navigation"))
    api(libs.rxjava3)
    api(libs.rxrelay3)

    implementation(libs.androidx.lifecycle.java8)

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
}
