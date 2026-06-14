package uz.yalla.sdk.buildlogic.extensions

import kotlinx.validation.KotlinApiBuildTask
import kotlinx.validation.KotlinApiCompareTask
import org.gradle.api.Project
import org.gradle.api.tasks.Sync
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

// G3 — gate the published Android (.aar) ABI.
//
// The standalone BCV plugin and the Kotlin Gradle plugin's own abiValidation both
// only understand the deprecated `androidTarget()` (KotlinAndroidTarget). Neither
// introspects the new `com.android.kotlin.multiplatform.library` target, so its
// `.aar` — and every Android-only public symbol in it, e.g.
// `getAppSignature(context: Context)` — slipped past the iOS-klib-only gate.
//
// BCV's underlying tasks are target-agnostic: KotlinApiBuildTask dumps the public
// ABI of a directory of JVM .class files, and KotlinApiCompareTask fails on any
// drift from a committed baseline. We point them at the Android target's compiled
// classes ourselves, producing the same `<module>/api/android/<module>.api` dumps
// BCV would emit for a JVM/Android target, and fold the check into the module's
// existing `apiCheck` (so CI's `apiCheck` run gains Android coverage for free).
fun Project.configureAndroidAbiValidation() {
    val moduleName = name
    val baselineDir = layout.projectDirectory.dir("api/android")
    val baselineFile = baselineDir.file("$moduleName.api")
    val generatedFile = layout.buildDirectory.file("api/android/$moduleName.api")

    // The Kotlin compile output for the android target's main compilation. This is
    // exactly the bytecode bundled into the published .aar's classes.jar.
    val androidMainClasses = layout.buildDirectory.dir("classes/kotlin/android/main")

    // BCV reads class metadata via kotlin-metadata-jvm; pin it to the project's
    // own Kotlin version so the reader and the bytecode stay in lockstep.
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

    // Fold into the BCV-created aggregate tasks so CI's unqualified `apiCheck`
    // (run in every module) and `apiDump` pick the Android surface up too.
    tasks.named("apiCheck").configure { dependsOn(check) }
    tasks.named("apiDump").configure { dependsOn("androidApiDump") }
}
