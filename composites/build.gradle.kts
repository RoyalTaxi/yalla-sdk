plugins {
    id("yalla.sdk.kmp.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Yalla libraries
            api(projects.core)
            api(projects.design)
            api(projects.resources)
            implementation(projects.foundation)
            implementation(projects.primitives)
            implementation(projects.platform)

            // Compose — public types: @Composable + Modifier in every public
            // composable signature; Color + Shape + Dp + TextStyle in every
            // *Colors / *Dimens data class; layout primitives + RowScope in
            // public slots; Surface / Icon / Text / TextField from Material3
            // in many public composables.
            api(compose.runtime)
            api(compose.ui)
            api(compose.foundation)
            api(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(libs.compose.ui.tooling.preview)

            // Datetime — LocalDate fields on DatePickerSheetState
            implementation(libs.kotlinx.datetime)

            // Connectivity — DeviceConnectivityState wraps moko-connectivity
            implementation(libs.connectivity.device)
        }

        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.datetime.wheel.picker)
        }
    }
}
