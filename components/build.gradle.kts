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
            api(projects.maps)
            api(projects.media)
            api(projects.core)
            implementation(projects.foundation)

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
            implementation(libs.coil.svg)

            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(libs.compose.ui.tooling.preview)
        }

        androidMain.dependencies {
            implementation(projects.capabilities)
        }
    }
}

val yallaComponentsXcf = XCFramework("YallaComponents")
kotlin.targets
    .withType(KotlinNativeTarget::class.java)
    .configureEach {
        binaries.withType(Framework::class.java).configureEach {
            export(projects.maps)
            export(projects.media)
            export(projects.design)
            export(projects.resources)
            export(projects.core)
            yallaComponentsXcf.add(this)
        }
    }

dependencies {
    androidRuntimeClasspath(libs.compose.ui.tooling)
}
