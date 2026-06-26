package uz.yalla.components.composites.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

@Immutable
public data class ServiceCardColors(
    val containerBrush: Brush,
    val titleColor: Color,
)

@Immutable
public data class ServiceCardDimens(
    val shape: Shape,
    val width: Dp,
    val height: Dp,
    val titlePadding: PaddingValues,
    val disabledAlpha: Float,
)

@Immutable
public data class ServiceCardStyles(
    val titleStyle: TextStyle,
)

public object ServiceCardDefaults {
    @Composable
    public fun colors(
        containerBrush: Brush =
            if (System.isDark) {
                Brush.linearGradient(
                    listOf(System.color.background.secondary, System.color.background.secondary),
                )
            } else {
                Brush.linearGradient(
                    listOf(
                        System.color.background.base,
                        System.color.background.brandLite.copy(alpha = 0.4f),
                    ),
                )
            },
        titleColor: Color = if (System.isDark) System.color.text.white else System.color.text.link,
    ): ServiceCardColors =
        ServiceCardColors(
            containerBrush = containerBrush,
            titleColor = titleColor,
        )

    @Composable
    public fun dimens(
        shape: Shape = RoundedCornerShape(20.dp),
        width: Dp = 114.dp,
        height: Dp = 120.dp,
        titlePadding: PaddingValues = PaddingValues(top = 16.dp, start = 14.dp),
        disabledAlpha: Float = 0.5f,
    ): ServiceCardDimens =
        ServiceCardDimens(
            shape = shape,
            width = width,
            height = height,
            titlePadding = titlePadding,
            disabledAlpha = disabledAlpha,
        )

    @Composable
    public fun styles(
        titleStyle: TextStyle = System.font.body.small.bold,
    ): ServiceCardStyles =
        ServiceCardStyles(
            titleStyle = titleStyle,
        )
}

@Composable
public fun ServiceCard(
    title: String,
    painter: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ServiceCardColors = ServiceCardDefaults.colors(),
    dimens: ServiceCardDimens = ServiceCardDefaults.dimens(),
    styles: ServiceCardStyles = ServiceCardDefaults.styles(),
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier =
            modifier
                .size(width = dimens.width, height = dimens.height)
                .alpha(if (enabled) 1f else dimens.disabledAlpha),
        shape = dimens.shape,
        color = Color.Transparent,
    ) {
        Box(modifier = Modifier.fillMaxSize().background(brush = colors.containerBrush)) {
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
            )
            Text(
                text = title,
                color = colors.titleColor,
                style = styles.titleStyle,
                modifier =
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(dimens.titlePadding),
            )
        }
    }
}
