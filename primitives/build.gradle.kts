plugins {
    id("yalla.sdk.kmp.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Yalla libraries
            implementation(projects.core)
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

            // Lottie animations (for SearchPin)
            implementation(libs.compottie)
            implementation(libs.compottie.resources)

            // Layout
            implementation(libs.constraintlayout)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.ui.tooling)
}
