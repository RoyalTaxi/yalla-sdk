package uz.yalla.platform.sheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import platform.UIKit.UIApplication
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
import uz.yalla.platform.LocalSheetPresenterFactory
import uz.yalla.platform.LocalThemeProvider

@Suppress("UNUSED_PARAMETER")
@Composable
actual fun NativeSheet(
    isVisible: Boolean,
    shape: Shape,
    containerColor: Color,
    onDismissRequest: () -> Unit,
    dismissEnabled: Boolean,
    onDismissAttempt: () -> Unit,
    isDark: Boolean?,
    onFullyExpanded: (() -> Unit)?,
    content: @Composable () -> Unit
) {
    val currentOnDismiss by rememberUpdatedState(onDismissRequest)
    val currentContent by rememberUpdatedState(content)
    val currentOnFullyExpanded by rememberUpdatedState(onFullyExpanded)

    val sheetFactory = LocalSheetPresenterFactory.current
    val themeProvider = LocalThemeProvider.current
    val backgroundColor = containerColor.toArgb().toLong()

    val presenter =
        remember(sheetFactory) {
            SheetPresenter(
                factory = sheetFactory,
                onDismissedByUser = { currentOnDismiss() }
            )
        }

    DisposableEffect(isVisible) {
        if (!isVisible) return@DisposableEffect onDispose {}

        val parent = findKeyWindowRootController() ?: return@DisposableEffect onDispose {}

        presenter.updateDismissBehavior(
            dismissEnabled = dismissEnabled,
            onDismissAttempt = onDismissAttempt
        )
        presenter.present(
            parent = parent,
            themeProvider = themeProvider,
            backgroundColor = backgroundColor,
            onPresented = { currentOnFullyExpanded?.invoke() },
            content = { currentContent() }
        )

        onDispose { presenter.dismiss(animated = true) }
    }

    // React to property changes while the sheet is visible.
    // isVisible is intentionally NOT a key — DisposableEffect handles present/dismiss.
    // Including isVisible would cause redundant updateBackground calls during the
    // presentation animation, contributing to visual flickering.
    LaunchedEffect(backgroundColor, dismissEnabled, onDismissAttempt) {
        if (isVisible) {
            presenter.updateDismissBehavior(
                dismissEnabled = dismissEnabled,
                onDismissAttempt = onDismissAttempt
            )
            presenter.updateBackground(backgroundColor)
        }
    }
}

@Suppress("UNCHECKED_CAST")
private fun findKeyWindowRootController(): UIViewController? {
    val scenes = UIApplication.sharedApplication.connectedScenes as? Set<*>

    val activeScene =
        scenes?.firstOrNull { scene ->
            (scene as? UIWindowScene)?.activationState == UISceneActivationStateForegroundActive
        } as? UIWindowScene

    val keyWindow =
        activeScene?.windows?.firstOrNull { window ->
            (window as? UIWindow)?.isKeyWindow() == true
        } as? UIWindow ?: UIApplication.sharedApplication.keyWindow

    return keyWindow?.rootViewController
}
