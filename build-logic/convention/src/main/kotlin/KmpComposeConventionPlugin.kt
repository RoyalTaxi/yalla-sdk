import org.gradle.api.Plugin
import org.gradle.api.Project

/** Base KMP convention + Compose Multiplatform + compose-compiler. Use for UI modules. */
class KmpComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target.pluginManager) {
            apply("yalla.sdk.kmp")
            apply("org.jetbrains.compose")
            apply("org.jetbrains.kotlin.plugin.compose")
        }
    }
}
