package uz.yalla.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertFalse
import org.junit.jupiter.api.Test

/**
 * Route-eating and projection live in the SDK model ([uz.yalla.maps.motion.DriverMotionModel] /
 * [uz.yalla.core.geo.RouteProgressGeometry]), and renderers/controllers are Humble Objects that draw
 * only what they are handed. This pins that boundary on the Android/common side: a `*Renderer` or
 * `*Controller` must not import the route geometry math, so the "route behind the car" policy cannot
 * silently re-scatter back into a renderer.
 *
 * Konsist sees only Kotlin sources, so the iOS Swift renderers (`IosMapRenderer`, the MapLibre /
 * Google annotation layers) cannot be checked here; the same invariant — renderers draw
 * `remainingRoute` / `connector` / pose from the model, never re-project GPS — is enforced for them by
 * manual review (called out in ADR 0003 and the renderer KDoc).
 */
class RendererPurityKonsistTest {
    @Test
    fun renderersAndControllersDoNotImportRouteGeometryMath() {
        Konsist
            .scopeFromProject()
            .files
            .filter { file ->
                val name = file.name
                name.endsWith("Renderer.kt") || name.endsWith("Controller.kt")
            }.assertFalse { file ->
                file.hasImport { import ->
                    FORBIDDEN_GEO_MATH_IMPORTS.any { forbidden -> import.name == forbidden }
                }
            }
    }

    private companion object {
        val FORBIDDEN_GEO_MATH_IMPORTS =
            listOf(
                "uz.yalla.core.geo.RouteProgressGeometry",
                "uz.yalla.core.geo.RouteGeometry",
                "uz.yalla.core.geo.routeProgress",
                "uz.yalla.core.geo.headingAlongRoute"
            )
    }
}
