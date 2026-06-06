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
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.compose.compiler.gradle.plugin)
    compileOnly(libs.dokka.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("kmp") {
            id = "yalla.sdk.kmp"
            implementationClass = "uz.yalla.sdk.buildlogic.plugins.KmpLibraryConventionPlugin"
        }

        register("kmpCompose") {
            id = "yalla.sdk.kmp.compose"
            implementationClass = "uz.yalla.sdk.buildlogic.plugins.KmpComposeConventionPlugin"
        }

        register("bom") {
            id = "yalla.sdk.bom"
            implementationClass = "uz.yalla.sdk.buildlogic.plugins.BomConventionPlugin"
        }
    }
}
