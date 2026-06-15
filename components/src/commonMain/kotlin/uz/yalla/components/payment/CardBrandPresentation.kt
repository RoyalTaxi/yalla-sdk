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

/**
 * How a [CardBrand] is shown to the user: its icon and its label.
 *
 * The SDK owns the brand itself ([CardBrand], in `uz.yalla.core`); this module owns how that brand
 * looks. Keeping the mapping here means every feature renders Humo/Uzcard identically and none of
 * them re-derives the brand from card-id lengths or hard-codes an icon. To add a brand, add it to
 * [CardBrand] and the `when`s below stop compiling until the new icon and label are supplied.
 */
@Immutable
public data class CardBrandPresentation(
    val painter: Painter,
    val label: String
)

/**
 * The icon for [brand], drawn from the design system's icon pack.
 */
@Composable
public fun cardBrandPainter(brand: CardBrand): Painter =
    rememberVectorPainter(
        when (brand) {
            CardBrand.Humo -> YallaIcons.Humo
            CardBrand.Uzcard -> YallaIcons.Uzcard
        }
    )

/**
 * The icon for the brand of [method], derived from its card id via [CardBrand.of].
 */
@Composable
public fun cardBrandPainter(method: PaymentMethod.Card): Painter = cardBrandPainter(CardBrand.of(method.cardId.raw))

/**
 * The display name for [brand].
 *
 * Brand names are proper nouns ("Humo", "Uzcard") and are not translated, so the label is a constant
 * per brand rather than a localized string.
 */
public fun cardBrandLabel(brand: CardBrand): String =
    when (brand) {
        CardBrand.Humo -> "Humo"
        CardBrand.Uzcard -> "Uzcard"
    }

/**
 * Both the icon and label for [brand], remembered together so a caller can render a card row without
 * touching the brand-to-resource mapping.
 */
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

/**
 * Both the icon and label for the brand of [method], derived from its card id via [CardBrand.of].
 */
@Composable
public fun rememberCardBrandPresentation(method: PaymentMethod.Card): CardBrandPresentation =
    rememberCardBrandPresentation(CardBrand.of(method.cardId.raw))
