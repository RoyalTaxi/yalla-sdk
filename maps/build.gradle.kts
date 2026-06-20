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
        }

        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
        }

        iosTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
