plugins {
    id("yalla.sdk.kmp")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core)

            api(libs.kotlinx.serialization.json)
            api(libs.ktor.client.core)
            api(libs.ktor.client.content.negotiation)
            api(libs.ktor.serialization.kotlinx.json)
            api(libs.ktor.client.logging)
            api(libs.kotlinx.coroutines.core)
            api(libs.koin.core)

            api(libs.multiplatform.settings)
            api(libs.multiplatform.settings.no.arg)
            api(libs.multiplatform.settings.coroutines)
            api(libs.datastore.preferences)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(libs.koin.android)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}
