plugins {
    id("yalla.sdk.kmp.compose")
}

compose.resources {
    publicResClass = true
    packageOfResClass = "uz.yalla.resources"
    generateResClass = always
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.components.resources)
        }
    }
}
