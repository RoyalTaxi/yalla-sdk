package uz.yalla.foundation.settings

import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource
import uz.yalla.core.settings.ThemeKind
import uz.yalla.resources.Res
import uz.yalla.resources.icons.ThemeDark
import uz.yalla.resources.icons.ThemeLight
import uz.yalla.resources.icons.ThemeSystem
import uz.yalla.resources.icons.YallaIcons
import uz.yalla.resources.settings_theme_dark
import uz.yalla.resources.settings_theme_light
import uz.yalla.resources.settings_theme_system

/**
 * Theme option for settings screens.
 *
 * Sealed hierarchy mapping [ThemeKind] to display properties.
 *
 */
sealed class ThemeOption(
    override val icon: ImageVector,
    override val name: StringResource,
    val kind: ThemeKind
) : Selectable {

    /** Always uses light color scheme. */
    data object Light : ThemeOption(
        icon = YallaIcons.ThemeLight,
        name = Res.string.settings_theme_light,
        kind = ThemeKind.Light
    )

    /** Always uses dark color scheme. */
    data object Dark : ThemeOption(
        icon = YallaIcons.ThemeDark,
        name = Res.string.settings_theme_dark,
        kind = ThemeKind.Dark
    )

    /** Follows the OS dark/light setting. */
    data object System : ThemeOption(
        icon = YallaIcons.ThemeSystem,
        name = Res.string.settings_theme_system,
        kind = ThemeKind.System
    )

    companion object {
        val all = listOf(Light, Dark, System)

        fun from(kind: ThemeKind): ThemeOption =
            when (kind) {
                ThemeKind.Light -> Light
                ThemeKind.Dark -> Dark
                ThemeKind.System -> System
            }
    }
}
