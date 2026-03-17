package uz.yalla.foundation.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
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
 * UI-ready theme model for settings screens.
 *
 * Sealed hierarchy mapping [ThemeKind] to display properties (icon, localized name).
 *
 * @property icon Theme icon as [ImageVector]
 * @property name Localized theme name
 * @property themeKind Corresponding [ThemeKind] for persistence
 * @since 0.0.1
 */
sealed class ThemeModel(
    val icon: ImageVector,
    val name: StringResource,
    val themeKind: ThemeKind
) {
    data object Light : ThemeModel(
        icon = YallaIcons.ThemeLight,
        name = Res.string.settings_theme_light,
        themeKind = ThemeKind.Light
    )

    data object Dark : ThemeModel(
        icon = YallaIcons.ThemeDark,
        name = Res.string.settings_theme_dark,
        themeKind = ThemeKind.Dark
    )

    data object System : ThemeModel(
        icon = YallaIcons.ThemeSystem,
        name = Res.string.settings_theme_system,
        themeKind = ThemeKind.System
    )

    @Composable
    fun toSelectableItemModel() =
        SelectableItemModel(
            item = this,
            title = stringResource(name),
            icon = rememberVectorPainter(icon),
            iconColor = uz.yalla.design.theme.System.color.iconBase
        )

    companion object {
        /** All available theme options. @since 0.0.1 */
        val all = listOf(Light, Dark, System)

        /**
         * Resolves a [ThemeModel] from the persisted [ThemeKind].
         *
         * @since 0.0.1
         */
        fun fromThemeKind(themeKind: ThemeKind): ThemeModel =
            when (themeKind) {
                ThemeKind.Light -> Light
                ThemeKind.Dark -> Dark
                ThemeKind.System -> System
            }
    }
}
