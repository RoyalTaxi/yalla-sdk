package uz.yalla.components.composite.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import uz.yalla.components.util.formatArgs
import uz.yalla.design.theme.System
import uz.yalla.platform.toggle.NativeSwitch
import uz.yalla.resources.Res
import uz.yalla.resources.bonus_balance
import uz.yalla.resources.bonus_pay
import uz.yalla.resources.ic_coin

/**
 * State for [EnableBonusCard].
 *
 * @property balance Current bonus balance.
 * @property isBonusEnabled Whether bonus is currently enabled.
 * @property enabled Whether the switch is interactive.
 */
data class EnableBonusCardState(
    val balance: Long,
    val isBonusEnabled: Boolean,
    val enabled: Boolean = true,
)

/**
 * Default configuration values for [EnableBonusCard].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object EnableBonusCardDefaults {
    /**
     * Color configuration for [EnableBonusCard].
     *
     * @param container Card background color.
     * @param iconBackground Background color of the icon container.
     * @param title Title text color.
     * @param subtitle Subtitle text color.
     */
    data class EnableBonusCardColors(
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
    ) = EnableBonusCardColors(
        container = container,
        iconBackground = iconBackground,
        title = title,
        subtitle = subtitle,
    )

    /**
     * Text style configuration for [EnableBonusCard].
     *
     * @param title Style for the title text.
     * @param subtitle Style for the subtitle text.
     */
    data class EnableBonusCardStyle(
        val title: TextStyle,
        val subtitle: TextStyle
    )

    @Composable
    fun style(
        title: TextStyle = System.font.body.base.bold,
        subtitle: TextStyle = System.font.body.small.medium
    ) = EnableBonusCardStyle(
        title = title,
        subtitle = subtitle
    )

    /**
     * Dimension configuration for [EnableBonusCard].
     *
     * @param contentPadding Padding inside the card.
     * @param iconSize Size of the icon container.
     * @param iconBackgroundShape Shape of the icon background.
     * @param iconPadding Padding inside the icon container.
     * @param iconSpacing Spacing between icon and text.
     * @param textSpacing Spacing between title and subtitle.
     * @param trailingSpacing Spacing before switch.
     */
    data class EnableBonusCardDimens(
        val contentPadding: PaddingValues,
        val iconSize: Dp,
        val iconBackgroundShape: Shape,
        val iconPadding: Dp,
        val iconSpacing: Dp,
        val textSpacing: Dp,
        val trailingSpacing: Dp
    )

    @Composable
    fun dimens(
        contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        iconSize: Dp = 44.dp,
        iconBackgroundShape: Shape = RoundedCornerShape(10.dp),
        iconPadding: Dp = 10.dp,
        iconSpacing: Dp = 16.dp,
        textSpacing: Dp = 4.dp,
        trailingSpacing: Dp = 28.dp
    ) = EnableBonusCardDimens(
        contentPadding = contentPadding,
        iconSize = iconSize,
        iconBackgroundShape = iconBackgroundShape,
        iconPadding = iconPadding,
        iconSpacing = iconSpacing,
        textSpacing = textSpacing,
        trailingSpacing = trailingSpacing
    )
}

/**
 * Card with switch to enable/disable bonus payment.
 *
 * ## Usage
 *
 * ```kotlin
 * EnableBonusCard(
 *     state = EnableBonusCardState(
 *         balance = 10000,
 *         isBonusEnabled = state.bonusEnabled,
 *     ),
 *     onSwitchChecked = { viewModel.toggleBonus(it) },
 * )
 * ```
 *
 * @param state Card state with balance and enabled status.
 * @param onSwitchChecked Called when switch is toggled.
 * @param modifier Applied to the card.
 * @param colors Color configuration, defaults to [EnableBonusCardDefaults.colors].
 * @param style Text style configuration, defaults to [EnableBonusCardDefaults.style].
 * @param dimens Dimension configuration, defaults to [EnableBonusCardDefaults.dimens].
 *
 * @see EnableBonusCardState for state configuration
 * @see EnableBonusCardDefaults for default values
 */
@Composable
fun EnableBonusCard(
    state: EnableBonusCardState,
    onSwitchChecked: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    colors: EnableBonusCardDefaults.EnableBonusCardColors = EnableBonusCardDefaults.colors(),
    style: EnableBonusCardDefaults.EnableBonusCardStyle = EnableBonusCardDefaults.style(),
    dimens: EnableBonusCardDefaults.EnableBonusCardDimens = EnableBonusCardDefaults.dimens(),
) {
    Card(
        modifier = modifier,
        colors =
            CardDefaults.cardColors(
                containerColor = colors.container,
                disabledContainerColor = colors.container
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(dimens.contentPadding)
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_coin),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier =
                    Modifier
                        .size(dimens.iconSize)
                        .background(
                            color = colors.iconBackground,
                            shape = dimens.iconBackgroundShape
                        ).padding(dimens.iconPadding)
            )

            Spacer(modifier = Modifier.width(dimens.iconSpacing))

            Column(
                verticalArrangement = Arrangement.spacedBy(dimens.textSpacing),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(Res.string.bonus_pay),
                    style = style.title,
                    color = colors.title
                )

                Text(
                    text = stringResource(Res.string.bonus_balance).formatArgs(state.balance.toString()),
                    style = style.subtitle,
                    color = colors.subtitle
                )
            }

            Spacer(modifier = Modifier.width(dimens.trailingSpacing))

            NativeSwitch(
                checked = state.isBonusEnabled,
                onCheckedChange = onSwitchChecked,
                enabled = state.enabled,
            )
        }
    }
}
