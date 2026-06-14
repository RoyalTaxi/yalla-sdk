package uz.yalla.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.jupiter.api.Test

/**
 * G2 — :components shape gate.
 *
 * A configurable component `X` has a fixed configuration surface (see components/README.md):
 * `@Immutable data class` holders `XColors` / `XDimens` / `XStyles`, plus a single `XDefaults`
 * object whose `colors()` / `dimens()` / `styles()` factories are the only place theme tokens
 * are read. That shape was hand-copied across ~15 files and drifted — holders with no `Defaults`
 * to build them, `Defaults` exposing some factories but not others. The compiler can't see those
 * gaps; these checks can.
 *
 * Two structural rules, each catching one drift mode we actually hit:
 *
 *  1. [everyColorsHolderHasMatchingDefaults] — a `*Colors` holder with no sibling `*Defaults`
 *     object is a dead-end: nothing builds it from tokens. `Colors` is the anchor because it is
 *     the axis every restyleable component exposes (a `Dimens`-only component like BonusCard is
 *     deliberately fine — see "what is not mandatory" in the README).
 *
 *  2. [everyHolderHasItsDefaultsFactory] — a holder whose `Defaults` object lacks the matching
 *     factory (`XColors` but no `XDefaults.colors()`) is the "Defaults miss Colors/Styles" half-
 *     pair. The holder is unreachable through the convention's front door.
 *
 * Scoped to `uz.yalla.components..` so the rule constrains :components only. Structural by design:
 * it asserts the shape exists, not what's inside the holders. Tighten with a green run + a README
 * edit. Test-only; ships no runtime code. Runs in CI under `:konsistTest:test`.
 */
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
        // prefix -> the set of factory function names its Defaults object exposes
        // (e.g. "PrimaryButton" -> {"colors", "dimens", "styles"}).
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
                val factory = suffix.lowercase() // Colors -> colors, Dimens -> dimens, Styles -> styles
                factoriesByPrefix[prefix]?.contains(factory) == true
            }
    }

    private companion object {
        const val COLORS = "Colors"
        const val DEFAULTS = "Defaults"

        // Order matters only for first-match; the three suffixes are mutually exclusive on a name.
        val HOLDER_SUFFIXES = listOf("Colors", "Dimens", "Styles")

        // Scope every query to the components module so this gate constrains :components alone.
        fun componentClasses() = Konsist.scopeFromProject().classes().withPackage("uz.yalla.components..")

        fun componentObjects() = Konsist.scopeFromProject().objects().withPackage("uz.yalla.components..")
    }
}
