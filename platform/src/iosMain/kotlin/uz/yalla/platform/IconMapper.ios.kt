package uz.yalla.platform

import uz.yalla.platform.model.IconType

/**
 * Maps an [IconType] to its iOS asset catalog image name.
 *
 * Used by the native icon button factories
 * ([CircleIconButtonFactory][uz.yalla.platform.config.CircleIconButtonFactory],
 * [SquircleIconButtonFactory][uz.yalla.platform.config.SquircleIconButtonFactory])
 * to load the correct image from the Xcode asset catalog.
 *
 * @return The asset catalog image name string (e.g., `"ic_menu"`, `"ic_close"`).
 * @see toImageVector
 * @since 0.0.1
 */
fun IconType.toAssetName(): String =
    when (this) {
        IconType.MENU -> "ic_menu"
        IconType.CLOSE -> "ic_close"
        IconType.DONE -> "ic_done_tick"
        IconType.BACK -> "ic_arrow_back"
        IconType.FOCUS_LOCATION -> "ic_focus_location"
        IconType.FOCUS_ROUTE -> "ic_focus_route"
        IconType.FOCUS_ORIGIN -> "ic_focus_origin"
        IconType.FOCUS_DESTINATION -> "ic_focus_destination"
    }
