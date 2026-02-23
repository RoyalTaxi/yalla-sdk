package uz.yalla.components.util

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import uz.yalla.core.kind.PaymentKind
import uz.yalla.resources.Res
import uz.yalla.resources.ic_cash
import uz.yalla.resources.ic_humo
import uz.yalla.resources.ic_uzcard
import uz.yalla.resources.payment_card_humo_format
import uz.yalla.resources.payment_card_uzcard_format
import uz.yalla.resources.payment_type_cash

fun PaymentKind.getDrawableResource(): DrawableResource =
    when (this) {
        PaymentKind.Cash -> Res.drawable.ic_cash

        is PaymentKind.Card -> {
            if (cardId.length == 16) {
                Res.drawable.ic_humo
            } else {
                Res.drawable.ic_uzcard
            }
        }
    }

fun PaymentKind.getStringResource(): StringResource =
    when (this) {
        PaymentKind.Cash -> Res.string.payment_type_cash

        is PaymentKind.Card -> {
            if (cardId.length == 16) {
                Res.string.payment_card_humo_format
            } else {
                Res.string.payment_card_uzcard_format
            }
        }
    }
