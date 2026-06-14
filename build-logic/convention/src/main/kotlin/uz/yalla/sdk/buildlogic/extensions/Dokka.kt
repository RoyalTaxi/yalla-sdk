package uz.yalla.sdk.buildlogic.extensions

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.dokka.gradle.DokkaExtension

/**
 * API docs (Dokka) are opt-in via `-Pyalla.docs=true`.
 *
 * They are off by default so the per-PR publications dry-run does not pay Dokka's
 * configuration cost across every module for docs nothing in that job consumes. A
 * release that wants the HTML/Javadoc output turns the flag on; see the root build
 * and `KmpLibraryConventionPlugin` for the matching gates.
 */
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
