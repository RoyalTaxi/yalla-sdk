package uz.yalla.foundation.settings

import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource
import uz.yalla.core.settings.LocaleKind
import uz.yalla.resources.Res
import uz.yalla.resources.icons.FlagRu
import uz.yalla.resources.icons.FlagUz
import uz.yalla.resources.icons.YallaIcons
import uz.yalla.resources.language_russian
import uz.yalla.resources.language_uzbek_latin

/**
 * Language option for language picker screens.
 *
 * Sealed hierarchy mapping [LocaleKind] to picker display properties. Narrowed
 * in Phase 3 (ADR-014) to the production-ready locales: [Uzbek] and [Russian].
 *
 * @property kind Corresponding [LocaleKind] for persistence.
 * @since 0.0.1
 */
sealed class LanguageOption(
    override val icon: ImageVector,
    override val name: StringResource,
    val kind: LocaleKind,
) : Selectable {

    /** Uzbek (Latin script). @since 0.0.1 */
    data object Uzbek : LanguageOption(
        icon = YallaIcons.FlagUz,
        name = Res.string.language_uzbek_latin,
        kind = LocaleKind.Uz,
    )

    /** Russian. @since 0.0.1 */
    data object Russian : LanguageOption(
        icon = YallaIcons.FlagRu,
        name = Res.string.language_russian,
        kind = LocaleKind.Ru,
    )

    companion object {
        /** All production-ready language options. */
        val all = listOf(Uzbek, Russian)

        /**
         * Resolves a [LanguageOption] from a persisted [LocaleKind].
         *
         * @param kind The persisted locale kind.
         * @return The corresponding [LanguageOption] — exhaustive over current `LocaleKind` cases.
         * @since 0.0.1
         */
        fun from(kind: LocaleKind): LanguageOption = when (kind) {
            LocaleKind.Uz -> Uzbek
            LocaleKind.Ru -> Russian
        }
    }
}
