package uz.yalla.components.composite.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import uz.yalla.design.theme.System
import uz.yalla.platform.toggle.NativeSwitch

/**
 * State for [SwitchCard].
 *
 * @property checked Whether switch is checked.
 * @property title Primary title text.
 * @property subtitle Optional secondary text.
 * @property enabled Whether card and switch are enabled.
 */
data class SwitchCardState(
    val checked: Boolean,
    val title: String,
    val subtitle: String? = null,
    val enabled: Boolean = true,
)

/**
 * Card with toggle switch for enabling/disabling features.
 *
 * Use for settings or feature toggles within cards.
 *
 * ## Usage
 *
 * ```kotlin
 * SwitchCard(
 *     state = SwitchCardState(
 *         checked = bonusEnabled,
 *         title = "Pay with bonus",
 *         subtitle = "Balance: 50,000 sum",
 *     ),
 *     onCheckedChange = { viewModel.toggleBonus(it) },
 *     leadingIcon = { Icon(painterResource(Res.drawable.ic_coin), null) },
 * )
 * ```
 *
 * @param state Switch card state.
 * @param onCheckedChange Called when switch value changes.
 * @param modifier Applied to card.
 * @param leadingIcon Optional icon before content.
 * @param colors Color configuration, defaults to [SwitchCardDefaults.colors].
 * @param dimens Dimension configuration, defaults to [SwitchCardDefaults.dimens].
 *
 * @see SwitchCardState for state configuration
 * @see SwitchCardDefaults for default values
 */
@Composable
fun SwitchCard(
    state: SwitchCardState,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    colors: SwitchCardDefaults.SwitchCardColors = SwitchCardDefaults.colors(),
    dimens: SwitchCardDefaults.SwitchCardDimens = SwitchCardDefaults.dimens(),
) {
    Card(
        modifier = modifier,
        shape = dimens.shape,
        colors =
            CardDefaults.cardColors(
                containerColor = colors.container,
                disabledContainerColor = colors.container,
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

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = state.title,
                    style = System.font.body.base.bold,
                    color = colors.title,
                )
                if (state.subtitle != null) {
                    Text(
                        text = state.subtitle,
                        style = System.font.body.small.medium,
                        color = colors.subtitle,
                    )
                }
            }

            Spacer(Modifier.width(dimens.contentSpacing))

            NativeSwitch(
                checked = state.checked,
                onCheckedChange = onCheckedChange,
                enabled = state.enabled,
            )
        }
    }
}

/**
 * Default values for [SwitchCard].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 */
object SwitchCardDefaults {
    /**
     * Color configuration for [SwitchCard].
     *
     * @param container Background color.
     * @param iconBackground Icon container background.
     * @param title Title text color.
     * @param subtitle Subtitle text color.
     */
    data class SwitchCardColors(
        val container: Color,
        val iconBackground: Color,
        val title: Color,
        val subtitle: Color,
    )

    @Composable
    fun colors(
        container: Color = Color.Transparent,
        iconBackground: Color = System.color.backgroundSecondary,
        title: Color = System.color.textBase,
        subtitle: Color = System.color.textBase,
    ): SwitchCardColors =
        SwitchCardColors(
            container = container,
            iconBackground = iconBackground,
            title = title,
            subtitle = subtitle,
        )

    /**
     * Dimension configuration for [SwitchCard].
     *
     * @param shape Card corner shape.
     * @param iconShape Icon container shape.
     * @param iconContainerSize Icon container size.
     * @param iconPadding Icon padding.
     * @param horizontalPadding Horizontal padding.
     * @param verticalPadding Vertical padding.
     * @param contentSpacing Spacing between elements.
     */
    data class SwitchCardDimens(
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
    ): SwitchCardDimens =
        SwitchCardDimens(
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
private fun SwitchCardPreview() {
    SwitchCard(
        state =
            SwitchCardState(
                checked = true,
                title = "Pay with bonus",
                subtitle = "Balance: 50,000 sum",
            ),
        onCheckedChange = {},
    )
}
