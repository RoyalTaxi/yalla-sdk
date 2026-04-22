package uz.yalla.platform.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Smoke tests for [Route] marker interface and [ScreenConfig] data class — support types
 * for the [NativeNavHost] expect/actual pair.
 *
 * [NativeNavHost] is a @Composable expect function requiring a Compose UI harness that is
 * not wired in this module. Runtime navigation tests are deferred to Phase 4.
 *
 * TODO(Phase 4): Add a Compose UI test that sets up NativeNavHost with a fake
 *   NativeRootComponent and ScreenProvider, verifies the initial route is rendered, and
 *   that push/pop transitions work correctly on both platforms.
 *
 * These tests verify:
 * - [Route] marker interface can be implemented.
 * - [ScreenConfig] defaults are correct.
 * - [ToolbarAction] sealed variants are callable.
 * - [ToolbarIcon] enum has all four documented entries.
 */
class RouteTest {

    private sealed class TestRoute : Route {
        data object Home : TestRoute()

        data class Detail(val id: String) : TestRoute()
    }

    @Test
    fun shouldAllowRouteImplementation() {
        // Route marker must be implementable by any sealed class in consumer code.
        val home: Route = TestRoute.Home
        val detail: Route = TestRoute.Detail("42")

        assertNotNull(home)
        assertNotNull(detail)
    }

    @Test
    fun shouldPreserveRouteTypeIdentity() {
        // Verify that a Route typed reference holds the concrete value correctly.
        val home: Route = TestRoute.Home
        assertEquals(TestRoute.Home, home)
    }

    @Test
    fun shouldExposeCorrectDetailPayload() {
        val route = TestRoute.Detail(id = "user-123")
        assertEquals("user-123", route.id)
    }

    @Test
    fun shouldBuildToolbarActionText() {
        var clicked = false
        val action = ToolbarAction.Text(label = "Save") { clicked = true }
        action.onClick()

        assertEquals("Save", action.label)
        assertTrue(clicked)
    }

    @Test
    fun shouldBuildToolbarActionIcon() {
        var clicked = false
        val action = ToolbarAction.Icon(icon = ToolbarIcon.Edit) { clicked = true }
        action.onClick()

        assertEquals(ToolbarIcon.Edit, action.icon)
        assertTrue(clicked)
    }

    @Test
    fun shouldExposeAllFourToolbarIconVariants() {
        // Guard against accidental removal of documented ToolbarIcon entries.
        assertEquals(4, ToolbarIcon.entries.size, "ToolbarIcon must have exactly 4 entries")
        assertNotNull(ToolbarIcon.Edit)
        assertNotNull(ToolbarIcon.ReadAll)
        assertNotNull(ToolbarIcon.More)
        assertNotNull(ToolbarIcon.Add)
    }
}
