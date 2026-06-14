import org.jetbrains.dokka.gradle.DokkaExtension

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.multiplatform.library) apply false
    // Dokka is opt-in (`-Pyalla.docs=true`) so the per-PR publications dry-run does not
    // apply it across every module for docs nothing in that job consumes. Applied (and
    // its aggregation wired) below only when the flag is set; releases that want the
    // HTML/Javadoc output turn it on. See the KMP convention plugin for the module gate.
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.binary.compatibility.validator)
    // G4 — static-analysis gate. The detekt + ktlint markers are declared here
    // `apply false` so they land on the build classpath; the quality convention
    // plugin then applies and configures them across the root + every module.
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ktlint) apply false
    id("yalla.sdk.quality")
}

// G3 — public ABI gate. `apiCheck` (wired into CI) fails the build when a change
// alters the published binary surface without an accompanying, reviewed update to
// the committed `<module>/api/*.api` baselines. Regenerate intentionally with
// `./gradlew apiDump` and commit the diff.
//
// Each published module ships TWO linkable artifacts and both are gated:
//   - the iOS native klibs   -> `<module>/api/<module>.klib.api`, validated by the
//     BCV `klib` block below;
//   - the Android `.aar`     -> `<module>/api/android/<module>.api`, validated by the
//     `androidApiCheck` task the KMP convention plugin wires into each module's
//     `apiCheck` (see build-logic .../extensions/AndroidAbiValidation.kt). BCV 0.18.1
//     does not understand the `com.android.kotlin.multiplatform.library` target, so
//     the Android surface needs that dedicated path — without it, Android-only public
//     symbols (e.g. getAppSignature(context: Context)) changed silently.
apiValidation {
    @OptIn(kotlinx.validation.ExperimentalBCVApi::class)
    klib {
        enabled = true
    }

    // Not published: the demo app, the JVM-only architecture-test module, and the BOM
    // (a constraints-only platform with no code surface).
    ignoredProjects.addAll(listOf("demo", "konsistTest", "bom"))
}

// API docs (Dokka) — opt-in via `-Pyalla.docs=true`. The root plugin owns the
// `dokka` aggregation configuration the modules feed into, so it (and the matching
// per-module plugin application) is applied only when the flag is set. Off by
// default the whole pipeline disappears from configuration, which is what the
// per-PR publications dry-run wants. With the plugin `apply false` above, the Kotlin
// DSL generates no `dokka {}` / `dokka(...)` accessors, so the typed API is used here.
val docsEnabled = (findProperty("yalla.docs") as? String)?.toBoolean() == true
if (docsEnabled) {
    // Version pinned by the `apply false` catalog alias above; apply by id like the
    // KMP convention plugin does, which keeps the typed `dokka {}` config below valid.
    apply(plugin = "org.jetbrains.dokka")

    // Aggregate the documented modules into the root `dokka` configuration. Same set
    // and order as before; `dokka(project(...))` is unavailable here (no accessor when
    // the plugin is `apply false`), so the configuration is named explicitly.
    dependencies.add("dokka", project(":core"))
    dependencies.add("dokka", project(":network"))
    dependencies.add("dokka", project(":datastore"))
    dependencies.add("dokka", project(":resources"))
    dependencies.add("dokka", project(":design"))
    dependencies.add("dokka", project(":foundation"))
    dependencies.add("dokka", project(":components"))
    dependencies.add("dokka", project(":maps"))
    dependencies.add("dokka", project(":media"))
    dependencies.add("dokka", project(":telemetry"))

    extensions.configure<DokkaExtension> {
        moduleName.set("Yalla SDK")
        dokkaPublications.named("html") {
            outputDirectory.set(rootProject.layout.buildDirectory.dir("dokka"))
        }
    }
}
