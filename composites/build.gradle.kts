plugins {
    id("yalla.sdk.kmp.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Yalla libraries
            api(projects.core)
            implementation(projects.foundation)
            implementation(projects.primitives)
            implementation(projects.design)
            implementation(projects.resources)
            implementation(projects.platform)

            // Compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(libs.compose.ui.tooling.preview)

            // Image loading
            implementation(libs.coil)
            implementation(libs.coil.compose)

            // Lottie animations
            implementation(libs.compottie)
            implementation(libs.compottie.resources)

            // Layout
            implementation(libs.constraintlayout)

            // iOS-style components
            implementation(libs.cupertino)

            // Serialization & datetime
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)

            // Connectivity
            implementation(libs.connectivity.device)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.datetime.wheel.picker)
        }
    }
}
