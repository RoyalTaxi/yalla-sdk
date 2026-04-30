plugins {
    id("yalla.sdk.kmp.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Yalla libraries
            api(projects.design)
            api(projects.resources)
            implementation(projects.core)
            implementation(projects.platform)

            // Compose — public types: @Composable + Modifier in every public
            // composable signature; ImageVector + Color + Shape on every
            // *Colors / *Dimens data class.
            api(compose.runtime)
            api(compose.ui)
            api(compose.foundation)
            api(compose.material3)
            implementation(compose.components.resources)
            implementation(libs.compose.ui.tooling.preview)

            // Lottie animations (for SearchPin)
            implementation(libs.compottie)
            implementation(libs.compottie.resources)

            // Layout
            implementation(libs.constraintlayout)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.ui.tooling)
}
