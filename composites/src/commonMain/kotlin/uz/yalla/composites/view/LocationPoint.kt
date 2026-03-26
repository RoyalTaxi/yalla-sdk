package uz.yalla.composites.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

@Immutable
data class LocationPointColors(
    val label: Color,
)

@Immutable
data class LocationPointDimens(
    val iconLabelSpacing: Dp,
    val labelMaxLines: Int,
)

object LocationPointDefaults {

    @Composable
    fun colors(
        label: Color = System.color.text.base,
    ): LocationPointColors = LocationPointColors(
        label = label,
    )

    @Composable
    fun destinationColors(
        label: Color = System.color.text.subtle,
    ): LocationPointColors = LocationPointColors(
        label = label,
    )

    fun dimens(
        iconLabelSpacing: Dp = 8.dp,
        labelMaxLines: Int = 1,
    ): LocationPointDimens = LocationPointDimens(
        iconLabelSpacing = iconLabelSpacing,
        labelMaxLines = labelMaxLines,
    )
}

@Composable
fun LocationPoint(
    icon: Painter,
    label: String,
    modifier: Modifier = Modifier,
    labelStyle: TextStyle = System.font.body.small.bold,
    colors: LocationPointColors = LocationPointDefaults.colors(),
    dimens: LocationPointDimens = LocationPointDefaults.dimens(),
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = icon,
            contentDescription = null,
        )

        Spacer(Modifier.width(dimens.iconLabelSpacing))

        Text(
            text = label,
            style = labelStyle,
            color = colors.label,
            overflow = TextOverflow.Ellipsis,
            maxLines = dimens.labelMaxLines,
        )
    }
}
