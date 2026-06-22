package uz.yalla.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.jupiter.api.Test

class ComponentShapeKonsistTest {
    @Test
    fun everyColorsHolderHasMatchingDefaults() {
        val defaultsObjectNames =
            componentObjects()
                .withNameEndingWith(DEFAULTS)
                .map { it.name }
                .toSet()

        componentClasses()
            .withNameEndingWith(COLORS)
            .assertTrue(
                additionalMessage =
                    "A `*Colors` holder needs a sibling `*Defaults` object to " +
                        "build it from theme tokens (see components/README.md §2)."
            ) { colorsHolder ->
                val expectedDefaults = colorsHolder.name.removeSuffix(COLORS) + DEFAULTS
                expectedDefaults in defaultsObjectNames
            }
    }

    @Test
    fun everyHolderHasItsDefaultsFactory() {
        val factoriesByPrefix =
            componentObjects()
                .withNameEndingWith(DEFAULTS)
                .associate { defaults ->
                    val prefix = defaults.name.removeSuffix(DEFAULTS)
                    prefix to defaults.functions().map { it.name }.toSet()
                }

        componentClasses()
            .filter { HOLDER_SUFFIXES.any { suffix -> it.name.endsWith(suffix) } }
            .assertTrue(
                additionalMessage =
                    "A `*Colors`/`*Dimens`/`*Styles` holder needs its matching " +
                        "`XDefaults.colors()`/`dimens()`/`styles()` factory (see components/README.md §2)."
            ) { holder ->
                val suffix = HOLDER_SUFFIXES.first { holder.name.endsWith(it) }
                val prefix = holder.name.removeSuffix(suffix)
                val factory = suffix.lowercase()
                factoriesByPrefix[prefix]?.contains(factory) == true
            }
    }

    private companion object {
        const val COLORS = "Colors"
        const val DEFAULTS = "Defaults"

        val HOLDER_SUFFIXES = listOf("Colors", "Dimens", "Styles")

        fun componentClasses() = Konsist.scopeFromProject().classes().withPackage("uz.yalla.components..")

        fun componentObjects() = Konsist.scopeFromProject().objects().withPackage("uz.yalla.components..")
    }
}
