package uz.yalla.platform.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitViewController
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.flow.collectLatest
import platform.UIKit.*
import uz.yalla.platform.config.requireIosConfig

/**
 * iOS actual for [NativeNavHost].
 *
 * Creates a [UINavigationController] managed by [UIKitNavigator] — a direct,
 * single-source-of-truth navigator with no bidirectional sync layer.
 *
 * [NativeRootComponent] is used only to read the initial route. Decompose's
 * ChildStack subscription is NOT used — UINavigationController owns the stack.
 *
 * @since 0.0.6
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun <C : Route> NativeNavHost(
    rootComponent: NativeRootComponent<C>,
    screenProvider: ScreenProvider<C>,
    modifier: Modifier,
) {
    val initialRoute = remember { rootComponent.childStack.value.active.configuration }
    val iosConfig = requireIosConfig()

    val (navController, navigator) = remember {
        val nc = UINavigationController()
        nc.navigationBar.prefersLargeTitles = true

        iosConfig.navigationBarAppearance?.let { applyAppearance(nc, it) }

        val nav = UIKitNavigator<C>(
            navController = nc,
            vcFactory = { route, nav -> createViewController(route, screenProvider, nav) },
            initialRoute = initialRoute,
        )
        nav.setInitial()

        nc to nav
    }

    // Expose navigator for ScreenFactory; clear on dispose to prevent retention.
    androidx.compose.runtime.DisposableEffect(navigator) {
        _iosNavigator = navigator
        onDispose { _iosNavigator = null }
    }

    CompositionLocalProvider(LocalNavigator provides navigator) {
        UIKitViewController(
            factory = { navController },
            modifier = modifier,
        )
    }
}

/** Navigator instance accessible from the app's entry point for session event handling. */
@Suppress("ObjectPropertyName")
var _iosNavigator: Navigator? = null
    internal set

// region ScreenContainerViewController

/**
 * Container [UIViewController] that hosts a [ComposeUIViewController] child
 * and manages navigation bar visibility per-screen via [ScreenConfig].
 */
@OptIn(ExperimentalForeignApi::class)
private class ScreenContainerViewController(
    private val hidesNavBar: Boolean,
    private val toolbarState: ToolbarState,
) : UIViewController(nibName = null, bundle = null) {

    override fun viewWillAppear(animated: Boolean) {
        super.viewWillAppear(animated)
        navigationController?.setNavigationBarHidden(hidesNavBar, animated = animated)
    }

    override fun viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        view.safeAreaInsets.useContents {
            val newPadding = androidx.compose.foundation.layout.PaddingValues(
                top = top.dp, bottom = bottom.dp, start = left.dp, end = right.dp,
            )
            if (toolbarState.contentPadding != newPadding) {
                toolbarState.contentPadding = newPadding
            }
        }
    }
}

// endregion

// region VC Factory

@OptIn(ExperimentalForeignApi::class)
private fun <C : Route> createViewController(
    route: C,
    screenProvider: ScreenProvider<C>,
    navigator: Navigator,
): UIViewController {
    val config = screenProvider.configFor(route)
    val toolbarState = ToolbarState()

    val containerVC = ScreenContainerViewController(
        hidesNavBar = !config.showsNavigationBar,
        toolbarState = toolbarState,
    )

    val composeVC = ComposeUIViewController {
        CompositionLocalProvider(LocalNavigator provides navigator) {
            screenProvider.Content(route, navigator, toolbarState)
        }

        LaunchedEffect(toolbarState) {
            snapshotFlow { toolbarState.actions }.collectLatest { actions ->
                syncToolbarActions(containerVC, actions)
            }
        }
    }

    containerVC.addChildViewController(composeVC)
    composeVC.view.setFrame(containerVC.view.bounds)
    composeVC.view.setAutoresizingMask(
        UIViewAutoresizingFlexibleWidth or UIViewAutoresizingFlexibleHeight
    )
    containerVC.view.addSubview(composeVC.view)
    composeVC.didMoveToParentViewController(containerVC)

    return containerVC
}

// endregion

// region Toolbar Sync

@OptIn(ExperimentalForeignApi::class)
private fun syncToolbarActions(vc: UIViewController, actions: List<ToolbarAction>) {
    if (actions.isEmpty()) {
        vc.navigationItem.rightBarButtonItems = null
        return
    }
    val items = actions.map { action ->
        when (action) {
            is ToolbarAction.Text -> {
                val item = UIBarButtonItem(
                    title = action.label,
                    style = UIBarButtonItemStyle.UIBarButtonItemStylePlain,
                    target = null,
                    action = null,
                )
                item.primaryAction = UIAction.actionWithHandler { _ -> action.onClick() }
                item
            }
            is ToolbarAction.Icon -> {
                val image = toolbarIconImage(action.icon)
                val item = UIBarButtonItem(
                    image = image,
                    style = UIBarButtonItemStyle.UIBarButtonItemStylePlain,
                    target = null,
                    action = null,
                )
                item.primaryAction = UIAction.actionWithHandler { _ -> action.onClick() }
                item
            }
        }
    }
    vc.navigationItem.rightBarButtonItems = items
}

private fun toolbarIconImage(icon: ToolbarIcon): UIImage? = when (icon) {
    ToolbarIcon.Edit -> UIImage.systemImageNamed("pencil")
    ToolbarIcon.ReadAll -> UIImage.systemImageNamed("envelope.open")
    ToolbarIcon.More -> UIImage.systemImageNamed("ellipsis")
    ToolbarIcon.Add -> UIImage.systemImageNamed("plus")
}

// endregion

// region Appearance

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
        val titleAttrs = mapOf<Any?, Any?>(NSForegroundColorAttributeName to titleColor)
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

private fun argbToUIColor(argb: Long): UIColor {
    val a = ((argb shr 24) and 0xFF) / 255.0
    val r = ((argb shr 16) and 0xFF) / 255.0
    val g = ((argb shr 8) and 0xFF) / 255.0
    val b = (argb and 0xFF) / 255.0
    return UIColor(red = r, green = g, blue = b, alpha = a)
}

// endregion
