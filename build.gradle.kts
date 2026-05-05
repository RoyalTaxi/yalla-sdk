plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.multiplatform.library) apply false
    alias(libs.plugins.dokka)
    // Binary Compatibility Validator — gates accidental ABI breaks via .api
    // baselines per module. `./gradlew apiDump` regenerates baselines (commit
    // them); `./gradlew apiCheck` runs in CI and fails on drift.
    alias(libs.plugins.kotlinx.binary.compatibility.validator)
}

apiValidation {
    // BOM is a java-platform module — no compiled API surface to validate.
    ignoredProjects.add("bom")
    // Convention plugins live in an included build, not a project — already excluded.

    // Validate the .klib ABI for native targets (iosArm64 / iosSimulatorArm64).
    // Without this, BCV only inspects the JVM (Android) artifacts and would
    // miss accidental ABI breaks on the iOS surface — exactly what consumers
    // (YallaClient via SwiftUI/KMP) hit at integration time.
    @OptIn(kotlinx.validation.ExperimentalBCVApi::class)
    klib {
        enabled = true
    }
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
