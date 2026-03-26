package uz.yalla.composites.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import uz.yalla.design.theme.System
import uz.yalla.resources.Res
import uz.yalla.resources.img_coin

@Immutable
data class ValueItemColors(
    val background: Brush,
    val text: Color,
)

@Immutable
data class ValueItemDimens(
    val shape: Shape,
    val contentPadding: PaddingValues,
    val iconSize: Dp,
    val iconSpacing: Dp,
    val trailingSpacing: Dp,
)

object ValueItemDefaults {

    @Composable
    fun colors(
        background: Brush = System.color.gradient.sunsetNight,
        text: Color = System.color.text.white,
    ) = ValueItemColors(
        background = background,
        text = text,
    )

    fun dimens(
        shape: Shape = CircleShape,
        contentPadding: PaddingValues = PaddingValues(4.dp),
        iconSize: Dp = 20.dp,
        iconSpacing: Dp = 4.dp,
        trailingSpacing: Dp = 12.dp,
    ) = ValueItemDimens(
        shape = shape,
        contentPadding = contentPadding,
        iconSize = iconSize,
        iconSpacing = iconSpacing,
        trailingSpacing = trailingSpacing,
    )
}

@Composable
fun ClickableValueItem(
    bonus: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ValueItemColors = ValueItemDefaults.colors(),
    dimens: ValueItemDimens = ValueItemDefaults.dimens(),
) = Surface(
    shape = dimens.shape,
    color = Color.Transparent,
    modifier = modifier,
    onClick = onClick,
) {
    ValueItemContent(
        bonus = bonus,
        colors = colors,
        dimens = dimens,
    )
}

@Composable
fun ValueItemView(
    bonus: Long,
    modifier: Modifier = Modifier,
    colors: ValueItemColors = ValueItemDefaults.colors(),
    dimens: ValueItemDimens = ValueItemDefaults.dimens(),
) = Card(
    shape = dimens.shape,
    modifier = modifier,
) {
    ValueItemContent(
        bonus = bonus,
        colors = colors,
        dimens = dimens,
    )
}

@Composable
private fun ValueItemContent(
    bonus: Long,
    colors: ValueItemColors,
    dimens: ValueItemDimens,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.iconSpacing),
        modifier =
            Modifier
                .background(colors.background)
                .padding(dimens.contentPadding),
    ) {
        Image(
            painter = painterResource(Res.drawable.img_coin),
            contentDescription = null,
            modifier = Modifier.size(dimens.iconSize),
        )

        Text(
            text = bonus.toString(),
            style = System.font.body.base.bold,
            color = colors.text,
        )

        Spacer(modifier = Modifier.size(dimens.trailingSpacing))
    }
}
