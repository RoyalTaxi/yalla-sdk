plugins {
    id("yalla.sdk.kmp.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core)

            api(libs.compose.runtime)
            api(libs.compose.ui)
            implementation(libs.compose.foundation)

            api(libs.kotlinx.coroutines.core)

            implementation(libs.koin.core)
            implementation(libs.kermit)

            api(libs.geo)
            api(libs.connectivity.device)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.koin.android)

            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.browser)
            implementation(libs.google.play.app.update)
            implementation(libs.play.services.auth.api.phone)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
