package uz.yalla.composites.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * UI state for [PlaceCard].
 *
 * @param name Place display name.
 * @param address Optional address text.
 * @param isEmpty Whether place is not yet configured.
 */
data class PlaceCardState(
    val name: String,
    val address: String? = null,
    val isEmpty: Boolean = address == null,
)

/**
 * Default configuration values for [PlaceCard].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object PlaceCardDefaults {
    /**
     * Color configuration for [PlaceCard].
     *
     * @param container Card background color.
     * @param name Name text color.
     * @param address Address text color.
     * @param hint Hint text color for empty state.
     * @param icon Icon tint when configured.
     * @param iconBackground Icon background when configured.
     * @param iconEmpty Icon tint when empty.
     * @param iconBackgroundEmpty Icon background when empty.
     */
    data class PlaceCardColors(
        val container: Color,
        val name: Color,
        val address: Color,
        val hint: Color,
        val icon: Color,
        val iconBackground: Color,
        val iconEmpty: Color,
        val iconBackgroundEmpty: Color,
    )

    @Composable
    fun colors(
        container: Color = System.color.backgroundSecondary,
        name: Color = System.color.textBase,
        address: Color = System.color.textBase,
        hint: Color = System.color.textSubtle,
        icon: Color = System.color.backgroundBrandBase,
        iconBackground: Color = System.color.backgroundBrandBase.copy(alpha = 0.15f),
        iconEmpty: Color = System.color.iconSubtle,
        iconBackgroundEmpty: Color = System.color.backgroundTertiary,
    ) = PlaceCardColors(
        container = container,
        name = name,
        address = address,
        hint = hint,
        icon = icon,
        iconBackground = iconBackground,
        iconEmpty = iconEmpty,
        iconBackgroundEmpty = iconBackgroundEmpty,
    )

    /**
     * Text style configuration for [PlaceCard].
     *
     * @param name Name text style.
     * @param address Address text style.
     */
    data class PlaceCardStyle(
        val name: TextStyle,
        val address: TextStyle,
    )

    @Composable
    fun style(
        name: TextStyle = System.font.title.base,
        address: TextStyle = System.font.body.caption,
    ) = PlaceCardStyle(
        name = name,
        address = address,
    )

    /**
     * Dimension configuration for [PlaceCard].
     *
     * @param radius Card corner radius.
     * @param iconRadius Icon container corner radius.
     * @param height Card height.
     * @param iconPadding Padding inside icon container.
     * @param contentPadding Padding around content.
     * @param addressMaxLines Maximum lines for address text.
     */
    data class PlaceCardDimens(
        val radius: Dp,
        val iconRadius: Dp,
        val height: Dp,
        val iconPadding: Dp,
        val contentPadding: PaddingValues,
        val addressMaxLines: Int,
    )

    @Composable
    fun dimens(
        radius: Dp = 20.dp,
        iconRadius: Dp = 14.dp,
        height: Dp = 120.dp,
        iconPadding: Dp = 10.dp,
        contentPadding: PaddingValues =
            PaddingValues(
                top = 10.dp,
                end = 10.dp,
                bottom = 8.dp,
                start = 16.dp,
            ),
        addressMaxLines: Int = 2,
    ) = PlaceCardDimens(
        radius = radius,
        iconRadius = iconRadius,
        height = height,
        iconPadding = iconPadding,
        contentPadding = contentPadding,
        addressMaxLines = addressMaxLines,
    )
}

/**
 * Saved place card displaying name, address, and icon.
 *
 * Shows different states for configured and empty places.
 *
 * @param state Current UI state.
 * @param icon Place icon.
 * @param onClick Called when card is clicked.
 * @param modifier Applied to card.
 * @param emptyHint Hint text for empty state.
 * @param colors Color configuration, defaults to [PlaceCardDefaults.colors].
 * @param style Text style configuration, defaults to [PlaceCardDefaults.style].
 * @param dimens Dimension configuration, defaults to [PlaceCardDefaults.dimens].
 */
@Composable
fun PlaceCard(
    state: PlaceCardState,
    icon: Painter?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    emptyHint: String = "Tap to add",
    colors: PlaceCardDefaults.PlaceCardColors = PlaceCardDefaults.colors(),
    style: PlaceCardDefaults.PlaceCardStyle = PlaceCardDefaults.style(),
    dimens: PlaceCardDefaults.PlaceCardDimens = PlaceCardDefaults.dimens(),
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(dimens.height),
        shape = RoundedCornerShape(dimens.radius),
        colors = CardDefaults.cardColors(containerColor = colors.container),
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier =
                Modifier
                    .fillMaxHeight()
                    .padding(dimens.contentPadding),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = state.name,
                    style = style.name,
                    color = colors.name,
                )

                if (icon != null) {
                    Surface(
                        shape = RoundedCornerShape(dimens.iconRadius),
                        color = if (state.isEmpty) colors.iconBackgroundEmpty else colors.iconBackground,
                    ) {
                        Icon(
                            painter = icon,
                            contentDescription = null,
                            tint = if (state.isEmpty) colors.iconEmpty else colors.icon,
                            modifier = Modifier.padding(dimens.iconPadding),
                        )
                    }
                }
            }

            Text(
                text = if (state.isEmpty) emptyHint else (state.address ?: ""),
                style = style.address,
                maxLines = dimens.addressMaxLines,
                color = if (state.isEmpty) colors.hint else colors.address,
            )
        }
    }
}

@Preview
@Composable
private fun PlaceCardPreview() {
    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp)
    ) {
        PlaceCard(
            state =
                PlaceCardState(
                    name = "Home",
                    address = "123 Main Street, Apartment 4B",
                ),
            icon = null,
            onClick = {},
        )
    }
}
