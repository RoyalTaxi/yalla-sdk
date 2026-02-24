package uz.yalla.composites.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * State for [RadioItem].
 *
 * @property text Item label text.
 * @property selected Whether this item is selected.
 * @property checkedIcon Icon to show when selected.
 * @property uncheckedIcon Icon to show when not selected.
 */
data class RadioItemState(
    val text: String,
    val selected: Boolean,
    val checkedIcon: Painter,
    val uncheckedIcon: Painter,
)

/**
 * Selectable item with radio-style indicator.
 *
 * Displays a text label with a checked/unchecked icon indicator.
 * Commonly used in single-selection lists.
 *
 * ## Usage
 *
 * ```kotlin
 * RadioItem(
 *     state = RadioItemState(
 *         text = "Option 1",
 *         selected = selectedIndex == 0,
 *         checkedIcon = painterResource(Res.drawable.ic_checked),
 *         uncheckedIcon = painterResource(Res.drawable.ic_unchecked),
 *     ),
 *     onClick = { selectedIndex = 0 },
 * )
 * ```
 *
 * @param state Item state containing text, selection, and icons.
 * @param onClick Called when item is clicked.
 * @param modifier Modifier for the item.
 * @param colors Color configuration, defaults to [RadioItemDefaults.colors].
 * @param style Text style configuration, defaults to [RadioItemDefaults.style].
 * @param dimens Dimension configuration, defaults to [RadioItemDefaults.dimens].
 *
 * @see RadioItemState for state configuration
 * @see RadioItemDefaults for default values
 */
@Composable
fun RadioItem(
    state: RadioItemState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: RadioItemDefaults.RadioItemColors = RadioItemDefaults.colors(),
    style: RadioItemDefaults.RadioItemStyle = RadioItemDefaults.style(),
    dimens: RadioItemDefaults.RadioItemDimens = RadioItemDefaults.dimens(),
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        color = Color.Transparent,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.contentSpacing),
            modifier =
                Modifier
                    .height(dimens.height)
                    .padding(horizontal = dimens.horizontalPadding),
        ) {
            Text(
                text = state.text,
                color = colors.text,
                style = style.text,
                maxLines = dimens.textMaxLines,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )

            Image(
                painter = if (state.selected) state.checkedIcon else state.uncheckedIcon,
                contentDescription = null,
                modifier = Modifier.size(dimens.iconSize),
            )
        }
    }
}

/**
 * Default configuration values for [RadioItem].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object RadioItemDefaults {
    /**
     * Color configuration for [RadioItem].
     *
     * @param text Text label color.
     */
    data class RadioItemColors(
        val text: Color,
    )

    @Composable
    fun colors(text: Color = System.color.textBase) =
        RadioItemColors(
            text = text,
        )

    /**
     * Text style configuration for [RadioItem].
     *
     * @param text Text label style.
     */
    data class RadioItemStyle(
        val text: TextStyle,
    )

    @Composable
    fun style(text: TextStyle = System.font.body.base.bold) =
        RadioItemStyle(
            text = text,
        )

    /**
     * Dimension configuration for [RadioItem].
     *
     * @param height Item height.
     * @param horizontalPadding Horizontal padding.
     * @param contentSpacing Spacing between text and icon.
     * @param iconSize Icon size.
     * @param textMaxLines Maximum lines for text label.
     */
    data class RadioItemDimens(
        val height: Dp,
        val horizontalPadding: Dp,
        val contentSpacing: Dp,
        val iconSize: Dp,
        val textMaxLines: Int,
    )

    @Composable
    fun dimens(
        height: Dp = 64.dp,
        horizontalPadding: Dp = 20.dp,
        contentSpacing: Dp = 16.dp,
        iconSize: Dp = 24.dp,
        textMaxLines: Int = 1,
    ) = RadioItemDimens(
        height = height,
        horizontalPadding = horizontalPadding,
        contentSpacing = contentSpacing,
        iconSize = iconSize,
        textMaxLines = textMaxLines,
    )
}
