package uz.yalla.platform.navigation

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.value.MutableValue
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Serializable
private sealed class TestRoute : Route {
    @Serializable data object Home : TestRoute()

    @Serializable data object Detail : TestRoute()

    @Serializable data object Settings : TestRoute()
}

class NavigatorImplTest {

    @Test
    fun shouldReportCanGoBackFalseOnSingleItemStack() {
        val nav = StackNavigation<TestRoute>()
        val stack = MutableValue(createStack(TestRoute.Home))
        val navigator = NavigatorImpl(nav, stack)

        assertFalse(navigator.canGoBack.value)
    }

    @Test
    fun shouldReportCanGoBackTrueOnMultiItemStack() {
        val nav = StackNavigation<TestRoute>()
        val stack = MutableValue(createStack(TestRoute.Home, TestRoute.Detail))
        val navigator = NavigatorImpl(nav, stack)

        assertTrue(navigator.canGoBack.value)
    }

    @Test
    fun shouldReportCurrentRouteAsTopOfStack() {
        val nav = StackNavigation<TestRoute>()
        val stack = MutableValue(createStack(TestRoute.Home, TestRoute.Detail))
        val navigator = NavigatorImpl(nav, stack)

        assertEquals(TestRoute.Detail, navigator.currentRoute.value)
    }

    @Test
    fun shouldUpdateCanGoBackOnStackChange() {
        val nav = StackNavigation<TestRoute>()
        val stack = MutableValue(createStack(TestRoute.Home))
        val navigator = NavigatorImpl(nav, stack)

        assertFalse(navigator.canGoBack.value)

        stack.value = createStack(TestRoute.Home, TestRoute.Detail)
        assertTrue(navigator.canGoBack.value)
    }

    @Test
    fun shouldUpdateCurrentRouteOnStackChange() {
        val nav = StackNavigation<TestRoute>()
        val stack = MutableValue(createStack(TestRoute.Home))
        val navigator = NavigatorImpl(nav, stack)

        assertEquals(TestRoute.Home, navigator.currentRoute.value)

        stack.value = createStack(TestRoute.Home, TestRoute.Settings)
        assertEquals(TestRoute.Settings, navigator.currentRoute.value)
    }

    @Test
    fun shouldRevertCanGoBackOnPopToRoot() {
        val nav = StackNavigation<TestRoute>()
        val stack = MutableValue(createStack(TestRoute.Home, TestRoute.Detail))
        val navigator = NavigatorImpl(nav, stack)

        assertTrue(navigator.canGoBack.value)

        stack.value = createStack(TestRoute.Home)
        assertFalse(navigator.canGoBack.value)
    }

    @Test
    fun shouldTrackCurrentRouteAfterMultiplePushes() {
        val nav = StackNavigation<TestRoute>()
        val stack = MutableValue(createStack(TestRoute.Home))
        val navigator = NavigatorImpl(nav, stack)

        stack.value = createStack(TestRoute.Home, TestRoute.Detail)
        assertEquals(TestRoute.Detail, navigator.currentRoute.value)

        stack.value = createStack(TestRoute.Home, TestRoute.Detail, TestRoute.Settings)
        assertEquals(TestRoute.Settings, navigator.currentRoute.value)
        assertTrue(navigator.canGoBack.value)
    }

    @Test
    fun shouldTrackReplaceCurrentViaMutableValue() {
        val nav = StackNavigation<TestRoute>()
        val stack = MutableValue(createStack(TestRoute.Home, TestRoute.Detail))
        val navigator = NavigatorImpl(nav, stack)

        assertEquals(TestRoute.Detail, navigator.currentRoute.value)

        // Simulate replaceCurrent: same backStack, different active
        stack.value = createStack(TestRoute.Home, TestRoute.Settings)
        assertEquals(TestRoute.Settings, navigator.currentRoute.value)
        assertTrue(navigator.canGoBack.value)
    }

    @Test
    fun shouldTrackSetRootViaMutableValue() {
        val nav = StackNavigation<TestRoute>()
        val stack = MutableValue(createStack(TestRoute.Home, TestRoute.Detail))
        val navigator = NavigatorImpl(nav, stack)

        assertTrue(navigator.canGoBack.value)

        // Simulate setRoot: single-item stack
        stack.value = createStack(TestRoute.Settings)
        assertEquals(TestRoute.Settings, navigator.currentRoute.value)
        assertFalse(navigator.canGoBack.value)
    }

    private fun createStack(vararg routes: TestRoute): ChildStack<TestRoute, Unit> {
        require(routes.isNotEmpty()) { "At least one route is required" }
        val items = routes.map { Child.Created(configuration = it, instance = Unit) }
        return ChildStack(
            active = items.last(),
            backStack = items.dropLast(1),
        )
    }
}
