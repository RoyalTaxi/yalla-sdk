plugins {
    id("yalla.sdk.kmp")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core)

            api(libs.kotlinx.coroutines.core)
            api(libs.koin.core)
            api(libs.androidx.datastore.preferences)
            api(libs.multiplatform.settings)
        }
    }
}
