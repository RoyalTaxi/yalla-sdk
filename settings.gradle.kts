rootProject.name = "yalla-sdk"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

include(
    ":core",
    ":data",
    ":resources",
    ":design",
    ":platform",
    ":components",
    ":maps",
    ":media",
    ":firebase",
    ":bom",
)
