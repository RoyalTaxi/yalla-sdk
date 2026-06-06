package uz.yalla.sdk.buildlogic.plugins

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import uz.yalla.sdk.buildlogic.extensions.configureDokkaModuleDoc
import uz.yalla.sdk.buildlogic.extensions.configureYallaCoordinates
import uz.yalla.sdk.buildlogic.extensions.configureYallaPublishing
import uz.yalla.sdk.buildlogic.extensions.yallaFrameworkBaseName
import uz.yalla.sdk.buildlogic.extensions.yallaNamespace

class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.kotlin.multiplatform.library")
                apply("org.jetbrains.kotlin.multiplatform")
                apply("org.jetbrains.kotlin.plugin.serialization")
                apply("maven-publish")
                apply("org.jetbrains.dokka")
            }

            configureYallaCoordinates()

            val androidNamespace = yallaNamespace()
            val frameworkName = yallaFrameworkBaseName()

            extensions.configure<KotlinMultiplatformExtension> {
                targets.withType(KotlinMultiplatformAndroidLibraryTarget::class.java)
                    .configureEach {
                        namespace = androidNamespace
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
                        baseName = frameworkName
                        isStatic = true
                    }
                }
            }

            configureDokkaModuleDoc()
            configureYallaPublishing()
        }
    }
}
