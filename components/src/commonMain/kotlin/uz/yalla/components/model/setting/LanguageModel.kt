package uz.yalla.components.model.setting

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import uz.yalla.components.model.common.SelectableItemModel
import uz.yalla.core.kind.LocaleKind
import uz.yalla.resources.Res
import uz.yalla.resources.ic_flag_en
import uz.yalla.resources.ic_flag_ru
import uz.yalla.resources.ic_flag_uz
import uz.yalla.resources.language_english
import uz.yalla.resources.language_russian
import uz.yalla.resources.language_uzbek_cyrillic
import uz.yalla.resources.language_uzbek_latin

sealed class LanguageModel(
    val icon: DrawableResource,
    val name: StringResource,
    val localeKind: LocaleKind
) {
    data object Uzbek : LanguageModel(
        icon = Res.drawable.ic_flag_uz,
        name = Res.string.language_uzbek_latin,
        localeKind = LocaleKind.Uz
    )

    data object UzbekCyrillic : LanguageModel(
        icon = Res.drawable.ic_flag_uz,
        name = Res.string.language_uzbek_cyrillic,
        localeKind = LocaleKind.UzCyrillic
    )

    data object Russian : LanguageModel(
        icon = Res.drawable.ic_flag_ru,
        name = Res.string.language_russian,
        localeKind = LocaleKind.Ru
    )

    data object English : LanguageModel(
        icon = Res.drawable.ic_flag_en,
        name = Res.string.language_english,
        localeKind = LocaleKind.En
    )

    @Composable
    fun toSelectableItemModel() =
        SelectableItemModel(
            item = this,
            title = stringResource(name),
            icon = painterResource(icon),
            iconColor = Color.Unspecified
        )

    companion object {
        val LANGUAGES = listOf(Uzbek, Russian)

        fun fromLocaleKind(localeKind: LocaleKind): LanguageModel =
            when (localeKind) {
                LocaleKind.Uz -> Uzbek
                LocaleKind.UzCyrillic -> UzbekCyrillic
                LocaleKind.Ru -> Russian
                LocaleKind.En -> English
            }
    }
}
