package uz.yalla.sdk.buildlogic.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import uz.yalla.sdk.buildlogic.extensions.configureQuality

class QualityConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        check(target == target.rootProject) {
            "yalla.sdk.quality must be applied to the root project (it configures all modules)."
        }
        target.configureQuality()
    }
}
