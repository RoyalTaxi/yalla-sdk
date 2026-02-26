import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.kotlin.multiplatform.library")
                apply("org.jetbrains.kotlin.multiplatform")
                apply("org.jetbrains.kotlin.plugin.serialization")
                apply("maven-publish")
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

            extensions.configure<PublishingExtension> {
                repositories {
                    maven {
                        name = "GitHubPackages"
                        url = uri("https://maven.pkg.github.com/RoyalTaxi/yalla-sdk")
                        credentials {
                            username = System.getenv("GITHUB_ACTOR")
                                ?: project.findProperty("gpr.user") as? String
                            password = System.getenv("GITHUB_TOKEN")
                                ?: project.findProperty("gpr.key") as? String
                        }
                    }
                }
            }
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
