// Component catalog — Storytale-powered wasmJs gallery, NOT published.
//
// Strategy:
//   - Use raw plugins (NOT yalla.sdk.kmp convention) so detekt/dokka don't enter the
//     buildscript classpath and risk kotlin-compiler-embeddable skew.
//   - srcDir SDK source files (primitives/composites/design/core) directly into the catalog's
//     wasmJs compilation, so SDK modules don't need their own wasmJs targets and
//     their published artifacts stay Android+iOS only.
//   - :resources is the one exception — it added `wasmJs { browser() }` so the
//     catalog can consume Compose Resources (icons/fonts/strings) via a normal
//     project dependency, avoiding the gnarly cross-module composeResources copy.
//   - Platform-native seams used by primitives/composites keep the same expect/actual
//     shape as :platform. The catalog supplies wasmJs actuals that mirror Android's
//     Material implementations where the web target exposes the same APIs.
//   - The one composite source excluded is DeviceConnectivityState: its backing
//     library has no wasmJs publication. DatePickerSheet is also excluded because
//     it is expect-only in common source.
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.storytale)
}

kotlin {
    wasmJs {
        // Storytale's generated index.html hardcodes a `<script src="composeApp.js">` tag,
        // so the webpack output filename has to be exactly `composeApp.js` for the deployed
        // /components/ page to load. The Kotlin module name stays as the project name.
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
    }

    // SDK source srcDir'd here uses @OptIn-style APIs (kotlin.time.Clock,
    // ExperimentalMaterial3Api, etc.) that the source-owning modules opt into via per-file
    // annotations or build-script compiler args. Mirror the broadest opt-ins so the same
    // source compiles cleanly under the catalog's wasmJs target.
    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.addAll(
                        "-opt-in=kotlin.time.ExperimentalTime",
                        "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                        "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
                        "-opt-in=androidx.compose.foundation.layout.ExperimentalLayoutApi",
                    )
                }
            }
        }
    }

    sourceSets {
        commonMain {
            kotlin.srcDirs(
                "../core/src/commonMain/kotlin",
                "../design/src/commonMain/kotlin",
                "../primitives/src/commonMain/kotlin/uz/yalla/primitives/button",
                "../primitives/src/commonMain/kotlin/uz/yalla/primitives/field",
                "../primitives/src/commonMain/kotlin/uz/yalla/primitives/indicator",
                "../primitives/src/commonMain/kotlin/uz/yalla/primitives/dialog",
                "../primitives/src/commonMain/kotlin/uz/yalla/primitives/skeleton",
                "../primitives/src/commonMain/kotlin/uz/yalla/primitives/topbar",
                "../primitives/src/commonMain/kotlin/uz/yalla/primitives/otp",
                "../primitives/src/commonMain/kotlin/uz/yalla/primitives/pin",
                "../primitives/src/commonMain/kotlin/uz/yalla/primitives/rating",
                "../primitives/src/commonMain/kotlin/uz/yalla/primitives/transformation",
                "../primitives/src/commonMain/kotlin/uz/yalla/primitives/util",
                "../composites/src/commonMain/kotlin/uz/yalla/composites/card",
                "../composites/src/commonMain/kotlin/uz/yalla/composites/drawer",
                "../composites/src/commonMain/kotlin/uz/yalla/composites/item",
                "../composites/src/commonMain/kotlin/uz/yalla/composites/sheet",
                "../composites/src/commonMain/kotlin/uz/yalla/composites/snackbar",
                "../composites/src/commonMain/kotlin/uz/yalla/composites/util",
                "../composites/src/commonMain/kotlin/uz/yalla/composites/view",
            )
            kotlin.exclude(
                "**/DatePickerSheet.kt",
                "**/DeviceConnectivityState.kt",
            )

            dependencies {
                implementation(projects.resources)

                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.ui)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.components.resources)
                implementation(libs.compose.ui.tooling.preview)

                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.compottie)
                implementation(libs.compottie.resources)
                implementation(libs.constraintlayout)

                // primitives' SensitiveButton uses repeatOnLifecycle + LocalLifecycleOwner
                // from the multiplatform Jetpack Lifecycle artifact.
                implementation(libs.androidx.lifecycle.runtime.compose)
            }
        }
    }
}
