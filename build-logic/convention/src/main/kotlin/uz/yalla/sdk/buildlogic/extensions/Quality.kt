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

internal const val STATIC_ANALYSIS_TASK = "staticAnalysis"

private const val DETEKT_ALL_TASK = "detektAll"

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

    tasks.register(STATIC_ANALYSIS_TASK) {
        description = "Aggregate static-analysis gate: detekt + ktlint across all modules."
        group = "verification"
        dependsOn(allprojects.map { "${it.path}:$DETEKT_ALL_TASK" })
        dependsOn(allprojects.map { "${it.path}:ktlintCheck" })
    }
}
