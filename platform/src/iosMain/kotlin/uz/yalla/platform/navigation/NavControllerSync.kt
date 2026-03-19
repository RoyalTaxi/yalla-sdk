package uz.yalla.platform.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import platform.UIKit.UINavigationController
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.darwin.NSObject

/**
 * Bidirectional sync engine between Decompose's [ChildStack] and [UINavigationController].
 *
 * Two sync directions:
 * 1. **Decompose -> UINav**: When [childStack] changes (push/pop via [Navigator]),
 *    the corresponding push/pop is performed on [navController].
 * 2. **UINav -> Decompose**: When the user swipe-backs (iOS interactive pop gesture),
 *    the [NavDelegate] detects the reduced VC count and calls [StackNavigation.pop] to
 *    keep Decompose in sync.
 *
 * A re-entrancy guard [isSyncingFromNative] prevents infinite loops when a native
 * swipe-back triggers a Decompose pop, which would otherwise trigger another UINav pop.
 *
 * @param navController The UINavigationController to sync with.
 * @param childStack Decompose's observable navigation stack.
 * @param navigation Decompose's stack navigation for programmatic pop.
 * @param viewControllerFactory Creates a UIViewController for a given route and its ComponentContext.
 * @since 0.0.5
 */
internal class NavControllerSync<C : Route>(
    private val navController: UINavigationController,
    private val childStack: Value<ChildStack<C, ComponentContext>>,
    private val navigation: StackNavigation<C>,
    private val viewControllerFactory: (route: C, context: ComponentContext) -> UIViewController,
) {
    /** Re-entrancy guard: true while syncing a native swipe-back into Decompose. */
    private var isSyncingFromNative = false

    /** Tracks Decompose stack size to detect push vs pop. */
    private var currentStackSize = 0

    /** The delegate instance — must be retained to avoid deallocation. */
    private val delegate = NavDelegate()

    /** Start observing both sides. Call once after initial setup. */
    fun start() {
        navController.delegate = delegate

        childStack.subscribe { stack ->
            if (isSyncingFromNative) return@subscribe

            val items = stack.backStack.map { it.configuration to it.instance } +
                (stack.active.configuration to stack.active.instance)
            val newSize = items.size

            when {
                newSize > currentStackSize -> {
                    // Push: add the new top VC
                    val (route, context) = items.last()
                    val vc = viewControllerFactory(route, context)
                    navController.pushViewController(vc, animated = true)
                }
                newSize < currentStackSize -> {
                    // Pop: pop to the correct VC
                    val targetIndex = newSize - 1
                    val viewControllers = navController.viewControllers
                    if (targetIndex >= 0 && targetIndex < viewControllers.count().toInt()) {
                        val targetVC = viewControllers[targetIndex] as UIViewController
                        navController.popToViewController(targetVC, animated = true)
                    }
                }
                // newSize == currentStackSize -> replace, no UINav action needed
                // (the VC content updates via Compose recomposition)
            }

            currentStackSize = newSize
        }
    }

    /**
     * UINavigationController delegate that detects user-initiated swipe-back gestures.
     *
     * When `didShowViewController` fires and the UINav VC count is less than
     * [currentStackSize], the user performed a swipe-back that Decompose doesn't
     * know about yet. We sync by calling [StackNavigation.pop].
     */
    private inner class NavDelegate : NSObject(), UINavigationControllerDelegateProtocol {
        override fun navigationController(
            navigationController: UINavigationController,
            didShowViewController: UIViewController,
            animated: Boolean,
        ) {
            val vcCount = navigationController.viewControllers.count().toInt()
            if (vcCount < currentStackSize) {
                // User swiped back — sync Decompose
                isSyncingFromNative = true
                val popCount = currentStackSize - vcCount
                repeat(popCount) { navigation.pop() }
                currentStackSize = vcCount
                isSyncingFromNative = false
            }
        }
    }
}
