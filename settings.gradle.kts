rootProject.name = "yalla-sdk"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        // Storytale dev builds (no Maven Central release yet)
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

include(
    ":core",
    ":data",
    ":resources",
    ":design",
    ":platform",
    ":foundation",
    ":primitives",
    ":composites",
    ":maps",
    ":media",
    ":firebase",
    ":bom",
    ":component-catalog",
)
