package uz.yalla.components.composites.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.Res
import uz.yalla.resources.icons.ArrowRightInCircle
import uz.yalla.resources.icons.YallaIcons
import uz.yalla.resources.order_list_title

@Immutable
public data class OrdersCardColors(
    val containerColor: Color,
    val badgeColor: Color,
    val countColor: Color,
    val labelColor: Color
)

@Immutable
public data class OrdersCardDimens(
    val shape: Shape,
    val badgeShape: Shape,
    val contentPadding: PaddingValues,
    val spacing: Dp,
    val badgeSize: Dp,
    val iconSize: Dp
)

@Immutable
public data class OrdersCardStyles(
    val countStyle: TextStyle,
    val labelStyle: TextStyle
)

public object OrdersCardDefaults {
    @Composable
    public fun colors(
        containerColor: Color = System.color.background.base,
        badgeColor: Color = System.color.background.secondary,
        countColor: Color = System.color.text.base,
        labelColor: Color = System.color.text.base
    ): OrdersCardColors =
        OrdersCardColors(
            containerColor = containerColor,
            badgeColor = badgeColor,
            countColor = countColor,
            labelColor = labelColor
        )

    @Composable
    public fun dimens(
        shape: Shape = CircleShape,
        badgeShape: Shape = CircleShape,
        contentPadding: PaddingValues = PaddingValues(2.dp),
        spacing: Dp = 12.dp,
        badgeSize: Dp = 42.dp,
        iconSize: Dp = 24.dp
    ): OrdersCardDimens =
        OrdersCardDimens(
            shape = shape,
            badgeShape = badgeShape,
            contentPadding = contentPadding,
            spacing = spacing,
            badgeSize = badgeSize,
            iconSize = iconSize
        )

    @Composable
    public fun styles(
        countStyle: TextStyle = System.font.title.base,
        labelStyle: TextStyle =
            System.font.body.base.medium.copy(
                fontSize = 12.sp,
                lineHeight = 14.sp
            )
    ): OrdersCardStyles =
        OrdersCardStyles(
            countStyle = countStyle,
            labelStyle = labelStyle
        )
}

@Composable
public fun OrdersCard(
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: OrdersCardColors = OrdersCardDefaults.colors(),
    dimens: OrdersCardDimens = OrdersCardDefaults.dimens(),
    styles: OrdersCardStyles = OrdersCardDefaults.styles()
) {
    if (count == 0) return

    val ordersLabel = "$count ${stringResource(Res.string.order_list_title)}"

    Card(
        modifier = modifier.semantics(mergeDescendants = true) { contentDescription = ordersLabel },
        onClick = onClick,
        shape = dimens.shape,
        colors = CardDefaults.cardColors(colors.containerColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.spacing),
            modifier = Modifier.padding(dimens.contentPadding).height(IntrinsicSize.Min)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(dimens.badgeSize).background(colors.badgeColor, dimens.badgeShape)
            ) {
                Text(
                    text = count.toString(),
                    color = colors.countColor,
                    style = styles.countStyle
                )
            }

            Text(
                text = stringResource(Res.string.order_list_title).replace(' ', '\n'),
                color = colors.labelColor,
                style = styles.labelStyle
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(dimens.badgeSize).background(colors.badgeColor, dimens.badgeShape)
            ) {
                Icon(
                    painter = rememberVectorPainter(YallaIcons.ArrowRightInCircle),
                    contentDescription = null,
                    modifier = Modifier.size(dimens.iconSize).rotate(90f)
                )
            }
        }
    }
}

@Preview
@Composable
private fun OrdersCardPreview() {
    YallaTheme {
        OrdersCard(
            count = 3,
            onClick = {}
        )
    }
}
