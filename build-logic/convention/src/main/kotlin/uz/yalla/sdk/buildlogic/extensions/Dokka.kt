package uz.yalla.sdk.buildlogic.extensions

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.dokka.gradle.DokkaExtension

fun Project.configureDokkaModuleDoc() {
    extensions.configure<DokkaExtension> {
        dokkaSourceSets.configureEach {
            val moduleDoc = layout.projectDirectory.file("MODULE.md").asFile
            if (moduleDoc.exists()) {
                includes.from(moduleDoc)
            }
        }
    }
}
