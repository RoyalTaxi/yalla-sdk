package uz.yalla.foundation.settings

import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource
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

/**
 * Language option for language picker screens.
 *
 * Sealed hierarchy mapping [LocaleKind] to display properties.
 * Only [Uzbek] and [Russian] are in [all] — [UzbekCyrillic] and [English] are defined
 * but not production-ready.
 *
 * @property kind Corresponding [LocaleKind] for persistence
 * @since 0.0.1
 */
sealed class LanguageOption(
    override val icon: ImageVector,
    override val name: StringResource,
    val kind: LocaleKind
) : Selectable {

    /** Uzbek (Latin script) language option. @since 0.0.1 */
    data object Uzbek : LanguageOption(
        icon = YallaIcons.FlagUz,
        name = Res.string.language_uzbek_latin,
        kind = LocaleKind.Uz
    )

    /** Uzbek (Cyrillic script) language option. Not yet production-ready. @since 0.0.1 */
    data object UzbekCyrillic : LanguageOption(
        icon = YallaIcons.FlagUz,
        name = Res.string.language_uzbek_cyrillic,
        kind = LocaleKind.UzCyrillic
    )

    /** Russian language option. @since 0.0.1 */
    data object Russian : LanguageOption(
        icon = YallaIcons.FlagRu,
        name = Res.string.language_russian,
        kind = LocaleKind.Ru
    )

    /** English language option. Not yet production-ready. @since 0.0.1 */
    data object English : LanguageOption(
        icon = YallaIcons.FlagUs,
        name = Res.string.language_english,
        kind = LocaleKind.En
    )

    companion object {
        /** All production-ready language options. @since 0.0.1 */
        val all = listOf(Uzbek, Russian)

        /**
         * Resolves a [LanguageOption] from the persisted [LocaleKind].
         *
         * @param kind The persisted locale kind to resolve.
         * @return Corresponding [LanguageOption] instance.
         * @since 0.0.1
         */
        fun from(kind: LocaleKind): LanguageOption =
            when (kind) {
                LocaleKind.Uz -> Uzbek
                LocaleKind.UzCyrillic -> UzbekCyrillic
                LocaleKind.Ru -> Russian
                LocaleKind.En -> English
            }
    }
}
