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

public enum class ComponentImage(
    public val id: String
) {
    Camera("camera"),
    Gallery("gallery"),
    Trash("trash"),
    Brush("brush"),
    Logout("logout"),
    ThemeLight("theme_light"),
    ThemeDark("theme_dark"),
    ThemeSystem("theme_system"),
    FlagUz("flag_uz"),
    FlagRu("flag_ru"),
    Humo("humo"),
    Uzcard("uzcard");

    public companion object {
        public fun from(id: String?): ComponentImage? = entries.firstOrNull { it.id == id }
    }
}

public fun ComponentImage.asImageVector(): ImageVector =
    when (this) {
        ComponentImage.Camera -> YallaIcons.Camera
        ComponentImage.Gallery -> YallaIcons.Gallery
        ComponentImage.Trash -> YallaIcons.Trash
        ComponentImage.Brush -> YallaIcons.Brush
        ComponentImage.Logout -> YallaIcons.Logout
        ComponentImage.ThemeLight -> YallaIcons.ThemeLight
        ComponentImage.ThemeDark -> YallaIcons.ThemeDark
        ComponentImage.ThemeSystem -> YallaIcons.ThemeSystem
        ComponentImage.FlagUz -> YallaIcons.FlagUz
        ComponentImage.FlagRu -> YallaIcons.FlagRu
        ComponentImage.Humo -> YallaIcons.Humo
        ComponentImage.Uzcard -> YallaIcons.Uzcard
    }

internal fun String.asImageVector(): ImageVector? = ComponentImage.from(this)?.asImageVector()
