package uz.yalla.platform

import org.jetbrains.compose.resources.DrawableResource
import uz.yalla.platform.model.IconType
import uz.yalla.resources.Res
import uz.yalla.resources.ic_arrow_back
import uz.yalla.resources.ic_close
import uz.yalla.resources.ic_done_tick
import uz.yalla.resources.ic_focus_destination
import uz.yalla.resources.ic_focus_location
import uz.yalla.resources.ic_focus_origin
import uz.yalla.resources.ic_focus_route
import uz.yalla.resources.ic_menu

fun IconType.toDrawableResource(): DrawableResource =
    when (this) {
        IconType.MENU -> Res.drawable.ic_menu
        IconType.CLOSE -> Res.drawable.ic_close
        IconType.DONE -> Res.drawable.ic_done_tick
        IconType.Back -> Res.drawable.ic_arrow_back
        IconType.FOCUS_LOCATION -> Res.drawable.ic_focus_location
        IconType.FOCUS_ROUTE -> Res.drawable.ic_focus_route
        IconType.FOCUS_ORIGIN -> Res.drawable.ic_focus_origin
        IconType.FOCUS_DESTINATION -> Res.drawable.ic_focus_destination
    }
