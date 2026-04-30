plugins {
    id("yalla.sdk.kmp.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.resources)
            api(compose.runtime)
            api(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }
    }
}
