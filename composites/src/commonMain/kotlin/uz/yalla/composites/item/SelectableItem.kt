package uz.yalla.composites.item

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import uz.yalla.design.theme.System
import uz.yalla.foundation.model.SelectableItemModel
import uz.yalla.resources.Res
import uz.yalla.resources.ic_checked

/**
 * State for [SelectableItem].
 *
 * @param T Type of the underlying data item.
 * @property item Selectable item model with icon and title.
 * @property isSelected Whether this item is currently selected.
 */
data class SelectableItemState<T>(
    val item: SelectableItemModel<T>,
    val isSelected: Boolean,
)

/**
 * Default configuration values for [SelectableItem].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object SelectableItemDefaults {
    /**
     * Color configuration for [SelectableItem].
     *
     * @param selectedContainer Background color when selected.
     * @param unselectedContainer Background color when not selected.
     * @param border Border color when not selected.
     * @param text Text color.
     */
    data class SelectableItemColors(
        val selectedContainer: Color,
        val unselectedContainer: Color,
        val border: Color,
        val text: Color
    )

    @Composable
    fun colors(
        selectedContainer: Color = System.color.backgroundSecondary,
        unselectedContainer: Color = Color.Transparent,
        border: Color = System.color.borderDisabled,
        text: Color = System.color.textBase
    ) = SelectableItemColors(
        selectedContainer = selectedContainer,
        unselectedContainer = unselectedContainer,
        border = border,
        text = text
    )

    /**
     * Text style configuration for [SelectableItem].
     *
     * @param title Style applied to the item title.
     */
    data class SelectableItemStyle(
        val title: TextStyle
    )

    @Composable
    fun style(title: TextStyle = System.font.body.small.medium) =
        SelectableItemStyle(
            title = title
        )

    /**
     * Dimension configuration for [SelectableItem].
     *
     * @param shape Shape of the item container.
     * @param contentPadding Padding inside the item.
     * @param iconSpacing Spacing between icon and text.
     * @param borderWidth Border width when not selected.
     */
    data class SelectableItemDimens(
        val shape: Shape,
        val contentPadding: PaddingValues,
        val iconSpacing: Dp,
        val borderWidth: Dp
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(16.dp),
        contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        iconSpacing: Dp = 12.dp,
        borderWidth: Dp = 1.dp
    ) = SelectableItemDimens(
        shape = shape,
        contentPadding = contentPadding,
        iconSpacing = iconSpacing,
        borderWidth = borderWidth
    )
}

/**
 * Selectable list item with icon and checkmark.
 *
 * ## Usage
 *
 * ```kotlin
 * SelectableItem(
 *     state = SelectableItemState(
 *         item = SelectableItemModel(
 *             item = paymentType,
 *             title = "Credit Card",
 *             icon = painterResource(Res.drawable.ic_card),
 *             iconColor = System.color.iconBase
 *         ),
 *         isSelected = selectedType == paymentType,
 *     ),
 *     onSelect = { selectPaymentType(paymentType) },
 * )
 * ```
 *
 * @param T Type of the underlying data item.
 * @param state Item state with model and selection status.
 * @param onSelect Invoked when item is selected.
 * @param modifier Applied to surface.
 * @param colors Color configuration, defaults to [SelectableItemDefaults.colors].
 * @param style Text style configuration, defaults to [SelectableItemDefaults.style].
 * @param dimens Dimension configuration, defaults to [SelectableItemDefaults.dimens].
 *
 * @see SelectableItemState for state configuration
 * @see SelectableItemDefaults for default values
 */
@Composable
fun <T> SelectableItem(
    state: SelectableItemState<T>,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    colors: SelectableItemDefaults.SelectableItemColors = SelectableItemDefaults.colors(),
    style: SelectableItemDefaults.SelectableItemStyle = SelectableItemDefaults.style(),
    dimens: SelectableItemDefaults.SelectableItemDimens = SelectableItemDefaults.dimens(),
) {
    Surface(
        modifier = modifier,
        onClick = onSelect,
        shape = dimens.shape,
        color = if (state.isSelected) colors.selectedContainer else colors.unselectedContainer,
        border =
            BorderStroke(
                width = if (state.isSelected) 0.dp else dimens.borderWidth,
                color = colors.border
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(dimens.contentPadding)
        ) {
            Icon(
                painter = state.item.icon,
                contentDescription = null,
                tint = state.item.iconColor
            )

            Spacer(modifier = Modifier.width(dimens.iconSpacing))

            Text(
                text = state.item.title,
                color = colors.text,
                style = style.title
            )

            Spacer(modifier = Modifier.weight(1f))

            if (state.isSelected) {
                Icon(
                    painter = painterResource(Res.drawable.ic_checked),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
        }
    }
}
