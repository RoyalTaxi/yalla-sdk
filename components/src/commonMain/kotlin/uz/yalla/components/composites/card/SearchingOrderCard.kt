package uz.yalla.components.composites.card

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.Res
import uz.yalla.resources.img_active_order_car
import uz.yalla.resources.img_spinner
import uz.yalla.resources.order_status_by_instruction
import uz.yalla.resources.order_status_searching

@Immutable
public data class SearchingOrderCardColors(
    val containerColor: Color,
    val contentColor: Color,
    val dotColor: Color,
    val pickupColor: Color,
    val dropoffColor: Color,
    val dividerColor: Color,
    val badgeColor: Color,
    val badgeBorder: Brush,
    val spinnerColor: Color
)

@Immutable
public data class SearchingOrderCardDimens(
    val shape: Shape,
    val contentPadding: PaddingValues,
    val carSize: Dp,
    val carShape: Shape,
    val contentSpacing: Dp
)

@Immutable
public data class SearchingOrderCardStyles(
    val routeStyle: TextStyle,
    val badgeStyle: TextStyle
)

public object SearchingOrderCardDefaults {
    @Composable
    public fun colors(
        containerColor: Color = System.color.background.secondary,
        contentColor: Color = System.color.text.base,
        dotColor: Color = System.color.background.base,
        pickupColor: Color = System.color.background.brand,
        dropoffColor: Color = System.color.icon.red,
        dividerColor: Color = System.color.border.disabled,
        badgeColor: Color = System.color.background.base,
        badgeBorder: Brush = System.color.gradient.sunsetNight,
        spinnerColor: Color = System.color.icon.base
    ): SearchingOrderCardColors =
        SearchingOrderCardColors(
            containerColor = containerColor,
            contentColor = contentColor,
            dotColor = dotColor,
            pickupColor = pickupColor,
            dropoffColor = dropoffColor,
            dividerColor = dividerColor,
            badgeColor = badgeColor,
            badgeBorder = badgeBorder,
            spinnerColor = spinnerColor
        )

    @Composable
    public fun dimens(
        shape: Shape = RoundedCornerShape(16.dp),
        contentPadding: PaddingValues = PaddingValues(16.dp),
        carSize: Dp = 48.dp,
        carShape: Shape = RoundedCornerShape(12.dp),
        contentSpacing: Dp = 16.dp
    ): SearchingOrderCardDimens =
        SearchingOrderCardDimens(
            shape = shape,
            contentPadding = contentPadding,
            carSize = carSize,
            carShape = carShape,
            contentSpacing = contentSpacing
        )

    @Composable
    public fun styles(
        routeStyle: TextStyle =
            System.font.body.small.bold.copy(
                fontSize = 12.sp,
                lineHeight = 10.sp
            ),
        badgeStyle: TextStyle = System.font.body.caption
    ): SearchingOrderCardStyles =
        SearchingOrderCardStyles(
            routeStyle = routeStyle,
            badgeStyle = badgeStyle
        )
}

@Composable
public fun SearchingOrderCard(
    stops: List<String>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: SearchingOrderCardColors = SearchingOrderCardDefaults.colors(),
    dimens: SearchingOrderCardDimens = SearchingOrderCardDefaults.dimens(),
    styles: SearchingOrderCardStyles = SearchingOrderCardDefaults.styles()
) {
    Card(
        onClick = onClick,
        shape = dimens.shape,
        colors = CardDefaults.cardColors(colors.containerColor),
        modifier = modifier.semantics(mergeDescendants = true) {}
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .padding(dimens.contentPadding)
                    .height(IntrinsicSize.Min)
        ) {
            Image(
                painter = painterResource(Res.drawable.img_active_order_car),
                contentDescription = null,
                modifier =
                    Modifier
                        .size(dimens.carSize)
                        .clip(dimens.carShape)
            )

            Spacer(modifier = Modifier.width(dimens.contentSpacing))

            Stops(
                stops = stops,
                colors = colors,
                style = styles.routeStyle,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(dimens.contentSpacing))

            Searching(
                colors = colors,
                style = styles.badgeStyle
            )
        }
    }
}

@Composable
private fun Stops(
    stops: List<String>,
    colors: SearchingOrderCardColors,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(IntrinsicSize.Min),
        verticalArrangement = Arrangement.Center
    ) {
        StopRow(
            text = stops.firstOrNull().orEmpty(),
            borderColor = colors.pickupColor,
            colors = colors,
            style = style
        )

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(
            thickness = 1.dp,
            color = colors.dividerColor,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        StopRow(
            text =
                stops.drop(1).lastOrNull()
                    ?: stringResource(Res.string.order_status_by_instruction),
            borderColor = colors.dropoffColor,
            colors = colors,
            style = style
        )
    }
}

@Composable
private fun StopRow(
    text: String,
    borderColor: Color,
    colors: SearchingOrderCardColors,
    style: TextStyle
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier =
                Modifier
                    .size(14.dp)
                    .background(colors.dotColor, CircleShape)
                    .border(4.dp, borderColor, CircleShape)
        )

        Text(
            text = text,
            color = colors.contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = style
        )
    }
}

@Composable
private fun Searching(
    colors: SearchingOrderCardColors,
    style: TextStyle
) {
    val infinite = rememberInfiniteTransition()

    val rotation =
        infinite.animateFloat(
            initialValue = 360f,
            targetValue = 0f,
            animationSpec =
                infiniteRepeatable(
                    repeatMode = RepeatMode.Restart,
                    animation =
                        tween(
                            durationMillis = 800,
                            easing = LinearEasing
                        )
                )
        )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = colors.badgeColor,
        border = BorderStroke(1.dp, colors.badgeBorder)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(Res.string.order_status_searching),
                color = colors.contentColor,
                style = style
            )

            Icon(
                painter = painterResource(Res.drawable.img_spinner),
                contentDescription = null,
                tint = colors.spinnerColor,
                modifier =
                    Modifier
                        .size(10.dp)
                        .graphicsLayer { rotationZ = rotation.value }
            )
        }
    }
}

@Preview
@Composable
private fun SearchingOrderCardPreview() {
    YallaTheme {
        SearchingOrderCard(
            stops = listOf("Pickup point", "Dropoff point"),
            onClick = {}
        )
    }
}
