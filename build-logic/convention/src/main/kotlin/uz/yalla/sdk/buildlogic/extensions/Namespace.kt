package uz.yalla.sdk.buildlogic.extensions

import org.gradle.api.Project

fun Project.yallaNamespace(): String = (listOf(YALLA_GROUP) + pathSegments().map { it.replace('-', '.') }).joinToString(".")

fun Project.yallaFrameworkBaseName(): String = "Yalla" + pathSegments().joinToString("") { it.replaceFirstChar(Char::uppercase) }

private fun Project.pathSegments(): List<String> = path.removePrefix(":").split(":").filter { it.isNotBlank() }
