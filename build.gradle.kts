plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.multiplatform.library) apply false
    alias(libs.plugins.dokka)
}

dependencies {
    dokka(project(":core"))
    dokka(project(":data"))
    dokka(project(":resources"))
    dokka(project(":design"))
    dokka(project(":foundation"))
    dokka(project(":platform"))
    dokka(project(":primitives"))
    dokka(project(":composites"))
    dokka(project(":maps"))
    dokka(project(":media"))
    dokka(project(":firebase"))
}

dokka {
    moduleName.set("Yalla SDK")
    dokkaPublications.html {
        outputDirectory.set(rootProject.layout.buildDirectory.dir("dokka"))
    }
}
