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
            // Compose — every Yalla*Map composable is @Composable + Modifier-
            // taking; CameraPosition / MarkerState are Color/Dp-shaped so
            // compose.ui types end up in our public surface.
            api(libs.compose.runtime)
            api(libs.compose.ui)
            api(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.components.resources)

            // Core — GeoPoint and Address types appear in MapController
            // signatures (cameraPosition, markerState, fitBounds).
            api(projects.core)

            // Coroutines — StateFlow<CameraPosition> / <MarkerState> /
            // <Boolean> on the MapController public surface.
            api(libs.kotlinx.coroutines.core)

            // Geo — moko-geo LatLng types appear in public params.
            api(libs.geo)
            implementation(libs.geo.compose)

            // MapLibre — LibreMapController exposes maplibre.compose
            // CameraState directly via bind(). Public.
            api(libs.maplibre.compose)

            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.google.maps.compose)
            implementation(libs.play.services.maps)
        }

        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
