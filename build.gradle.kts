plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.multiplatform.library) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.binary.compatibility.validator)
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

dependencies {
    dokka(project(":core"))
    dokka(project(":network"))
    dokka(project(":datastore"))
    dokka(project(":resources"))
    dokka(project(":design"))
    dokka(project(":foundation"))
    dokka(project(":components"))
    dokka(project(":maps"))
    dokka(project(":media"))
    dokka(project(":telemetry"))
}

dokka {
    moduleName.set("Yalla SDK")
    dokkaPublications.html {
        outputDirectory.set(rootProject.layout.buildDirectory.dir("dokka"))
    }
}
