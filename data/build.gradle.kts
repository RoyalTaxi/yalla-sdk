plugins {
    id("yalla.sdk.kmp")
}

dokka {
    dokkaSourceSets.configureEach {
        includes.from("MODULE.md")
    }
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

            api(libs.datastore.preferences)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(libs.koin.android)
        }

        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}
