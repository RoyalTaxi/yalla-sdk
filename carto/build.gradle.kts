plugins {
    id("yalla.sdk.kmp.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.compose.runtime)
            api(libs.compose.ui)
            api(libs.compose.foundation)

            api(projects.core)

            api(libs.kotlinx.coroutines.core)

            api(libs.geo)
            api("io.github.dellisd.spatialk:geojson:0.3.0")

            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.maplibre.compose.get().toString()) {
                exclude(group = "org.maplibre.gl", module = "android-sdk")
            }
        }

        androidMain.dependencies {
            implementation(libs.google.maps.compose)
            implementation("org.maplibre.gl:android-sdk-opengl:13.0.2")
        }

        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
        }

        iosTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
