package uz.yalla.sdk.buildlogic.extensions

import org.gradle.api.Project

internal const val YALLA_GROUP = "uz.yalla.sdk"

fun Project.configureYallaCoordinates() {
    group = YALLA_GROUP
    version = findProperty("yalla.sdk.version") as String
}
