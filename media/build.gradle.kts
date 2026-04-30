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
            // Compose — YallaCamera / YallaGallery are @Composable + Modifier-
            // taking expect declarations; consumers need both transitively.
            api(compose.runtime)
            api(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material3)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.exifinterface)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.accompanist.permissions)
            implementation(libs.camera.camera2)
            implementation(libs.camera.lifecycle)
            implementation(libs.camera.view)
            implementation(libs.kotlinx.coroutines.guava)
            // Paging — Android-only feature surface (YallaGalleryPagingGrid,
            // YallaGalleryDataSource, YallaGalleryViewModel). The cross-
            // platform YallaGallery composable in commonMain returns
            // ByteArray?, no paging types.
            implementation(libs.paging.common)
            implementation(libs.paging.compose.common)
        }
    }
}
