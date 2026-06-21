plugins {
    id("yalla.sdk.kmp")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core)

            implementation(projects.telemetry)

            api(libs.kotlinx.coroutines.core)
            api(libs.koin.core)
            api(libs.androidx.datastore.preferences)
        }

        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
