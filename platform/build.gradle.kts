plugins {
    id("yalla.sdk.kmp.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Yalla libraries
            api(projects.design)
            api(projects.resources)

            // Compose — public types: every NativeXxx is @Composable +
            // takes Modifier; Color / Shape / Dp / TextStyle in public
            // signatures (e.g. NativeSheet, NativeCircleIconButton).
            api(libs.compose.runtime)
            api(libs.compose.ui)
            api(libs.compose.foundation)
            api(libs.compose.material3)
            implementation(libs.compose.components.resources)

            // Datetime — kotlinx-datetime types appear in NativeWheelDatePicker
            // (LocalDate). Wheel-picker library is android-only impl, demoted.
            api(libs.kotlinx.datetime)

            // Decompose — NavigatorImpl + NativeRootComponent expose
            // ComponentContext / StackNavigation in public ctors.
            api(libs.decompose)
            api(libs.decompose.compose)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.google.play.app.update)
            implementation(libs.androidx.browser)
            implementation(libs.play.services.auth.api.phone)
            implementation(libs.datetime.wheel.picker)
        }
    }
}
