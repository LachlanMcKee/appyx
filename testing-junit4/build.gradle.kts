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
    implementation(libs.junit)
    implementation(libs.kotlin.coroutines.test)
}
