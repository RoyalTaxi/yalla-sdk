package uz.yalla.composites.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.platform.toggle.NativeSwitch

/**
 * Color configuration for [ToggleCard].
 *
 * @param container Card background color.
 * @param iconBackground Background color of the leading icon container.
 * @since 0.0.5-alpha11
 */
@Immutable
data class ToggleCardColors(
    val container: Color,
    val iconBackground: Color,
)

/**
 * Dimension configuration for [ToggleCard].
 *
 * @param contentPadding Padding inside the card.
 * @param iconSize Size of the leading icon container.
 * @param iconShape Shape of the leading icon background.
 * @param iconPadding Padding inside the icon container.
 * @param iconSpacing Spacing between icon and content.
 * @param trailingSpacing Spacing before the switch.
 * @since 0.0.5-alpha11
 */
@Immutable
data class ToggleCardDimens(
    val contentPadding: PaddingValues,
    val iconSize: Dp,
    val iconShape: Shape,
    val iconPadding: Dp,
    val iconSpacing: Dp,
    val trailingSpacing: Dp,
)

/**
 * Default configuration values for [ToggleCard].
 *
 * @since 0.0.5-alpha11
 */
object ToggleCardDefaults {

    /**
     * Creates theme-aware default colors.
     */
    @Composable
    fun colors(
        container: Color = Color.Transparent,
        iconBackground: Color = System.color.background.secondary,
    ): ToggleCardColors = ToggleCardColors(
        container = container,
        iconBackground = iconBackground,
    )

    /**
     * Creates default dimensions.
     */
    fun dimens(
        contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        iconSize: Dp = 44.dp,
        iconShape: Shape = RoundedCornerShape(10.dp),
        iconPadding: Dp = 10.dp,
        iconSpacing: Dp = 16.dp,
        trailingSpacing: Dp = 28.dp,
    ): ToggleCardDimens = ToggleCardDimens(
        contentPadding = contentPadding,
        iconSize = iconSize,
        iconShape = iconShape,
        iconPadding = iconPadding,
        iconSpacing = iconSpacing,
        trailingSpacing = trailingSpacing,
    )
}

/**
 * Card with a toggle switch for on/off settings.
 *
 * Built on [ContentCard] with a leading icon container and trailing [NativeSwitch].
 *
 * ## Usage
 *
 * ```kotlin
 * ToggleCard(
 *     checked = isEnabled,
 *     onCheckedChange = { viewModel.toggle(it) },
 *     leadingIcon = { Icon(painterResource(Res.drawable.img_coin), null) },
 * ) {
 *     Column {
 *         Text("Use Bonus", style = System.font.body.base.bold)
 *         Text("Balance: 10,000", style = System.font.body.small.medium)
 *     }
 * }
 * ```
 *
 * @param checked Whether the switch is on.
 * @param onCheckedChange Called when the switch is toggled.
 * @param modifier Applied to the root card.
 * @param enabled Whether the switch is interactive.
 * @param colors Color configuration, defaults to [ToggleCardDefaults.colors].
 * @param dimens Dimension configuration, defaults to [ToggleCardDefaults.dimens].
 * @param leadingIcon Optional icon displayed in a styled container before content.
 * @param content Card content, typically title and subtitle [Text][androidx.compose.material3.Text].
 *
 * @see ContentCard
 * @see ToggleCardDefaults
 * @since 0.0.5-alpha11
 */
@Composable
fun ToggleCard(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ToggleCardColors = ToggleCardDefaults.colors(),
    dimens: ToggleCardDimens = ToggleCardDefaults.dimens(),
    leadingIcon: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    ContentCard(
        modifier = modifier,
        colors = ContentCardDefaults.colors(
            container = colors.container,
            disabledContainer = colors.container,
        ),
        dimens = ContentCardDefaults.dimens(
            contentPadding = dimens.contentPadding,
        ),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(dimens.iconSize)
                        .clip(dimens.iconShape)
                        .background(colors.iconBackground)
                        .padding(dimens.iconPadding),
                ) {
                    leadingIcon()
                }

                Spacer(Modifier.width(dimens.iconSpacing))
            }

            Box(Modifier.weight(1f)) {
                content()
            }

            Spacer(Modifier.width(dimens.trailingSpacing))

            NativeSwitch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
            )
        }
    }
}
