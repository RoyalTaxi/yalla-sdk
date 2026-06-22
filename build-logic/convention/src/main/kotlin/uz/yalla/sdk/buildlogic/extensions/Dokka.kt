package uz.yalla.sdk.buildlogic.extensions

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.dokka.gradle.DokkaExtension

internal val Project.yallaDocsEnabled: Boolean
    get() = (findProperty("yalla.docs") as? String)?.toBoolean() == true

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
