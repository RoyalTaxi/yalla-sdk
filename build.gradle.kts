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
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.binary.compatibility.validator)
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ktlint) apply false
    id("yalla.sdk.quality")
}

apiValidation {
    @OptIn(kotlinx.validation.ExperimentalBCVApi::class)
    klib {
        enabled = true
    }

    ignoredProjects.addAll(listOf("demo", "konsistTest", "bom"))
}

val docsEnabled = (findProperty("yalla.docs") as? String)?.toBoolean() == true
if (docsEnabled) {
    apply(plugin = "org.jetbrains.dokka")

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
