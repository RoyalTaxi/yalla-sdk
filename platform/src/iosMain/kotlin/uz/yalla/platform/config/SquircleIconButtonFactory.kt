package uz.yalla.platform.config

import platform.UIKit.UIViewController

/**
 * Factory for creating squircle-shaped (continuous rounded rectangle) native iOS icon buttons.
 *
 * Implement this interface in Swift to provide a UIKit-backed squircle button
 * (e.g., using `UIButton` with a continuous corner curve via `layer.cornerCurve = .continuous`).
 * The returned [UIViewController] is embedded inside a Compose [UIKitViewController]
 * interop wrapper.
 *
 * Register via [IosPlatformConfig.Builder.squircleButton].
 *
 * @see IosPlatformConfig
 * @since 0.0.1
 */
interface SquircleIconButtonFactory {
    /**
     * Creates a squircle icon button view controller.
     *
     * @param icon The iOS asset catalog image name (e.g., `"ic_menu"`, `"ic_close"`).
     *   Mapped from [IconType][uz.yalla.platform.model.IconType] via
     *   [toAssetName][uz.yalla.platform.toAssetName].
     * @param onClick Callback invoked when the button is tapped.
     * @param borderWidth Border width in points. `0.0` for no border.
     * @param borderColor ARGB [Long] color for the border. Ignored when [borderWidth] is `0.0`.
     * @return A [UIViewController] whose view renders the squircle button.
     */
    fun create(
        icon: String,
        onClick: () -> Unit,
        borderWidth: Double,
        borderColor: Long,
    ): UIViewController
}
