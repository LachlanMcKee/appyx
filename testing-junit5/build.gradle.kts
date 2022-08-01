plugins {
    id("java")
    id("kotlin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    api(project(":testing-unit-common"))
    implementation(libs.junit.api)
    implementation(libs.androidx.arch.core.testing)
    implementation(libs.kotlin.coroutines.test.jvm)
}
