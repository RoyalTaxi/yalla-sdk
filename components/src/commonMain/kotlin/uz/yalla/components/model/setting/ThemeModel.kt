package uz.yalla.components.model.setting

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import uz.yalla.components.model.common.SelectableItemModel
import uz.yalla.core.kind.ThemeKind
import uz.yalla.design.theme.System
import uz.yalla.resources.Res
import uz.yalla.resources.ic_moon
import uz.yalla.resources.ic_setting
import uz.yalla.resources.ic_sun
import uz.yalla.resources.settings_theme_dark
import uz.yalla.resources.settings_theme_light
import uz.yalla.resources.settings_theme_system

sealed class ThemeModel(
    val icon: DrawableResource,
    val name: StringResource,
    val themeKind: ThemeKind
) {
    data object LIGHT : ThemeModel(
        icon = Res.drawable.ic_sun,
        name = Res.string.settings_theme_light,
        themeKind = ThemeKind.Light
    )

    data object DARK : ThemeModel(
        icon = Res.drawable.ic_moon,
        name = Res.string.settings_theme_dark,
        themeKind = ThemeKind.Dark
    )

    data object SYSTEM : ThemeModel(
        icon = Res.drawable.ic_setting,
        name = Res.string.settings_theme_system,
        themeKind = ThemeKind.System
    )

    @Composable
    fun toSelectableItemModel() =
        SelectableItemModel(
            item = this,
            title = stringResource(name),
            icon = painterResource(icon),
            iconColor = System.color.iconBase
        )

    companion object {
        val THEMES = listOf(LIGHT, DARK, SYSTEM)

        fun fromThemeKind(themeKind: ThemeKind): ThemeModel =
            when (themeKind) {
                ThemeKind.Light -> LIGHT
                ThemeKind.Dark -> DARK
                ThemeKind.System -> SYSTEM
            }
    }
}
