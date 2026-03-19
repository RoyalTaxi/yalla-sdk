package uz.yalla.platform.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.StateFlow

/**
 * Cross-platform navigation controller.
 *
 * Thin abstraction over Decompose's `StackNavigation` that screens use to
 * push, pop, and replace routes. Access inside any screen via [LocalNavigator].
 *
 * All operations are synchronous and execute on the calling thread (main thread).
 *
 * @since 0.0.5
 */
interface Navigator {
    /**
     * Push a new route onto the navigation stack.
     * On iOS this triggers a native UINavigationController push animation.
     */
    fun push(route: Route)

    /** Pop the current route. No-op if at root. */
    fun pop()

    /**
     * Pop routes while [predicate] returns true.
     * Matches Decompose's `popWhile` semantics — pops from the top of the stack
     * until the predicate returns false for the new top.
     */
    fun popWhile(predicate: (Route) -> Boolean)

    /**
     * Replace the entire stack with a single [route] as the new root.
     * Useful for auth flows (e.g., login → home with no back stack).
     */
    fun setRoot(route: Route)

    /**
     * Replace the current top-of-stack route with [route].
     * The back stack beneath is unchanged.
     */
    fun replaceCurrent(route: Route)

    /** Whether the stack has more than one route (i.e., back navigation is possible). */
    val canGoBack: StateFlow<Boolean>

    /** The currently active route (top of stack). Non-nullable — stack always has ≥1 item. */
    val currentRoute: StateFlow<Route>
}

/**
 * CompositionLocal providing the [Navigator] inside a [NativeNavHost].
 * Throws if accessed outside of [NativeNavHost].
 *
 * @since 0.0.5
 */
val LocalNavigator = staticCompositionLocalOf<Navigator> {
    error("No Navigator provided. Ensure you are inside a NativeNavHost.")
}
