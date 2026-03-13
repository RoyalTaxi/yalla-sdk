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
        val usesValkyrieOutput = name.startsWith("compileKotlin") ||
            name.contains("SourcesJar", ignoreCase = true) ||
            name.contains("sourcesJar", ignoreCase = true)
        if (usesValkyrieOutput) {
            dependsOn(valkyrieTask)
        }
    }
}
