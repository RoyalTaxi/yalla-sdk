package uz.yalla.components.util

import org.jetbrains.compose.resources.StringResource
import uz.yalla.core.kind.GenderKind
import uz.yalla.resources.Res
import uz.yalla.resources.register_gender_female
import uz.yalla.resources.register_gender_male

val GenderKind.resource: StringResource?
    get() =
        when (this) {
            GenderKind.Male -> Res.string.register_gender_male
            GenderKind.Female -> Res.string.register_gender_female
            GenderKind.NotSelected -> null
        }
