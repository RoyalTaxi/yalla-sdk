package uz.yalla.sdk.buildlogic.extensions

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

// Stable aggregate task name. Wired as the single required check in CI — keep it
// stable so the GitHub branch-protection rule never needs renaming.
internal const val STATIC_ANALYSIS_TASK = "staticAnalysis"

// Per-project detekt pass that scans EVERY Kotlin source set (commonMain, androidMain,
// iosMain, *Test, ...). The default `detekt` task the plugin auto-creates only sees the
// JVM `src/main` layout, which is empty in a KMP module — so we register our own and
// point it at the whole `src` tree. ktlint-gradle already discovers all source sets.
private const val DETEKT_ALL_TASK = "detektAll"

// Configures the static-analysis gate on [target] and all of its subprojects with a
// clean split of concerns: ktlint owns formatting (driven by the committed root
// .editorconfig — no trailing commas, 120 cols, Compose naming exemptions) and detekt
// owns code smells (config/detekt/detekt.yml, tuned for a Compose component library).
// Running a single ktlint engine avoids the indentation conflicts you get from also
// enabling detekt-formatting. Applied once at the SDK root.
internal fun Project.configureQuality() {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
    val detektVersion = libs.findVersion("detekt").get().requiredVersion
    val ktlintVersion = libs.findVersion("ktlint").get().requiredVersion

    val sharedDetektConfig = rootProject.layout.projectDirectory.file("config/detekt/detekt.yml")

    allprojects {
        pluginManager.apply("io.gitlab.arturbosch.detekt")
        pluginManager.apply("org.jlleitschuh.gradle.ktlint")

        extensions.configure<DetektExtension> {
            toolVersion = detektVersion
            buildUponDefaultConfig = true
            allRules = false
            parallel = true
            ignoreFailures = false
            config.setFrom(sharedDetektConfig)
        }

        extensions.configure<KtlintExtension> {
            version.set(ktlintVersion)
            android.set(false)
            outputToConsole.set(true)
            ignoreFailures.set(false)
            filter {
                exclude { element -> element.file.path.contains("/build/") }
                exclude("**/generated/**")
            }
            reporters {
                reporter(ReporterType.PLAIN)
                reporter(ReporterType.CHECKSTYLE)
            }
        }

        tasks.register<Detekt>(DETEKT_ALL_TASK) {
            description = "Runs detekt over every Kotlin source set in this module."
            group = "verification"
            buildUponDefaultConfig = true
            parallel = true
            ignoreFailures = false
            config.setFrom(sharedDetektConfig)
            setSource(layout.projectDirectory.dir("src"))
            include("**/*.kt", "**/*.kts")
            exclude("**/build/**", "**/generated/**", "**/resources/**")
            reports {
                html.required.set(true)
                sarif.required.set(true)
                xml.required.set(false)
                txt.required.set(false)
                md.required.set(false)
            }
        }
    }

    // One required check for CI. Fans out to every module's detektAll + ktlintCheck.
    tasks.register(STATIC_ANALYSIS_TASK) {
        description = "Aggregate static-analysis gate: detekt + ktlint across all modules."
        group = "verification"
        dependsOn(allprojects.map { "${it.path}:$DETEKT_ALL_TASK" })
        dependsOn(allprojects.map { "${it.path}:ktlintCheck" })
    }
}
