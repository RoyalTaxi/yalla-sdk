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
 */
interface SheetPresenterFactory {
    /**
     * Presents a sheet from the given [parent] view controller.
     *
     * @param onDismiss Callback to invoke when the sheet is dismissed (programmatically or by user).
     */
    fun present(
        parent: UIViewController,
        controller: UIViewController,
        cornerRadius: Double,
        backgroundColor: Long,
        onDismiss: () -> Unit,
        onPresented: () -> Unit
    )

    /**
     * Updates the sheet's custom detent height to match the measured Compose content.
     */
    fun updateHeight(
        controller: UIViewController,
        height: Double
    )

    /**
     * Updates the sheet's background color while it is visible.
     */
    fun updateBackground(
        controller: UIViewController,
        backgroundColor: Long
    )

    /**
     * Updates whether the user can interactively dismiss the sheet.
     *
     * @param dismissEnabled `true` to allow swipe-to-dismiss, `false` to block it.
     */
    fun updateDismissBehavior(
        controller: UIViewController,
        dismissEnabled: Boolean,
        onDismissAttempt: () -> Unit
    )

    /**
     * Dismisses the sheet.
     */
    fun dismiss(
        controller: UIViewController,
        animated: Boolean
    )
}
