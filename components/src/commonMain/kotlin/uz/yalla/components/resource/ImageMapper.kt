package uz.yalla.components.resource

import androidx.compose.ui.graphics.vector.ImageVector
import uz.yalla.resources.icons.Brush
import uz.yalla.resources.icons.Camera
import uz.yalla.resources.icons.FlagRu
import uz.yalla.resources.icons.FlagUz
import uz.yalla.resources.icons.Gallery
import uz.yalla.resources.icons.Humo
import uz.yalla.resources.icons.Logout
import uz.yalla.resources.icons.ThemeDark
import uz.yalla.resources.icons.ThemeLight
import uz.yalla.resources.icons.ThemeSystem
import uz.yalla.resources.icons.Trash
import uz.yalla.resources.icons.Uzcard
import uz.yalla.resources.icons.YallaIcons

public fun String.asImageVector(): ImageVector? =
    when (this) {
        "camera" -> YallaIcons.Camera
        "gallery" -> YallaIcons.Gallery
        "trash" -> YallaIcons.Trash
        "brush" -> YallaIcons.Brush
        "logout" -> YallaIcons.Logout
        "theme_light" -> YallaIcons.ThemeLight
        "theme_dark" -> YallaIcons.ThemeDark
        "theme_system" -> YallaIcons.ThemeSystem
        "flag_uz" -> YallaIcons.FlagUz
        "flag_ru" -> YallaIcons.FlagRu
        "humo" -> YallaIcons.Humo
        "uzcard" -> YallaIcons.Uzcard
        else -> null
    }
