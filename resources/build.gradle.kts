plugins {
    id("yalla.sdk.kmp.compose")
    alias(libs.plugins.valkyrie)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "uz.yalla.resources"
    generateResClass = always
}

valkyrie {
    packageName = "uz.yalla.resources.icons"
    resourceDirectoryName = "valkyrieResources"

    iconPack {
        name = "YallaIcons"
        targetSourceSet = "commonMain"
    }
}

kotlin {
    // wasmJs target exists so :component-catalog (Storytale gallery) can consume the SDK's
    // shared resources (icons, fonts, strings) without source duplication. This module has no
    // expect declarations and no platform-specific code in commonMain, so the target is safe
    // to add. The wasmJs Maven publication that results is harmless to existing Android/iOS
    // consumers — they continue to resolve the variant matching their platform attributes.
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.components.resources)
            implementation(compose.ui)
        }
    }
}

val valkyrieTask = "generateValkyrieImageVectorCommonMain"

tasks.configureEach {
    if (name != valkyrieTask && !name.startsWith("generateValkyrie")) {
        val usesValkyrieOutput =
            name.startsWith("compileKotlin") ||
                name.contains("SourcesJar", ignoreCase = true) ||
                name.contains("sourcesJar", ignoreCase = true) ||
                // ktlint scans the same source roots compileKotlin* does, so
                // it has to wait for valkyrie before reading generated icons.
                // Without this, parallel `apiCheck + ktlintCheck` runs fail
                // with an implicit-dependency error on Gradle 9.x.
                name.startsWith("runKtlintCheck") ||
                (name.startsWith("ktlint") && name.endsWith("Check")) ||
                // Same race for klib API extraction (BCV) and detekt.
                name.endsWith("ApiBuild") ||
                name.startsWith("detekt")
        if (usesValkyrieOutput) {
            dependsOn(valkyrieTask)
        }
    }
}
