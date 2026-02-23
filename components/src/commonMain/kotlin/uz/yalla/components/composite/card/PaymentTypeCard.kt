package uz.yalla.components.composite.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import uz.yalla.components.util.formatArgs
import uz.yalla.components.util.getDrawableResource
import uz.yalla.components.util.getStringResource
import uz.yalla.core.kind.PaymentKind
import uz.yalla.design.theme.System
import uz.yalla.resources.Res
import uz.yalla.resources.ic_checkbox
import uz.yalla.resources.ic_checkbox_border

/**
 * Default configuration values for [PaymentTypeCard].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object PaymentTypeCardDefaults {
    /**
     * Color configuration for [PaymentTypeCard].
     *
     * @param container Card background color.
     * @param iconBackground Background color of the icon container.
     * @param iconTint Tint color for the payment icon.
     * @param text Text color.
     */
    data class PaymentTypeCardColors(
        val container: Color,
        val iconBackground: Color,
        val iconTint: Color,
        val text: Color
    )

    @Composable
    fun colors(
        container: Color = Color.Transparent,
        iconBackground: Color = System.color.backgroundSecondary,
        iconTint: Color = System.color.iconBase,
        text: Color = System.color.textBase
    ) = PaymentTypeCardColors(
        container = container,
        iconBackground = iconBackground,
        iconTint = iconTint,
        text = text
    )

    /**
     * Text style configuration for [PaymentTypeCard].
     *
     * @param label Style for the payment label text.
     */
    data class PaymentTypeCardStyle(
        val label: TextStyle
    )

    @Composable
    fun style(label: TextStyle = System.font.body.base.bold) =
        PaymentTypeCardStyle(
            label = label
        )

    /**
     * Dimension configuration for [PaymentTypeCard].
     *
     * @param contentPadding Padding inside the card.
     * @param iconSize Size of the icon container.
     * @param iconBackgroundShape Shape of the icon background.
     * @param iconPadding Padding inside the icon container.
     * @param iconSpacing Spacing between icon and text.
     * @param trailingSpacing Spacing before checkbox.
     */
    data class PaymentTypeCardDimens(
        val contentPadding: PaddingValues,
        val iconSize: Dp,
        val iconBackgroundShape: Shape,
        val iconPadding: Dp,
        val iconSpacing: Dp,
        val trailingSpacing: Dp
    )

    @Composable
    fun dimens(
        contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        iconSize: Dp = 44.dp,
        iconBackgroundShape: Shape = RoundedCornerShape(10.dp),
        iconPadding: Dp = 10.dp,
        iconSpacing: Dp = 16.dp,
        trailingSpacing: Dp = 28.dp
    ) = PaymentTypeCardDimens(
        contentPadding = contentPadding,
        iconSize = iconSize,
        iconBackgroundShape = iconBackgroundShape,
        iconPadding = iconPadding,
        iconSpacing = iconSpacing,
        trailingSpacing = trailingSpacing
    )
}

/**
 * State for [PaymentTypeCard] component.
 *
 * @property paymentType The payment kind to display.
 * @property isSelected Whether this payment type is selected.
 */
data class PaymentTypeCardState(
    val paymentType: PaymentKind,
    val isSelected: Boolean,
)

/**
 * Card for selecting a payment type with checkbox indicator.
 *
 * ## Usage
 *
 * ```kotlin
 * PaymentTypeCard(
 *     state = PaymentTypeCardState(
 *         paymentType = PaymentKind.Cash,
 *         isSelected = state.selectedPayment == PaymentKind.Cash,
 *     ),
 *     onClick = { viewModel.selectPayment(PaymentKind.Cash) },
 * )
 * ```
 *
 * @param state Card state containing payment type and selection state.
 * @param onClick Called when the card is clicked.
 * @param modifier Applied to the card.
 * @param colors Color configuration, defaults to [PaymentTypeCardDefaults.colors].
 * @param style Text style configuration, defaults to [PaymentTypeCardDefaults.style].
 * @param dimens Dimension configuration, defaults to [PaymentTypeCardDefaults.dimens].
 *
 * @see PaymentTypeCardState for state configuration
 * @see PaymentTypeCardDefaults for default values
 */
@Composable
fun PaymentTypeCard(
    state: PaymentTypeCardState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: PaymentTypeCardDefaults.PaymentTypeCardColors = PaymentTypeCardDefaults.colors(),
    style: PaymentTypeCardDefaults.PaymentTypeCardStyle = PaymentTypeCardDefaults.style(),
    dimens: PaymentTypeCardDefaults.PaymentTypeCardDimens = PaymentTypeCardDefaults.dimens()
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(colors.container)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(dimens.contentPadding)
        ) {
            Icon(
                painter = painterResource(state.paymentType.getDrawableResource()),
                contentDescription = null,
                tint =
                    when (state.paymentType) {
                        PaymentKind.Cash -> Color.Unspecified
                        is PaymentKind.Card -> colors.iconTint
                    },
                modifier =
                    Modifier
                        .size(dimens.iconSize)
                        .clip(dimens.iconBackgroundShape)
                        .background(colors.iconBackground)
                        .padding(dimens.iconPadding)
            )

            Spacer(modifier = Modifier.width(dimens.iconSpacing))

            Text(
                text =
                    stringResource(state.paymentType.getStringResource())
                        .formatArgs((state.paymentType as? PaymentKind.Card)?.maskedNumber?.takeLast(4) ?: ""),
                style = style.label,
                color = colors.text,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(dimens.trailingSpacing))

            Icon(
                painter =
                    painterResource(
                        if (state.isSelected) Res.drawable.ic_checkbox else Res.drawable.ic_checkbox_border
                    ),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
    }
}
