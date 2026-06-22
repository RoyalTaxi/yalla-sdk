package uz.yalla.sdk.buildlogic.extensions

import kotlinx.validation.KotlinApiBuildTask
import kotlinx.validation.KotlinApiCompareTask
import org.gradle.api.Project
import org.gradle.api.tasks.Sync
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

fun Project.configureAndroidAbiValidation() {
    val moduleName = name
    val baselineDir = layout.projectDirectory.dir("api/android")
    val baselineFile = baselineDir.file("$moduleName.api")
    val generatedFile = layout.buildDirectory.file("api/android/$moduleName.api")

    val androidMainClasses = layout.buildDirectory.dir("classes/kotlin/android/main")

    val abiReaderClasspath = configurations.detachedConfiguration(
        dependencies.create("org.jetbrains.kotlin:kotlin-metadata-jvm:${getKotlinPluginVersion()}")
    )

    val build = tasks.register<KotlinApiBuildTask>("androidApiBuild") {
        group = "verification"
        description = "Builds the public ABI dump of the Android target's main classes."
        dependsOn("compileAndroidMain")
        inputClassesDirs.from(androidMainClasses)
        runtimeClasspath.from(abiReaderClasspath)
        outputApiFile.set(generatedFile)
    }

    val check = tasks.register<KotlinApiCompareTask>("androidApiCheck") {
        group = "verification"
        description = "Checks the Android target's public ABI against the committed api/android baseline."
        projectApiFile.set(baselineFile)
        generatedApiFile.set(build.flatMap { it.outputApiFile })
    }

    tasks.register<Sync>("androidApiDump") {
        group = "other"
        description = "Updates the committed Android ABI baseline from the current public surface."
        from(build.flatMap { it.outputApiFile })
        into(baselineDir)
        rename { "$moduleName.api" }
    }

    tasks.named("apiCheck").configure { dependsOn(check) }
    tasks.named("apiDump").configure { dependsOn("androidApiDump") }
}
