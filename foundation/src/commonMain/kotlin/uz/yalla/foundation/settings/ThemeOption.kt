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
 * @property kind Corresponding [ThemeKind] for persistence
 * @since 0.0.1
 */
sealed class ThemeOption(
    override val icon: ImageVector,
    override val name: StringResource,
    val kind: ThemeKind
) : Selectable {

    data object Light : ThemeOption(
        icon = YallaIcons.ThemeLight,
        name = Res.string.settings_theme_light,
        kind = ThemeKind.Light
    )

    data object Dark : ThemeOption(
        icon = YallaIcons.ThemeDark,
        name = Res.string.settings_theme_dark,
        kind = ThemeKind.Dark
    )

    data object System : ThemeOption(
        icon = YallaIcons.ThemeSystem,
        name = Res.string.settings_theme_system,
        kind = ThemeKind.System
    )

    companion object {
        /** All available theme options. @since 0.0.1 */
        val all = listOf(Light, Dark, System)

        /**
         * Resolves a [ThemeOption] from the persisted [ThemeKind].
         *
         * @since 0.0.1
         */
        fun from(kind: ThemeKind): ThemeOption =
            when (kind) {
                ThemeKind.Light -> Light
                ThemeKind.Dark -> Dark
                ThemeKind.System -> System
            }
    }
}
