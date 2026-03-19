package uz.yalla.platform.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.KSerializer

/**
 * SDK-provided root component for navigation.
 *
 * Wraps Decompose's [childStack] with a generic route type [C] and exposes
 * a [Navigator] for screen-level navigation. Pass this to [NativeNavHost].
 *
 * ## Usage
 * ```kotlin
 * // Android
 * val root = NativeRootComponent<AppRoute>(
 *     componentContext = defaultComponentContext(),
 *     initialRoute = AppRoute.Home,
 *     serializer = AppRoute.serializer(),
 * )
 * ```
 *
 * @param C Concrete route type (sealed class extending [Route]).
 * @param componentContext Decompose lifecycle context — obtain via `defaultComponentContext()`.
 * @param initialRoute First screen shown when the app starts.
 * @param serializer Kotlin serialization serializer for [C]. Enables state preservation.
 *   Pass `null` to disable.
 * @since 0.0.5
 */
class NativeRootComponent<C : Route>(
    componentContext: ComponentContext,
    initialRoute: C,
    serializer: KSerializer<C>?,
) : ComponentContext by componentContext {

    internal val navigation = StackNavigation<C>()

    /** Observable navigation stack. */
    val childStack: Value<ChildStack<C, ComponentContext>> = childStack(
        source = navigation,
        serializer = serializer,
        initialConfiguration = initialRoute,
        handleBackButton = true,
        childFactory = { _, childContext -> childContext },
    )

    /** Navigation controller for pushing, popping, and replacing routes. */
    val navigator: Navigator = NavigatorImpl(navigation, childStack)
}
