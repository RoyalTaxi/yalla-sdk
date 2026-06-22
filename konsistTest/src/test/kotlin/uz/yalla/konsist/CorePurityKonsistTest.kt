package uz.yalla.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertFalse
import org.junit.jupiter.api.Test

class CorePurityKonsistTest {
    @Test
    fun coreDependsOnNoOtherSdkLayer() {
        Konsist
            .scopeFromProject()
            .assertArchitecture {
                val core = Layer("core", "uz.yalla.core..")

                val network = Layer("network", "uz.yalla.network..")
                val datastore = Layer("datastore", "uz.yalla.datastore..")
                val design = Layer("design", "uz.yalla.design..")
                val foundation = Layer("foundation", "uz.yalla.foundation..")
                val capabilities = Layer("capabilities", "uz.yalla.capabilities..")
                val components = Layer("components", "uz.yalla.components..")
                val maps = Layer("maps", "uz.yalla.maps..")
                val media = Layer("media", "uz.yalla.media..")
                val telemetry = Layer("telemetry", "uz.yalla.telemetry..")

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
        val FORBIDDEN_IMPORT_PREFIXES =
            listOf(
                "android.",
                "androidx.",
                "io.ktor.",
                "platform."
            )
    }
}
