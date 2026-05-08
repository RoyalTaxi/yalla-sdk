plugins {
    id("yalla.sdk.kmp.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.design)
            api(projects.resources)

            api(libs.compose.runtime)
            api(libs.compose.ui)
            api(libs.compose.foundation)
            api(libs.compose.material3)
            api(libs.compose.material.icons.extended)

            implementation(libs.compose.ui.tooling.preview)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.ui.tooling)
}
