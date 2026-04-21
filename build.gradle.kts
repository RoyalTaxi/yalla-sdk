import com.diffplug.gradle.spotless.SpotlessExtension
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
val ktlintVersion = libs.findVersion("ktlint").get().requiredVersion
val detektVersion = libs.findVersion("detekt").get().requiredVersion

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.multiplatform.library) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.binary.compatibility.validator)
}

@OptIn(kotlinx.validation.ExperimentalBCVApi::class)
apiValidation {
    // BOM has no public API surface (it is a java-platform with no code)
    ignoredProjects.addAll(listOf("bom"))

    // Experimental Klib mode: covers KMP Native targets (iosArm64, iosSimulatorArm64)
    // in addition to JVM/Android. Validated by Task 2 Investigation B; plugin version 0.18.1,
    // @ExperimentalBCVApi as of 2026-04-21.
    klib {
        enabled = true
    }
}

subprojects {
    // Skip BOM module — it's a java-platform, not a KMP module
    if (name == "bom") return@subprojects

    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    configure<SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            targetExclude("**/build/**")
            ktlint(ktlintVersion)
            trimTrailingWhitespace()
            endWithNewline()
        }
        kotlinGradle {
            target("**/*.kts")
            targetExclude("**/build/**")
            ktlint(ktlintVersion)
            trimTrailingWhitespace()
            endWithNewline()
        }
    }

    configure<DetektExtension> {
        toolVersion = detektVersion
        buildUponDefaultConfig = true
        allRules = false
        parallel = true
        config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    }

    configure<KtlintExtension> {
        version.set(ktlintVersion)
        android.set(false)
        outputToConsole.set(true)
        ignoreFailures.set(false)
        filter {
            exclude("**/build/**")
            exclude("**/generated/**")
            exclude("**/build/generated/**")
        }
        reporters {
            reporter(ReporterType.CHECKSTYLE)
            reporter(ReporterType.PLAIN)
        }
    }
}

// Dokka aggregation: every published module contributes to a single docs site.
dependencies {
    dokka(project(":core"))
    dokka(project(":data"))
    dokka(project(":resources"))
    dokka(project(":design"))
    dokka(project(":foundation"))
    dokka(project(":platform"))
    dokka(project(":primitives"))
    dokka(project(":composites"))
    dokka(project(":maps"))
    dokka(project(":media"))
    dokka(project(":firebase"))
}

dokka {
    moduleName.set("Yalla SDK")
    dokkaPublications.html {
        outputDirectory.set(rootProject.layout.buildDirectory.dir("dokka"))
    }
}
