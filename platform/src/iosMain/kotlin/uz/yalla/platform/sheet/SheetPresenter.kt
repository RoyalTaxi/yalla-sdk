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

/**
 * Manages the lifecycle of a single iOS native sheet presentation.
 *
 * Wraps a [SheetPresenterFactory] (provided by the Swift side) and handles:
 * - Presenting a [ComposeUIViewController] inside a native page sheet.
 * - Auto-measuring Compose content height and updating the sheet detent.
 * - Tracking programmatic vs. user-initiated dismiss to avoid double callbacks.
 * - Updating background color and dismiss behavior while the sheet is visible.
 *
 * Only one sheet can be active at a time; calling [present] while another is visible
 * will dismiss the previous one first (non-animated).
 *
 * @param factory Platform-provided factory for native sheet presentation and management.
 * @param onDismissedByUser Callback invoked only when the user dismisses the sheet interactively
 *   (swipe-down). Not called for programmatic [dismiss] calls.
 * @see NativeSheet
 * @see SheetPresenterFactory
 * @since 0.0.1
 */
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

    /**
     * Presents a new sheet from the given [parent] view controller.
     *
     * If a sheet is already presented, it is dismissed without animation before the new one
     * is shown. The Compose [content] is wrapped in a [ComposeUIViewController] with optional
     * [ThemeProvider] theming.
     *
     * @param parent The presenting view controller. The topmost presented controller in its
     *   chain is used as the actual presenter.
     * @param themeProvider Optional theme wrapper for the Compose content. `null` renders
     *   content without additional theming.
     * @param backgroundColor ARGB [Long] color for the sheet background. Default opaque white.
     * @param onPresented Callback invoked after the sheet finishes its present animation.
     * @param content Composable content to render inside the sheet.
     */
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

    /**
     * Dismisses the currently presented sheet.
     *
     * Sets [isProgrammaticDismiss] to `true` so that [handleDismissCallback] knows not to
     * invoke [onDismissedByUser]. The flag is reset inside [handleDismissCallback] after the
     * async dismiss animation completes (BUG-4 fix).
     *
     * @param animated Whether to animate the dismissal. Default `true`.
     */
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

    /**
     * Updates the sheet background color while the sheet is visible.
     *
     * @param backgroundColor New ARGB [Long] color value.
     */
    fun updateBackground(backgroundColor: Long) {
        controller?.let { factory.updateBackground(it, backgroundColor) }
    }

    /**
     * Updates whether the user can interactively dismiss the sheet.
     *
     * When [dismissEnabled] is `false`, swipe-down gestures are blocked and
     * [onDismissAttempt] is called instead, allowing the app to show a confirmation dialog.
     *
     * @param dismissEnabled Whether interactive dismiss is allowed.
     * @param onDismissAttempt Callback when the user tries to dismiss while it is disabled.
     */
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

    /**
     * Wrapper composable that measures the Compose content height and updates the native
     * sheet detent via [SheetPresenterFactory.updateHeight].
     *
     * Only triggers a height update when the measured height changes by more than
     * [HEIGHT_CHANGE_THRESHOLD] points, preventing unnecessary layout thrashing.
     *
     * @param content The actual sheet content composable to measure and display.
     */
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

/**
 * Traverses the presentation chain from this [UIViewController] to find the topmost
 * presented controller that is not being dismissed and whose view is still in the window.
 *
 * @return The topmost presented controller, or `null` if `this` is being dismissed.
 */
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
