package uz.yalla.foundation.settings

import androidx.compose.runtime.Immutable
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
 */
@Immutable
sealed class LanguageOption(
    override val icon: ImageVector,
    override val name: StringResource,
    val kind: LocaleKind
) : Selectable {
    data object Uzbek : LanguageOption(
        icon = YallaIcons.FlagUz,
        name = Res.string.language_uzbek_latin,
        kind = LocaleKind.Uz
    )

    data object Russian : LanguageOption(
        icon = YallaIcons.FlagRu,
        name = Res.string.language_russian,
        kind = LocaleKind.Ru
    )

    companion object {
        /** All production-ready language options. */
        val all = listOf(Uzbek, Russian)

        /** Exhaustive over current [LocaleKind] cases. */
        fun from(kind: LocaleKind): LanguageOption =
            when (kind) {
                LocaleKind.Uz -> Uzbek
                LocaleKind.Ru -> Russian
            }
    }
}
