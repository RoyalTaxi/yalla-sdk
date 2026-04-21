package uz.yalla.platform.config

import uz.yalla.platform.PlatformConfig
import uz.yalla.platform.YallaPlatform
import uz.yalla.platform.navigation.NavigationBarAppearance

/**
 * iOS platform configuration containing native component factories.
 * Use [Builder] to construct, then pass to [YallaPlatform.install].
 *
 * ## Usage
 * ```kotlin
 * YallaPlatform.install(
 *     IosPlatformConfig.Builder().apply {
 *         sheetPresenter = MySheetPresenterFactory()
 *         circleButton = MyCircleButtonFactory()
 *         squircleButton = MySquircleButtonFactory()
 *     }.build()
 * )
 * ```
 *
 * @param sheetPresenter Factory for presenting and managing native bottom sheets.
 * @param circleButton Factory for creating circle-shaped native icon buttons.
 * @param squircleButton Factory for creating squircle-shaped native icon buttons.
 * @param themeProvider Optional Compose theme wrapper for sheets outside the main tree. `null` to skip.
 * @param navigationBarAppearance Optional global navigation bar appearance. `null` for system defaults.
 * @since 0.0.1
 */
class IosPlatformConfig private constructor(
    val sheetPresenter: SheetPresenterFactory,
    val circleButton: CircleIconButtonFactory,
    val squircleButton: SquircleIconButtonFactory,
    val themeProvider: ThemeProvider? = null,
    val navigationBarAppearance: NavigationBarAppearance? = null,
) : PlatformConfig {

    /**
     * Builder for constructing an [IosPlatformConfig].
     *
     * Required properties: [sheetPresenter], [circleButton], [squircleButton].
     * Optional properties: [themeProvider], [navigationBarAppearance].
     *
     * @since 0.0.1
     */
    class Builder {
        /** Factory for presenting native bottom sheets. Required. */
        var sheetPresenter: SheetPresenterFactory? = null

        /** Factory for creating circular icon buttons. Required. */
        var circleButton: CircleIconButtonFactory? = null

        /** Factory for creating squircle (rounded rect) icon buttons. Required. */
        var squircleButton: SquircleIconButtonFactory? = null

        /** Optional Compose theme wrapper for sheets presented outside the Compose tree. */
        var themeProvider: ThemeProvider? = null

        /** Optional global navigation bar appearance. Applied once at [NativeNavHost] creation. */
        var navigationBarAppearance: NavigationBarAppearance? = null

        /**
         * Builds the [IosPlatformConfig], throwing if any required factory is missing.
         *
         * @return A fully configured [IosPlatformConfig].
         * @throws IllegalStateException if [sheetPresenter], [circleButton], or
         *   [squircleButton] is `null`.
         */
        fun build(): IosPlatformConfig = IosPlatformConfig(
            sheetPresenter = requireNotNull(sheetPresenter) {
                "sheetPresenter is required. Provide a SheetPresenterFactory implementation."
            },
            circleButton = requireNotNull(circleButton) {
                "circleButton is required. Provide a CircleIconButtonFactory implementation."
            },
            squircleButton = requireNotNull(squircleButton) {
                "squircleButton is required. Provide a SquircleIconButtonFactory implementation."
            },
            themeProvider = themeProvider,
            navigationBarAppearance = navigationBarAppearance,
        )
    }
}

/**
 * Retrieves the [IosPlatformConfig] from [YallaPlatform] or throws with clear installation instructions.
 *
 * Used internally by all iOS platform components (sheets, buttons, navigation).
 *
 * @return The registered [IosPlatformConfig].
 * @throws IllegalStateException if [YallaPlatform.install] has not been called.
 * @since 0.0.1
 */
internal fun requireIosConfig(): IosPlatformConfig =
    YallaPlatform.requireConfig<IosPlatformConfig>()
