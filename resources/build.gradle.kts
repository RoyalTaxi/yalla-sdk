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

    // Emit `public` on generated icon declarations so they satisfy the
    // module's explicitApi() mode (KmpLibraryConventionPlugin).
    codeStyle {
        useExplicitMode = true
    }

    iconPack {
        name = "YallaIcons"
        targetSourceSet = "commonMain"
    }
}

kotlin {
    sourceSets {
        commonMain {
            kotlin.srcDir(
                layout.buildDirectory.dir("generated/sources/valkyrie/commonMain/kotlin")
            )

            dependencies {
                implementation(libs.compose.runtime)
                implementation(libs.compose.components.resources)
                implementation(libs.compose.ui)
            }
        }
    }
}

val valkyrieTask = "generateValkyrieImageVectorCommonMain"

tasks.configureEach {
    if (name != valkyrieTask && !name.startsWith("generateValkyrie")) {
        val usesValkyrieOutput =
            name.startsWith("compileKotlin") ||
                name.startsWith("runKtlint") ||
                name.contains("SourcesJar", ignoreCase = true) ||
                name.contains("sourcesJar", ignoreCase = true)
        if (usesValkyrieOutput) {
            dependsOn(valkyrieTask)
        }
    }
}
