package uz.yalla.components.composite.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import uz.yalla.design.theme.System
import uz.yalla.resources.Res
import uz.yalla.resources.ic_checkbox
import uz.yalla.resources.ic_checkbox_border

/**
 * State for [SelectableCard] component.
 *
 * @property selected Whether card is selected.
 * @property enabled Whether card is enabled.
 */
data class SelectableCardState(
    val selected: Boolean,
    val enabled: Boolean = true,
)

/**
 * Selectable card with checkbox indicator.
 *
 * Use for single or multi-selection lists.
 *
 * ## Usage
 *
 * ```kotlin
 * SelectableCard(
 *     state = SelectableCardState(selected = isSelected),
 *     onClick = { onSelect() },
 *     leadingIcon = { Icon(painterResource(Res.drawable.ic_cash), null) },
 *     content = { Text("Cash Payment") },
 * )
 * ```
 *
 * @param state Card state containing selected and enabled.
 * @param onClick Called when card is clicked.
 * @param modifier Applied to card.
 * @param leadingIcon Optional icon in leading position.
 * @param colors Color configuration, defaults to [SelectableCardDefaults.colors].
 * @param dimens Dimension configuration, defaults to [SelectableCardDefaults.dimens].
 * @param content Card content.
 *
 * @see SelectableCardState for state configuration
 * @see SelectableCardDefaults for default values
 */
@Composable
fun SelectableCard(
    state: SelectableCardState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    colors: SelectableCardDefaults.SelectableCardColors = SelectableCardDefaults.colors(),
    dimens: SelectableCardDefaults.SelectableCardDimens = SelectableCardDefaults.dimens(),
    content: @Composable () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        enabled = state.enabled,
        shape = dimens.shape,
        colors =
            CardDefaults.cardColors(
                containerColor = colors.container,
                disabledContainerColor = colors.disabledContainer,
            ),
    ) {
        Row(
            modifier =
                Modifier.padding(
                    horizontal = dimens.horizontalPadding,
                    vertical = dimens.verticalPadding,
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leadingIcon != null) {
                Box(
                    modifier =
                        Modifier
                            .size(dimens.iconContainerSize)
                            .clip(dimens.iconShape)
                            .background(colors.iconBackground)
                            .padding(dimens.iconPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    leadingIcon()
                }
                Spacer(Modifier.width(dimens.contentSpacing))
            }

            Box(
                modifier = Modifier.weight(1f),
            ) {
                content()
            }

            Spacer(Modifier.width(dimens.contentSpacing))

            Icon(
                painter =
                    painterResource(
                        if (state.selected) Res.drawable.ic_checkbox else Res.drawable.ic_checkbox_border
                    ),
                contentDescription = null,
                tint = Color.Unspecified,
            )
        }
    }
}

/**
 * Default configuration values for [SelectableCard].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 */
object SelectableCardDefaults {
    /**
     * Color configuration for [SelectableCard].
     *
     * @param container Background color when enabled.
     * @param iconBackground Background color for icon container.
     * @param disabledContainer Background color when disabled.
     */
    data class SelectableCardColors(
        val container: Color,
        val iconBackground: Color,
        val disabledContainer: Color,
    )

    @Composable
    fun colors(
        container: Color = Color.Transparent,
        iconBackground: Color = System.color.backgroundSecondary,
        disabledContainer: Color = Color.Transparent,
    ) = SelectableCardColors(
        container = container,
        iconBackground = iconBackground,
        disabledContainer = disabledContainer,
    )

    /**
     * Dimension configuration for [SelectableCard].
     *
     * @param shape Card corner shape.
     * @param iconShape Icon container shape.
     * @param iconContainerSize Size of the icon container.
     * @param iconPadding Padding inside icon container.
     * @param horizontalPadding Horizontal padding for content.
     * @param verticalPadding Vertical padding for content.
     * @param contentSpacing Spacing between elements.
     */
    data class SelectableCardDimens(
        val shape: Shape,
        val iconShape: Shape,
        val iconContainerSize: Dp,
        val iconPadding: Dp,
        val horizontalPadding: Dp,
        val verticalPadding: Dp,
        val contentSpacing: Dp,
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(0.dp),
        iconShape: Shape = RoundedCornerShape(10.dp),
        iconContainerSize: Dp = 44.dp,
        iconPadding: Dp = 10.dp,
        horizontalPadding: Dp = 16.dp,
        verticalPadding: Dp = 10.dp,
        contentSpacing: Dp = 16.dp,
    ) = SelectableCardDimens(
        shape = shape,
        iconShape = iconShape,
        iconContainerSize = iconContainerSize,
        iconPadding = iconPadding,
        horizontalPadding = horizontalPadding,
        verticalPadding = verticalPadding,
        contentSpacing = contentSpacing,
    )
}

@Preview
@Composable
private fun SelectableCardPreview() {
    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp)
    ) {
        SelectableCard(
            state = SelectableCardState(selected = true),
            onClick = {},
            content = { Text("Cash Payment") },
        )
    }
}
