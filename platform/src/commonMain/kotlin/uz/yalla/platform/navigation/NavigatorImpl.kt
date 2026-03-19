package uz.yalla.platform.navigation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.navigate
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popWhile
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * [Navigator] implementation backed by Decompose's [StackNavigation].
 *
 * Observes [childStack] changes to keep [canGoBack] and [currentRoute] in sync.
 *
 * @since 0.0.5
 */
internal class NavigatorImpl<C : Route>(
    private val navigation: StackNavigation<C>,
    childStack: Value<ChildStack<C, *>>,
) : Navigator {

    private val _canGoBack = MutableStateFlow(childStack.value.backStack.isNotEmpty())
    override val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    private val _currentRoute = MutableStateFlow<Route>(childStack.value.active.configuration)
    override val currentRoute: StateFlow<Route> = _currentRoute.asStateFlow()

    init {
        childStack.subscribe { stack ->
            _canGoBack.value = stack.backStack.isNotEmpty()
            _currentRoute.value = stack.active.configuration
        }
    }

    override fun push(route: Route) {
        @Suppress("UNCHECKED_CAST")
        navigation.push(route as C)
    }

    override fun pop() {
        navigation.pop()
    }

    override fun popWhile(predicate: (Route) -> Boolean) {
        navigation.popWhile { predicate(it) }
    }

    override fun setRoot(route: Route) {
        @Suppress("UNCHECKED_CAST")
        navigation.navigate { listOf(route as C) }
    }

    override fun replaceCurrent(route: Route) {
        @Suppress("UNCHECKED_CAST")
        navigation.replaceCurrent(route as C)
    }
}
