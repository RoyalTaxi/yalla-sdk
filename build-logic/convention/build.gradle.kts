plugins {
    `kotlin-dsl`
}

group = "uz.yalla.sdk.buildlogic"

repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    compileOnly(libs.gradle)
    compileOnly(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("kmp") {
            id = "yalla.sdk.kmp"
            implementationClass = "KmpLibraryConventionPlugin"
        }

        register("kmpCompose") {
            id = "yalla.sdk.kmp.compose"
            implementationClass = "KmpComposeConventionPlugin"
        }
    }
}
