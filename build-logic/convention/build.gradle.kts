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
    // ktlint + detekt plugins are applied at runtime by KmpLibraryConventionPlugin,
    // so they must be on the runtime classpath of the convention plugin (not just
    // compileOnly). Otherwise: NoClassDefFoundError on KtlintExtension at apply().
    implementation("org.jlleitschuh.gradle:ktlint-gradle:${libs.versions.ktlint.gradle.get()}")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:${libs.versions.detekt.get()}")
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

        register("bom") {
            id = "yalla.sdk.bom"
            implementationClass = "BomConventionPlugin"
        }
    }
}
