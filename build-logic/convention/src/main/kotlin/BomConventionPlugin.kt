import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get

/** Maven BOM (java-platform) that pins every uz.yalla.sdk:* module to yalla.sdk.version. */
class BomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("java-platform")
                apply("maven-publish")
            }

            group = "uz.yalla.sdk"
            version = project.findProperty("yalla.sdk.version") as String

            extensions.configure<PublishingExtension> {
                publications {
                    create<MavenPublication>("bom") {
                        from(components["javaPlatform"])
                        artifactId = "bom"
                    }
                }
            }

            configureYallaPublishing()
        }
    }
}
