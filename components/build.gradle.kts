import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    id("yalla.sdk.kmp.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.design)
            api(projects.resources)
            implementation(projects.core)

            api(libs.kotlinx.datetime)

            api(libs.compose.runtime)
            api(libs.compose.ui)
            api(libs.compose.foundation)
            api(libs.compose.material3)
            implementation(libs.compose.components.resources)
            implementation(libs.constraintlayout)
            implementation(libs.compottie)
            implementation(libs.compottie.resources)

            api(libs.coil.compose)
            implementation(libs.coil.network.ktor3)

            implementation(libs.compose.ui.tooling.preview)
        }
    }
}

val yallaComponentsXcf = XCFramework("YallaComponents")
kotlin.targets
    .withType(KotlinNativeTarget::class.java)
    .configureEach {
        binaries.withType(Framework::class.java).configureEach {
            yallaComponentsXcf.add(this)
        }
    }

dependencies {
    androidRuntimeClasspath(libs.compose.ui.tooling)
}
