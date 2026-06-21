package uz.yalla.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertFalse
import org.junit.jupiter.api.Test

/**
 * G3 — :components primitives-purity gate.
 *
 * `uz.yalla.components.primitives..` are the generic, reusable UI building blocks (buttons, fields,
 * …). They must stay domain-AGNOSTIC: a primitive must never know about the app's business value
 * types (payment cards, orders, profiles, places, identity ids). Domain-aware widgets belong in the
 * `payment` / `composites` sub-areas, not in `primitives`.
 *
 * Today the primitives only touch `uz.yalla.core.util` (formatting helpers), which is foundational,
 * not domain. This gate locks that in: the moment a primitive imports a `core` DOMAIN package it
 * fails, so the layering can't silently erode the way `CardBrandPresentation` (legitimately, under
 * `components.payment`) reaches for `core.payment`. Foundational `core` packages (`util`, `geo`,
 * `result`, …) stay allowed — primitives may compute with coordinates/results, just not with
 * business entities.
 *
 * Test-only; ships no runtime code. Runs in CI under `:konsistTest:test`.
 */
class PrimitivesPurityKonsistTest {
    @Test
    fun primitivesDoNotImportCoreDomain() {
        Konsist
            .scopeFromProject()
            .files
            .withPackage("uz.yalla.components.primitives..")
            .assertFalse { file ->
                file.hasImport { import ->
                    FORBIDDEN_DOMAIN_PREFIXES.any { prefix -> import.name.startsWith(prefix) }
                }
            }
    }

    private companion object {
        // core business-domain value types — off-limits to generic primitives.
        val FORBIDDEN_DOMAIN_PREFIXES =
            listOf(
                "uz.yalla.core.payment",
                "uz.yalla.core.identity",
                "uz.yalla.core.order",
                "uz.yalla.core.profile",
                "uz.yalla.core.location"
            )
    }
}
