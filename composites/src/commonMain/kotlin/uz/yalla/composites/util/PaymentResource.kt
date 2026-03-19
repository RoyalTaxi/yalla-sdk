package uz.yalla.composites.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import uz.yalla.core.payment.PaymentKind
import uz.yalla.resources.Res
import uz.yalla.resources.icons.Humo
import uz.yalla.resources.icons.Uzcard
import uz.yalla.resources.icons.YallaIcons
import uz.yalla.resources.img_cash
import uz.yalla.resources.payment_card_humo_format
import uz.yalla.resources.payment_card_uzcard_format
import uz.yalla.resources.payment_type_cash

/**
 * Resolves a [Painter] for this [PaymentKind].
 *
 * Returns a cash image for [PaymentKind.Cash] and a card-brand vector
 * (Humo or Uzcard) for [PaymentKind.Card] based on `cardId` length.
 *
 * @since 0.0.1
 */
@Composable
fun PaymentKind.toPainter(): Painter =
    when (this) {
        PaymentKind.Cash -> painterResource(Res.drawable.img_cash)

        is PaymentKind.Card -> {
            if (cardId.length == 16) {
                rememberVectorPainter(YallaIcons.Humo)
            } else {
                rememberVectorPainter(YallaIcons.Uzcard)
            }
        }
    }

/**
 * Resolves a [StringResource] label for this [PaymentKind].
 *
 * Returns a localized format string appropriate for the payment type.
 *
 * @since 0.0.1
 */
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
