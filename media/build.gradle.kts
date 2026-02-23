import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("yalla.sdk.kmp.compose")
}

kotlin {
    targets.withType(KotlinMultiplatformAndroidLibraryTarget::class.java).configureEach {
        androidResources {
            enable = true
        }
    }

    targets.withType(KotlinNativeTarget::class.java).configureEach {
        compilations.getByName("main") {
            cinterops {
                val coremedia by creating {
                    defFile("src/nativeInterop/cinterop/coremedia.def")
                    compilerOpts("-framework", "CoreMedia")
                }
                val corevideo by creating {
                    defFile("src/nativeInterop/cinterop/corevideo.def")
                    compilerOpts("-framework", "CoreVideo")
                }
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(libs.paging.common)
            implementation(libs.paging.compose.common)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.exifinterface)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.accompanist.permissions)
            implementation(libs.camera.camera2)
            implementation(libs.camera.lifecycle)
            implementation(libs.camera.view)
            implementation(libs.kotlinx.coroutines.guava)
        }
    }
}
