package uz.yalla.platform

import uz.yalla.platform.model.IconType

fun IconType.toAssetName(): String =
    when (this) {
        IconType.MENU -> "ic_menu"
        IconType.CLOSE -> "ic_close"
        IconType.DONE -> "ic_done_tick"
        IconType.Back -> "ic_arrow_back"
        IconType.FOCUS_LOCATION -> "ic_focus_location"
        IconType.FOCUS_ROUTE -> "ic_focus_route"
        IconType.FOCUS_ORIGIN -> "ic_focus_origin"
        IconType.FOCUS_DESTINATION -> "ic_focus_destination"
    }
