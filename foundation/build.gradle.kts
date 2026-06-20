plugins {
    id("yalla.sdk.kmp.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core)
            api(projects.resources)

            api(libs.compose.runtime)
            api(libs.compose.ui)
            implementation(libs.compose.foundation)
            implementation(libs.compose.animation)
            implementation(libs.compose.components.resources)

            api(libs.androidx.lifecycle.viewmodel)
            api(libs.androidx.lifecycle.runtime.compose)
            api(libs.kotlinx.coroutines.core)

            implementation(libs.koin.core)

            implementation(libs.kermit)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
        }

        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
