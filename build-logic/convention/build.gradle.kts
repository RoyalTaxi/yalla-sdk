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
    // BCV task types (KotlinApiBuildTask/KotlinApiCompareTask) reused by the
    // Android ABI gate; the plugin itself is applied at the SDK root.
    compileOnly(libs.binary.compatibility.validator.gradle.plugin)
    // Static-analysis gate (G4): the QualityConventionPlugin applies these plugins
    // and configures their extensions, so it needs their gradle-plugin types on the
    // build-logic compile classpath.
    compileOnly(libs.detekt.gradle.plugin)
    compileOnly(libs.ktlint.gradle.plugin)
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

        register("quality") {
            id = "yalla.sdk.quality"
            implementationClass = "uz.yalla.sdk.buildlogic.plugins.QualityConventionPlugin"
        }
    }
}
