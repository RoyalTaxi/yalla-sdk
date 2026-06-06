package uz.yalla.sdk.buildlogic.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import uz.yalla.sdk.buildlogic.extensions.configureYallaCoordinates
import uz.yalla.sdk.buildlogic.extensions.configureYallaPublishing

class BomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("java-platform")
                apply("maven-publish")
            }

            configureYallaCoordinates()

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
