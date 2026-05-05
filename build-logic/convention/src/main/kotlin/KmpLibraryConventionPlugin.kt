import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

/** Base convention for published KMP library modules (Android + iosArm64 + iosSimulatorArm64). */
class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.kotlin.multiplatform.library")
                apply("org.jetbrains.kotlin.multiplatform")
                apply("org.jetbrains.kotlin.plugin.serialization")
                apply("maven-publish")
                apply("org.jetbrains.dokka")
                apply("org.jlleitschuh.gradle.ktlint")
                apply("io.gitlab.arturbosch.detekt")
            }

            group = "uz.yalla.sdk"
            version = project.findProperty("yalla.sdk.version") as String

            extensions.configure<KotlinMultiplatformExtension> {
                targets.withType(KotlinMultiplatformAndroidLibraryTarget::class.java).configureEach {
                    namespace = defaultNamespace(project)
                    compileSdk = 36
                    minSdk = 26

                    androidResources {
                        enable = true
                    }

                    compilerOptions {
                        jvmTarget.set(JvmTarget.JVM_11)
                    }
                }

                iosArm64()
                iosSimulatorArm64()

                targets.withType(KotlinNativeTarget::class.java).configureEach {
                    binaries.framework {
                        baseName = defaultFrameworkBaseName(project)
                        isStatic = true
                    }
                }

                sourceSets.commonTest.dependencies {
                    implementation(kotlin("test"))
                }
            }

            extensions.configure<DokkaExtension> {
                dokkaSourceSets.configureEach {
                    val moduleDoc = layout.projectDirectory.file("MODULE.md").asFile
                    if (moduleDoc.exists()) {
                        includes.from(moduleDoc)
                    }
                }
            }

            extensions.configure<KtlintExtension> {
                version.set("1.3.1")
                android.set(false)
                outputToConsole.set(true)
                ignoreFailures.set(false)
                filter {
                    exclude("**/build/**")
                    exclude("**/generated/**")
                    exclude("**/build/generated/**")
                }
                reporters {
                    reporter(ReporterType.PLAIN)
                    reporter(ReporterType.CHECKSTYLE)
                }
            }

            extensions.configure<DetektExtension> {
                toolVersion = "1.23.7"
                buildUponDefaultConfig = true
                allRules = false
                parallel = true
                config.setFrom(rootProject.files("config/detekt/detekt.yml"))
                val baselineFile = file("detekt-baseline.xml")
                if (baselineFile.exists()) {
                    baseline = baselineFile
                }
            }

            // Default `detekt` task only sees JVM `src/main/kotlin`. KMP modules
            // ship code under `src/{commonMain,androidMain,iosMain,...}`. Wire a
            // single per-module Detekt task that walks every Kotlin source set
            // and add it to `detekt`'s graph so `./gradlew detekt` (and CI)
            // covers the real surface area.
            tasks.register<Detekt>("detektAllSourceSets") {
                description = "Runs detekt across all KMP source sets."
                group = "verification"
                parallel = true
                buildUponDefaultConfig = true
                config.setFrom(rootProject.files("config/detekt/detekt.yml"))
                val baselineFile = file("detekt-baseline.xml")
                if (baselineFile.exists()) {
                    baseline.set(baselineFile)
                }
                setSource(
                    files(
                        "src/commonMain/kotlin",
                        "src/androidMain/kotlin",
                        "src/iosMain/kotlin",
                        "src/iosArm64Main/kotlin",
                        "src/iosSimulatorArm64Main/kotlin",
                        "src/appleMain/kotlin",
                        "src/nativeMain/kotlin",
                    ),
                )
                include("**/*.kt", "**/*.kts")
                exclude("**/build/**", "**/generated/**", "**/resources/**")
                reports {
                    html.required.set(true)
                    xml.required.set(true)
                    txt.required.set(false)
                    sarif.required.set(false)
                    md.required.set(false)
                }
            }
            tasks.named("detekt").configure {
                dependsOn("detektAllSourceSets")
            }

            // Companion baseline task — `./gradlew detektBaselineAllSourceSets`
            // writes detekt-baseline.xml in this module covering every KMP
            // source set. Use to bulk-suppress pre-existing violations when
            // first wiring detekt in or after a major rule upgrade.
            tasks.register<DetektCreateBaselineTask>("detektBaselineAllSourceSets") {
                description = "Generates a detekt baseline across all KMP source sets."
                group = "verification"
                parallel.set(true)
                buildUponDefaultConfig.set(true)
                config.setFrom(rootProject.files("config/detekt/detekt.yml"))
                baseline.set(file("detekt-baseline.xml"))
                setSource(
                    files(
                        "src/commonMain/kotlin",
                        "src/androidMain/kotlin",
                        "src/iosMain/kotlin",
                        "src/iosArm64Main/kotlin",
                        "src/iosSimulatorArm64Main/kotlin",
                        "src/appleMain/kotlin",
                        "src/nativeMain/kotlin",
                    ),
                )
                include("**/*.kt", "**/*.kts")
                exclude("**/build/**", "**/generated/**", "**/resources/**")
            }

            // Detekt's classpath needs the Kotlin compiler embeddable jar; the
            // plugin handles that automatically. We just need to keep the JVM
            // target consistent so loaded source sets parse correctly.
            tasks.withType<Detekt>().configureEach {
                jvmTarget = "11"
            }
            tasks.withType<DetektCreateBaselineTask>().configureEach {
                jvmTarget = "11"
            }

            configureYallaPublishing()
        }
    }

    private fun defaultNamespace(project: Project): String {
        val base = "uz.yalla.sdk"
        val pathSegments = project.path
            .removePrefix(":")
            .split(":")
            .filter { it.isNotBlank() }
        return (listOf(base) + pathSegments).joinToString(".")
    }

    private fun defaultFrameworkBaseName(project: Project): String {
        val parts = project.path
            .removePrefix(":")
            .split(":")
            .filter { it.isNotBlank() }
        return "Yalla" + parts.joinToString(separator = "") { segment ->
            segment.replaceFirstChar { c -> c.uppercase() }
        }
    }
}
