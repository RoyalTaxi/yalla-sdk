package uz.yalla.foundation.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import uz.yalla.core.settings.LocaleKind
import uz.yalla.resources.Res
import uz.yalla.resources.icons.FlagRu
import uz.yalla.resources.icons.FlagUs
import uz.yalla.resources.icons.FlagUz
import uz.yalla.resources.icons.YallaIcons
import uz.yalla.resources.language_english
import uz.yalla.resources.language_russian
import uz.yalla.resources.language_uzbek_cyrillic
import uz.yalla.resources.language_uzbek_latin

sealed class LanguageModel(
    val icon: ImageVector,
    val name: StringResource,
    val localeKind: LocaleKind
) {
    data object Uzbek : LanguageModel(
        icon = YallaIcons.FlagUz,
        name = Res.string.language_uzbek_latin,
        localeKind = LocaleKind.Uz
    )

    data object UzbekCyrillic : LanguageModel(
        icon = YallaIcons.FlagUz,
        name = Res.string.language_uzbek_cyrillic,
        localeKind = LocaleKind.UzCyrillic
    )

    data object Russian : LanguageModel(
        icon = YallaIcons.FlagRu,
        name = Res.string.language_russian,
        localeKind = LocaleKind.Ru
    )

    data object English : LanguageModel(
        icon = YallaIcons.FlagUs,
        name = Res.string.language_english,
        localeKind = LocaleKind.En
    )

    @Composable
    fun toSelectableItemModel() =
        SelectableItemModel(
            item = this,
            title = stringResource(name),
            icon = rememberVectorPainter(icon),
            iconColor = Color.Unspecified
        )

    companion object {
        val all = listOf(Uzbek, Russian)

        fun fromLocaleKind(localeKind: LocaleKind): LanguageModel =
            when (localeKind) {
                LocaleKind.Uz -> Uzbek
                LocaleKind.UzCyrillic -> UzbekCyrillic
                LocaleKind.Ru -> Russian
                LocaleKind.En -> English
            }
    }
}
