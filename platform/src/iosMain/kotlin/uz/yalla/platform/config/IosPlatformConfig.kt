package uz.yalla.platform.config

import uz.yalla.platform.PlatformConfig
import uz.yalla.platform.YallaPlatform

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
 * @since 0.0.1
 */
class IosPlatformConfig private constructor(
    val sheetPresenter: SheetPresenterFactory,
    val circleButton: CircleIconButtonFactory,
    val squircleButton: SquircleIconButtonFactory,
    val themeProvider: ThemeProvider? = null,
) : PlatformConfig {

    class Builder {
        var sheetPresenter: SheetPresenterFactory? = null
        var circleButton: CircleIconButtonFactory? = null
        var squircleButton: SquircleIconButtonFactory? = null
        var themeProvider: ThemeProvider? = null

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
        )
    }
}

/** Retrieve the iOS config or throw. Used by all iOS platform components internally. */
internal fun requireIosConfig(): IosPlatformConfig =
    YallaPlatform.requireConfig<IosPlatformConfig>()
