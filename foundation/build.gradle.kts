plugins {
    id("yalla.sdk.kmp.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Yalla libraries
            api(projects.core)
            api(projects.resources)

            // Compose — public types: @Composable + ProvidableCompositionLocal
            // (compose.runtime), Modifier on Modifier.staggerReveal (compose.ui)
            api(compose.runtime)
            api(compose.ui)
            implementation(compose.animation)
            implementation(compose.components.resources)

            // Architecture — public types (BaseViewModel : ViewModel,
            // Lifecycle.State in ObserveAsEvents, StateFlow/CoroutineScope
            // in LocationManager)
            api(libs.androidx.lifecycle.viewmodel)
            api(libs.androidx.lifecycle.runtime.compose)
            api(libs.kotlinx.coroutines.core)

            // DI
            implementation(libs.koin.core)

            // Logging
            implementation(libs.kermit)

            // Location — moko-geo's LocationTracker is a public ctor param
            api(libs.geo)
        }

        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.koin.android)
        }
    }
}
