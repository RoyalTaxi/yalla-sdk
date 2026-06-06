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
    ":network",
    ":datastore",
    ":resources",
    ":design",
    ":foundation",
    ":capabilities",
    ":components",
    ":maps",
    ":media",
    ":telemetry",
    ":bom",
    ":demo"
)
