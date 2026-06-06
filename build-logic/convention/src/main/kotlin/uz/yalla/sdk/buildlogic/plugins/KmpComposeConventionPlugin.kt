package uz.yalla.sdk.buildlogic.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import uz.yalla.sdk.buildlogic.extensions.configureComposeStability

class KmpComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target.pluginManager) {
            apply("yalla.sdk.kmp")
            apply("org.jetbrains.compose")
            apply("org.jetbrains.kotlin.plugin.compose")
        }

        target.configureComposeStability()
    }
}
