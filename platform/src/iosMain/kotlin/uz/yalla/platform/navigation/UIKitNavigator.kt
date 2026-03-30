package uz.yalla.platform.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.UIKit.UINavigationController
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.darwin.NSObject

/**
 * iOS [Navigator] backed directly by [UINavigationController].
 *
 * UINavigationController is the **single source of truth** for the navigation stack.
 * No Decompose ChildStack subscription, no bidirectional sync — just direct UIKit calls.
 *
 * A parallel [routeStack] tracks which [Route] each VC represents, so [popWhile],
 * [canGoBack], and [currentRoute] work without querying UIKit internals.
 *
 * Swipe-back is detected via [UINavigationControllerDelegateProtocol.didShowViewController].
 *
 * @since 0.0.6
 */
internal class UIKitNavigator<C : Route>(
    private val navController: UINavigationController,
    private val vcFactory: (route: C, navigator: Navigator) -> UIViewController,
    initialRoute: C,
) : Navigator {

    private val routeStack = mutableListOf(initialRoute)

    private val _canGoBack = MutableStateFlow(false)
    override val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    private val _currentRoute = MutableStateFlow<Route>(initialRoute)
    override val currentRoute: StateFlow<Route> = _currentRoute.asStateFlow()

    private val delegate = SwipeBackDelegate()

    init {
        navController.delegate = delegate
    }

    /** Creates the initial VC and sets it on the navigation controller. */
    fun setInitial() {
        val route = routeStack.first()
        val vc = vcFactory(route, this)
        navController.setViewControllers(listOf(vc), animated = false)
    }

    override fun push(route: Route) {
        val typed = route.typed()
        if (typed in routeStack) return
        routeStack.add(typed)
        val vc = vcFactory(typed, this)
        navController.pushViewController(vc, animated = true)
        updateState()
    }

    override fun pop() {
        if (routeStack.size <= 1) return
        routeStack.removeLast()
        navController.popViewControllerAnimated(true)
        updateState()
    }

    override fun popWhile(predicate: (Route) -> Boolean) {
        while (routeStack.size > 1 && predicate(routeStack.last())) {
            routeStack.removeLast()
        }
        @Suppress("UNCHECKED_CAST")
        val trimmed = (navController.viewControllers as List<UIViewController>).take(routeStack.size)
        navController.setViewControllers(trimmed, animated = true)
        updateState()
    }

    override fun setRoot(route: Route) {
        val typed = route.typed()
        routeStack.clear()
        routeStack.add(typed)

        val vc = vcFactory(typed, this)
        navController.setViewControllers(listOf(vc), animated = false)
        updateState()
    }

    override fun replaceCurrent(route: Route) {
        val typed = route.typed()
        if (routeStack.isNotEmpty()) routeStack.removeLast()
        routeStack.add(typed)

        @Suppress("UNCHECKED_CAST")
        val vcs = (navController.viewControllers as List<UIViewController>).toMutableList()
        if (vcs.isNotEmpty()) vcs.removeLast()
        vcs.add(vcFactory(typed, this))
        navController.setViewControllers(vcs, animated = true)
        updateState()
    }

    @Suppress("UNCHECKED_CAST")
    private fun Route.typed(): C = this as C

    private fun updateState() {
        _canGoBack.value = routeStack.size > 1
        routeStack.lastOrNull()?.let { _currentRoute.value = it }
    }

    /**
     * Detects user-initiated swipe-back and trims [routeStack] to match UINav.
     * No Decompose call needed — UINav already popped the VC.
     */
    private inner class SwipeBackDelegate : NSObject(), UINavigationControllerDelegateProtocol {
        override fun navigationController(
            navigationController: UINavigationController,
            didShowViewController: UIViewController,
            animated: Boolean,
        ) {
            val vcCount = navigationController.viewControllers.count().toInt()
            if (vcCount < routeStack.size) {
                while (routeStack.size > vcCount) {
                    routeStack.removeLast()
                }
                updateState()
            }
        }
    }
}
