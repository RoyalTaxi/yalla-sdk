package uz.yalla.platform.config

import platform.UIKit.UIViewController

/**
 * Factory for presenting and managing native iOS sheets.
 * Implement in Swift and register via [IosPlatformConfig.Builder].
 * @since 0.0.1
 */
interface SheetPresenterFactory {
    fun present(
        parent: UIViewController,
        controller: UIViewController,
        cornerRadius: Double,
        backgroundColor: Long,
        onDismiss: () -> Unit,
        onPresented: () -> Unit,
    )
    fun updateHeight(controller: UIViewController, height: Double)
    fun updateBackground(controller: UIViewController, backgroundColor: Long)
    fun updateDismissBehavior(
        controller: UIViewController,
        dismissEnabled: Boolean,
        onDismissAttempt: () -> Unit,
    )
    fun dismiss(controller: UIViewController, animated: Boolean)
}
