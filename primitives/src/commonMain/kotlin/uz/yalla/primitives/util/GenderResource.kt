package uz.yalla.primitives.util

import org.jetbrains.compose.resources.StringResource
import uz.yalla.core.profile.GenderKind
import uz.yalla.resources.Res
import uz.yalla.resources.register_gender_female
import uz.yalla.resources.register_gender_male

/**
 * Maps a [GenderKind] to its localized string resource.
 *
 * Returns `null` for [GenderKind.NotSelected].
 *
 * @since 0.0.1
 */
val GenderKind.resource: StringResource?
    get() =
        when (this) {
            GenderKind.Male -> Res.string.register_gender_male
            GenderKind.Female -> Res.string.register_gender_female
            GenderKind.NotSelected -> null
        }
