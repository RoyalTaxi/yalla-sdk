@file:OptIn(ExperimentalComposeUiApi::class)

package uz.yalla.platform.sheet

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIModalPresentationPageSheet
import platform.UIKit.UIViewController
import uz.yalla.platform.config.SheetPresenterFactory
import uz.yalla.platform.config.ThemeProvider
import kotlin.math.abs

internal class SheetPresenter(
    private val factory: SheetPresenterFactory,
    private val onDismissedByUser: () -> Unit,
) {
    private var controller: UIViewController? = null
    private var isProgrammaticDismiss = false
    private var lastMeasuredHeight = 0.0
    private var hasMeasuredHeight = false
    private var dismissEnabled = true
    private var onDismissAttempt: () -> Unit = {}

    fun present(
        parent: UIViewController,
        themeProvider: ThemeProvider?,
        backgroundColor: Long = 0xFFFFFFFF,
        onPresented: () -> Unit = {},
        content: @Composable () -> Unit,
    ) {
        controller?.let { dismiss(animated = false) }
        resetMeasurements()
        val parentController = parent.topPresentedController() ?: return

        val host =
            createComposeController(
                themeProvider = themeProvider,
                content = content,
            )

        controller = host

        factory.present(
            parentController,
            host,
            CORNER_RADIUS,
            backgroundColor,
            { handleDismissCallback() },
            onPresented
        )
        updateDismissBehavior(dismissEnabled, onDismissAttempt)
    }

    fun dismiss(animated: Boolean = true) {
        val ctrl = controller ?: return
        isProgrammaticDismiss = true
        controller = null

        factory.dismiss(ctrl, animated)

        // BUG-4 fix: do NOT reset isProgrammaticDismiss synchronously.
        // factory.dismiss() is async when animated — the onDismiss callback
        // fires after the animation completes. If we reset the flag here,
        // handleDismissCallback() would see isProgrammaticDismiss=false and
        // wrongly invoke onDismissedByUser(). Instead, let
        // handleDismissCallback() reset the flag after it runs.
    }

    fun updateBackground(backgroundColor: Long) {
        controller?.let { factory.updateBackground(it, backgroundColor) }
    }

    fun updateDismissBehavior(
        dismissEnabled: Boolean,
        onDismissAttempt: () -> Unit,
    ) {
        this.dismissEnabled = dismissEnabled
        this.onDismissAttempt = onDismissAttempt
        controller?.let { factory.updateDismissBehavior(it, dismissEnabled, onDismissAttempt) }
    }

    private fun createComposeController(
        themeProvider: ThemeProvider?,
        content: @Composable () -> Unit,
    ): UIViewController =
        ComposeUIViewController(
            configure = { opaque = false },
        ) {
            val themedContent: @Composable () -> Unit = { MeasuredContent(content) }
            if (themeProvider != null) {
                themeProvider.provide(themedContent)
            } else {
                themedContent()
            }
        }.apply {
            modalPresentationStyle = UIModalPresentationPageSheet
        }

    @Composable
    private fun MeasuredContent(content: @Composable () -> Unit) {
        val density = LocalDensity.current

        Box(
            modifier =
                Modifier.onSizeChanged { size ->
                    val heightPt = size.height / density.density.toDouble()
                    if (heightPt <= 0.0) return@onSizeChanged

                    val shouldUpdate =
                        !hasMeasuredHeight ||
                            abs(lastMeasuredHeight - heightPt) > HEIGHT_CHANGE_THRESHOLD

                    if (shouldUpdate) {
                        lastMeasuredHeight = heightPt
                        hasMeasuredHeight = true
                        controller?.let { factory.updateHeight(it, heightPt) }
                    }
                },
        ) {
            content()
        }
    }

    private fun resetMeasurements() {
        lastMeasuredHeight = 0.0
        hasMeasuredHeight = false
    }

    private fun handleDismissCallback() {
        if (!isProgrammaticDismiss) {
            controller = null
            onDismissedByUser()
        }
        // BUG-4 fix: reset flag here (after the check) instead of in dismiss().
        // This ensures the flag stays true for the entire async dismiss animation.
        isProgrammaticDismiss = false
    }

    private companion object {
        const val CORNER_RADIUS = 24.0
        const val HEIGHT_CHANGE_THRESHOLD = 20.0
    }
}

private fun UIViewController.topPresentedController(): UIViewController? {
    var current = this
    while (current.presentedViewController != null) {
        val presented = current.presentedViewController!!
        if (presented.isBeingDismissed() || presented.view.window == null) {
            break
        }
        current = presented
    }
    return if (current.isBeingDismissed()) null else current
}
