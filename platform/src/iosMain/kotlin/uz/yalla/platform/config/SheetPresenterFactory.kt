package uz.yalla.platform.config

import platform.UIKit.UIViewController

/**
 * Factory for presenting and managing native iOS bottom sheets.
 *
 * Implement this interface in Swift using `UISheetPresentationController` (iOS 15+)
 * to provide native sheet behavior (detents, grabber, dimming). Register the
 * implementation via [IosPlatformConfig.Builder.sheetPresenter].
 *
 * The Kotlin [SheetPresenter][uz.yalla.platform.sheet.SheetPresenter] calls these
 * methods to manage the sheet lifecycle; the Swift side owns the actual presentation.
 *
 * @see IosPlatformConfig
 * @see uz.yalla.platform.sheet.NativeSheet
 * @since 0.0.1
 */
interface SheetPresenterFactory {
    /**
     * Presents a sheet from the given [parent] view controller.
     *
     * @param parent The presenting view controller (topmost in the presentation chain).
     * @param controller The [UIViewController] containing Compose content to present.
     * @param cornerRadius Corner radius in points for the sheet surface.
     * @param backgroundColor ARGB [Long] color for the sheet background.
     * @param onDismiss Callback to invoke when the sheet is dismissed (programmatically or by user).
     * @param onPresented Callback to invoke after the present animation completes.
     */
    fun present(
        parent: UIViewController,
        controller: UIViewController,
        cornerRadius: Double,
        backgroundColor: Long,
        onDismiss: () -> Unit,
        onPresented: () -> Unit,
    )

    /**
     * Updates the sheet's custom detent height to match the measured Compose content.
     *
     * @param controller The presented sheet view controller.
     * @param height The measured content height in points.
     */
    fun updateHeight(controller: UIViewController, height: Double)

    /**
     * Updates the sheet's background color while it is visible.
     *
     * @param controller The presented sheet view controller.
     * @param backgroundColor New ARGB [Long] background color.
     */
    fun updateBackground(controller: UIViewController, backgroundColor: Long)

    /**
     * Updates whether the user can interactively dismiss the sheet.
     *
     * @param controller The presented sheet view controller.
     * @param dismissEnabled `true` to allow swipe-to-dismiss, `false` to block it.
     * @param onDismissAttempt Callback invoked when the user tries to dismiss while disabled.
     */
    fun updateDismissBehavior(
        controller: UIViewController,
        dismissEnabled: Boolean,
        onDismissAttempt: () -> Unit,
    )

    /**
     * Dismisses the sheet.
     *
     * @param controller The presented sheet view controller.
     * @param animated Whether to animate the dismissal.
     */
    fun dismiss(controller: UIViewController, animated: Boolean)
}
