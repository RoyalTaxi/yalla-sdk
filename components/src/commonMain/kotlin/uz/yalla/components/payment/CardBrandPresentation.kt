package uz.yalla.components.payment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import uz.yalla.core.payment.CardBrand
import uz.yalla.core.payment.PaymentMethod
import uz.yalla.resources.icons.Humo
import uz.yalla.resources.icons.Uzcard
import uz.yalla.resources.icons.YallaIcons

@Immutable
public data class CardBrandPresentation(
    val painter: Painter,
    val label: String
)

@Composable
public fun cardBrandPainter(brand: CardBrand): Painter =
    rememberVectorPainter(
        when (brand) {
            CardBrand.Humo -> YallaIcons.Humo
            CardBrand.Uzcard -> YallaIcons.Uzcard
        }
    )

@Composable
public fun cardBrandPainter(method: PaymentMethod.Card): Painter = cardBrandPainter(CardBrand.of(method.cardId.raw))

public fun cardBrandLabel(brand: CardBrand): String =
    when (brand) {
        CardBrand.Humo -> "Humo"
        CardBrand.Uzcard -> "Uzcard"
    }

@Composable
public fun rememberCardBrandPresentation(brand: CardBrand): CardBrandPresentation {
    val painter = cardBrandPainter(brand)
    return remember(painter, brand) {
        CardBrandPresentation(
            painter = painter,
            label = cardBrandLabel(brand)
        )
    }
}

@Composable
public fun rememberCardBrandPresentation(method: PaymentMethod.Card): CardBrandPresentation =
    rememberCardBrandPresentation(CardBrand.of(method.cardId.raw))
