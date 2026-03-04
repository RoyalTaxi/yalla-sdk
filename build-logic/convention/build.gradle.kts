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
    compileOnly("org.jetbrains.dokka:dokka-gradle-plugin:${libs.versions.dokka.get()}")
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
