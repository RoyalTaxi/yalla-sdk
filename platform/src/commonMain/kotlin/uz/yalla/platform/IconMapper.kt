package uz.yalla.platform

import androidx.compose.ui.graphics.vector.ImageVector
import uz.yalla.platform.model.IconType
import uz.yalla.resources.icons.ArrowLeft
import uz.yalla.resources.icons.Check
import uz.yalla.resources.icons.FocusDestination
import uz.yalla.resources.icons.FocusLocation
import uz.yalla.resources.icons.FocusOrigin
import uz.yalla.resources.icons.FocusRoute
import uz.yalla.resources.icons.Menu
import uz.yalla.resources.icons.X
import uz.yalla.resources.icons.YallaIcons

/**
 * Maps an [IconType] to its corresponding [ImageVector] from the Yalla icon set.
 *
 * Used internally by platform button composables to resolve the vector drawable
 * for each abstract icon identifier.
 *
 * @return The [ImageVector] resource for this icon type.
 * @since 0.0.1
 */
fun IconType.toImageVector(): ImageVector =
    when (this) {
        IconType.MENU -> YallaIcons.Menu
        IconType.CLOSE -> YallaIcons.X
        IconType.DONE -> YallaIcons.Check
        IconType.BACK -> YallaIcons.ArrowLeft
        IconType.FOCUS_LOCATION -> YallaIcons.FocusLocation
        IconType.FOCUS_ROUTE -> YallaIcons.FocusRoute
        IconType.FOCUS_ORIGIN -> YallaIcons.FocusOrigin
        IconType.FOCUS_DESTINATION -> YallaIcons.FocusDestination
    }
