plugins {
    id("yalla.sdk.kmp.compose")
    kotlin("native.cocoapods")
}

kotlin {
    cocoapods {
        version = "1.0.0"
        summary = "Yalla Maps - Unified KMP map library"
        ios.deploymentTarget = "16.6"
        pod("GoogleMaps")
    }

    sourceSets {
        commonMain.dependencies {
            // Compose
            api(compose.runtime)
            api(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)

            // Core dependencies
            api(projects.core)
            implementation(projects.design)
            implementation(projects.resources)

            // Lifecycle
            implementation(libs.androidx.lifecycle.runtime.compose)

            // Coroutines
            implementation(libs.kotlinx.coroutines.core)

            // DI
            implementation(libs.koin.core)
            implementation(libs.koin.compose)

            // Geo
            api(libs.geo)
            implementation(libs.geo.compose)

            // MapLibre
            api(libs.maplibre.compose)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.google.maps.compose)
            implementation(libs.play.services.maps)
        }
    }
}
