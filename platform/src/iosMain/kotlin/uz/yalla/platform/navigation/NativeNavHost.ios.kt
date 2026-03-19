package uz.yalla.platform.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitViewController
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.*
import uz.yalla.platform.config.requireIosConfig

/**
 * iOS actual for [NativeNavHost].
 *
 * Creates a [UINavigationController], embeds it via [UIKitViewController],
 * and uses [NavControllerSync] to keep Decompose's stack and UINavigationController
 * in bidirectional sync. Each route gets its own [ComposeUIViewController].
 *
 * @since 0.0.5
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun <C : Route> NativeNavHost(
    rootComponent: NativeRootComponent<C>,
    screenProvider: ScreenProvider<C>,
    modifier: Modifier,
) {
    val iosConfig = requireIosConfig()
    val appearance = iosConfig.navigationBarAppearance

    val navController = remember {
        val initialStack = rootComponent.childStack.value
        val initialRoute = initialStack.active.configuration
        val initialContext = initialStack.active.instance
        val initialVC = createViewController(initialRoute, initialContext, screenProvider, rootComponent.navigator)

        val nc = UINavigationController(rootViewController = initialVC)
        nc.navigationBar.prefersLargeTitles = true

        if (appearance != null) {
            applyAppearance(nc, appearance)
        }

        val sync = NavControllerSync(
            navController = nc,
            childStack = rootComponent.childStack,
            navigation = rootComponent.navigation,
            viewControllerFactory = { route, context ->
                createViewController(route, context, screenProvider, rootComponent.navigator)
            },
        )
        sync.start()

        nc
    }

    UIKitViewController(
        factory = { navController },
        modifier = modifier,
    )
}

/**
 * Creates a container [UIViewController] hosting a [ComposeUIViewController] child,
 * with navigation item configured (title, large title mode).
 *
 * We use a container VC because [ComposeUIViewController] returns an opaque type
 * whose `navigationItem` is not directly accessible in Kotlin/Native interop.
 * The container VC's own `navigationItem` is fully accessible and configurable.
 */
@OptIn(ExperimentalForeignApi::class)
private fun <C : Route> createViewController(
    route: C,
    context: com.arkivanov.decompose.ComponentContext,
    screenProvider: ScreenProvider<C>,
    navigator: Navigator,
): UIViewController {
    val config = screenProvider.configFor(route)
    val toolbarState = ToolbarState()

    val composeVC = ComposeUIViewController {
        CompositionLocalProvider(LocalNavigator provides navigator) {
            screenProvider.Content(route, navigator, toolbarState)
        }
    }

    // Wrap in a plain UIViewController so we can set navigationItem properties.
    val containerVC = UIViewController()
    containerVC.addChildViewController(composeVC)
    composeVC.view.setFrame(containerVC.view.bounds)
    composeVC.view.setAutoresizingMask(
        UIViewAutoresizingFlexibleWidth or UIViewAutoresizingFlexibleHeight
    )
    containerVC.view.addSubview(composeVC.view)
    composeVC.didMoveToParentViewController(containerVC)

    containerVC.navigationItem.title = config.title
    containerVC.navigationItem.setLargeTitleDisplayMode(
        when (config.largeTitleMode) {
            LargeTitleMode.Always -> UINavigationItemLargeTitleDisplayMode.UINavigationItemLargeTitleDisplayModeAlways
            LargeTitleMode.Never -> UINavigationItemLargeTitleDisplayMode.UINavigationItemLargeTitleDisplayModeNever
        }
    )

    return containerVC
}

/**
 * Applies [NavigationBarAppearance] to the [UINavigationController]'s navigation bar.
 * Configures both standard and scroll-edge appearances for consistency.
 */
private fun applyAppearance(
    navController: UINavigationController,
    appearance: NavigationBarAppearance,
) {
    val bar = navController.navigationBar
    val barAppearance = UINavigationBarAppearance()

    if (!appearance.showsSeparator) {
        barAppearance.shadowColor = null
    }

    if (appearance.backgroundColor != 0L) {
        barAppearance.backgroundColor = argbToUIColor(appearance.backgroundColor)
    }

    if (appearance.titleColor != 0L) {
        val titleColor = argbToUIColor(appearance.titleColor)
        val titleAttrs = mapOf<Any?, Any?>(
            NSForegroundColorAttributeName to titleColor,
        )
        barAppearance.titleTextAttributes = titleAttrs
        barAppearance.largeTitleTextAttributes = titleAttrs
    }

    if (appearance.titleFontName != null && appearance.titleColor != 0L) {
        val font = UIFont.fontWithName(appearance.titleFontName, size = 17.0)
        if (font != null) {
            val attrs = mapOf<Any?, Any?>(
                "NSFont" to font,
                NSForegroundColorAttributeName to argbToUIColor(appearance.titleColor),
            )
            barAppearance.titleTextAttributes = attrs
        }
    }

    if (appearance.largeTitleFontName != null && appearance.titleColor != 0L) {
        val font = UIFont.fontWithName(appearance.largeTitleFontName, size = 34.0)
        if (font != null) {
            val attrs = mapOf<Any?, Any?>(
                "NSFont" to font,
                NSForegroundColorAttributeName to argbToUIColor(appearance.titleColor),
            )
            barAppearance.largeTitleTextAttributes = attrs
        }
    }

    bar.standardAppearance = barAppearance
    bar.scrollEdgeAppearance = barAppearance
    bar.setTranslucent(appearance.isTranslucent)

    if (appearance.tintColor != 0L) {
        bar.tintColor = argbToUIColor(appearance.tintColor)
    }
}

/** Converts an ARGB [Long] (0xAARRGGBB) to [UIColor]. */
private fun argbToUIColor(argb: Long): UIColor {
    val a = ((argb shr 24) and 0xFF) / 255.0
    val r = ((argb shr 16) and 0xFF) / 255.0
    val g = ((argb shr 8) and 0xFF) / 255.0
    val b = (argb and 0xFF) / 255.0
    return UIColor(red = r, green = g, blue = b, alpha = a)
}
