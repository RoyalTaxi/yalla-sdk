package uz.yalla.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertFalse
import org.junit.jupiter.api.Test

/**
 * G1 — :core purity gate.
 *
 * :core is the SDK's pure functional core: domain model + pure functions, no platform and no
 * framework. Two complementary checks keep it honest, each catching a different escape route:
 *
 *  1. [coreDependsOnNoOtherSdkLayer] — module-graph leakage. :core must not reach "upward" into
 *     any sibling SDK module (network, datastore, design, …). Konsist's [Layer] model makes :core
 *     the leaf: `doesNotDependOn(...)` fails the moment a `uz.yalla.core..` file imports another
 *     declared layer.
 *
 *  2. [coreHasNoPlatformOrFrameworkImports] — third-party/platform leakage. Even an import the
 *     layer model can't see (Android, Ktor, Kotlin/Native `platform.*`) breaks "pure & common".
 *     We scan the source directly so commonMain stays portable across every KMP target.
 *
 * Test-only; it ships no runtime code. The gate runs in CI under `:konsistTest:test`.
 */
class CorePurityKonsistTest {
    @Test
    fun coreDependsOnNoOtherSdkLayer() {
        Konsist
            .scopeFromProject()
            .assertArchitecture {
                val core = Layer("core", "uz.yalla.core..")

                // Every sibling SDK module that ships Kotlin. :resources is intentionally absent —
                // it's a resource-only module with no Kotlin in `uz.yalla.resources..`, and Konsist
                // rejects an empty layer. Declaring the rest is what gives the check teeth: a Layer
                // can only forbid dependencies on *other declared* layers.
                val network = Layer("network", "uz.yalla.network..")
                val datastore = Layer("datastore", "uz.yalla.datastore..")
                val design = Layer("design", "uz.yalla.design..")
                val foundation = Layer("foundation", "uz.yalla.foundation..")
                val capabilities = Layer("capabilities", "uz.yalla.capabilities..")
                val components = Layer("components", "uz.yalla.components..")
                val maps = Layer("maps", "uz.yalla.maps..")
                val media = Layer("media", "uz.yalla.media..")
                val telemetry = Layer("telemetry", "uz.yalla.telemetry..")

                // :core is the leaf. It must not reach "upward" into any sibling module. Stated as
                // doesNotDependOn (not dependsOnNothing) so the gate asserts only what it owns —
                // core's purity — without coupling to how the siblings depend on each other.
                core.doesNotDependOn(
                    network,
                    datastore,
                    design,
                    foundation,
                    capabilities,
                    components,
                    maps,
                    media,
                    telemetry
                )
            }
    }

    @Test
    fun coreHasNoPlatformOrFrameworkImports() {
        Konsist
            .scopeFromProject()
            .files
            .withPackage("uz.yalla.core..")
            .assertFalse { file ->
                file.hasImport { import ->
                    FORBIDDEN_IMPORT_PREFIXES.any { prefix -> import.name.startsWith(prefix) }
                }
            }
    }

    private companion object {
        // android.* / androidx.*  -> Android platform (must not leak into common/pure core)
        // io.ktor.*               -> networking framework belongs in :network, not :core
        // platform.*              -> Kotlin/Native Apple platform APIs break commonMain portability
        val FORBIDDEN_IMPORT_PREFIXES =
            listOf(
                "android.",
                "androidx.",
                "io.ktor.",
                "platform."
            )
    }
}
