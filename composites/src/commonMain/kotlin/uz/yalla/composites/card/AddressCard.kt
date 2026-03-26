package uz.yalla.composites.card

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

@Immutable
data class AddressCardColors(
    val container: Color,
    val title: Color,
    val subtitle: Color,
    val footer: Color,
)

@Immutable
data class AddressCardDimens(
    val shape: Shape,
    val maxWidth: Dp,
    val height: Dp,
    val contentPadding: PaddingValues,
    val titleIconSpacing: Dp,
    val contentSpacing: Dp,
)

object AddressCardDefaults {

    @Composable
    fun colors(
        container: Color = System.color.background.secondary,
        title: Color = System.color.text.base,
        subtitle: Color = System.color.text.base,
        footer: Color = System.color.text.subtle,
    ): AddressCardColors = AddressCardColors(
        container = container,
        title = title,
        subtitle = subtitle,
        footer = footer,
    )

    fun dimens(
        shape: Shape = RoundedCornerShape(20.dp),
        maxWidth: Dp = 248.dp,
        height: Dp = 120.dp,
        contentPadding: PaddingValues = PaddingValues(16.dp),
        titleIconSpacing: Dp = 8.dp,
        contentSpacing: Dp = 8.dp,
    ): AddressCardDimens = AddressCardDimens(
        shape = shape,
        maxWidth = maxWidth,
        height = height,
        contentPadding = contentPadding,
        titleIconSpacing = titleIconSpacing,
        contentSpacing = contentSpacing,
    )
}

@Composable
fun AddressCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: AddressCardColors = AddressCardDefaults.colors(),
    dimens: AddressCardDimens = AddressCardDefaults.dimens(),
    leadingIcon: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    footer: @Composable (() -> Unit)? = null,
    title: @Composable () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = dimens.shape,
        colors = CardDefaults.cardColors(containerColor = colors.container),
        modifier = modifier
            .widthIn(max = dimens.maxWidth)
            .height(dimens.height),
    ) {
        Column(modifier = Modifier.padding(dimens.contentPadding)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                leadingIcon?.let {
                    it()
                    Spacer(modifier = Modifier.width(dimens.titleIconSpacing))
                }
                title()
            }

            subtitle?.let {
                Spacer(modifier = Modifier.height(dimens.contentSpacing))
                it()
            }

            Spacer(modifier = Modifier.weight(1f))

            footer?.invoke()
        }
    }
}
