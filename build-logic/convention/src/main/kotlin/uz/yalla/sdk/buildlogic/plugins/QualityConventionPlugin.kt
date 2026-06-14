package uz.yalla.sdk.buildlogic.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import uz.yalla.sdk.buildlogic.extensions.configureQuality

// G4 — the static-analysis gate. Applied once at the SDK root, this plugin stands up
// detekt (code smells) + ktlint (formatting) across the root and every subproject, all
// reading the single committed config/detekt/detekt.yml and root .editorconfig. The
// aggregate `staticAnalysis` task is the stable check name CI depends on.
class QualityConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        check(target == target.rootProject) {
            "yalla.sdk.quality must be applied to the root project (it configures all modules)."
        }
        target.configureQuality()
    }
}
