plugins {
    id("yalla.sdk.kmp.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.design)
            api(projects.resources)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)

            api(libs.kotlinx.datetime)
            api(libs.datetime.wheel.picker)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.google.play.app.update)
        }
    }
}
