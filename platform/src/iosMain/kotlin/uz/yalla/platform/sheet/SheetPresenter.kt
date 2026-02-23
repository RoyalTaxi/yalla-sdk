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
import uz.yalla.platform.SheetPresenterFactory
import kotlin.math.abs

internal typealias ThemeProvider = @Composable (@Composable () -> Unit) -> Unit

internal class SheetPresenter(
    private val factory: SheetPresenterFactory?,
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

        if (factory != null) {
            factory.present(
                parentController,
                host,
                CORNER_RADIUS,
                backgroundColor,
                { handleDismissCallback() },
                onPresented
            )
        } else {
            parentController.presentViewController(host, animated = true) { onPresented() }
        }
        updateDismissBehavior(dismissEnabled, onDismissAttempt)
    }

    fun dismiss(animated: Boolean = true) {
        val ctrl = controller ?: return
        isProgrammaticDismiss = true
        controller = null

        if (factory != null) {
            factory.dismiss(ctrl, animated)
        } else {
            ctrl.dismissViewControllerAnimated(animated, null)
        }

        isProgrammaticDismiss = false
    }

    fun updateBackground(backgroundColor: Long) {
        controller?.let { factory?.updateBackground(it, backgroundColor) }
    }

    fun updateDismissBehavior(
        dismissEnabled: Boolean,
        onDismissAttempt: () -> Unit,
    ) {
        this.dismissEnabled = dismissEnabled
        this.onDismissAttempt = onDismissAttempt
        controller?.let { factory?.updateDismissBehavior?.invoke(it, dismissEnabled, onDismissAttempt) }
    }

    private fun createComposeController(
        themeProvider: ThemeProvider?,
        content: @Composable () -> Unit,
    ): UIViewController =
        ComposeUIViewController(
            configure = { opaque = false },
        ) {
            val themedContent: @Composable () -> Unit = { MeasuredContent(content) }
            themeProvider?.invoke(themedContent) ?: themedContent()
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
                        controller?.let { factory?.updateHeight(it, heightPt) }
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
