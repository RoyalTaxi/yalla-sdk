package uz.yalla.composites.item

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.resources.icons.Checked
import uz.yalla.resources.icons.YallaIcons

/**
 * Color configuration for [SelectableItem].
 *
 * @param selectedContainer Background color when selected.
 * @param unselectedContainer Background color when not selected.
 * @param border Border color when not selected.
 * @param text Text color for the title.
 * @since 0.0.1
 */
@Immutable
data class SelectableItemColors(
    val selectedContainer: Color,
    val unselectedContainer: Color,
    val border: Color,
    val text: Color,
)

/**
 * Dimension configuration for [SelectableItem].
 *
 * @param shape Item surface shape.
 * @param contentPadding Padding around item content.
 * @param iconSpacing Spacing between the optional icon and the title text.
 * @param borderWidth Border stroke width when not selected.
 * @since 0.0.1
 */
@Immutable
data class SelectableItemDimens(
    val shape: Shape,
    val contentPadding: PaddingValues,
    val iconSpacing: Dp,
    val borderWidth: Dp,
)

/**
 * Default configuration values for [SelectableItem].
 *
 * @since 0.0.1
 */
object SelectableItemDefaults {

    /**
     * Creates theme-aware default colors.
     */
    @Composable
    fun colors(
        selectedContainer: Color = System.color.background.secondary,
        unselectedContainer: Color = Color.Transparent,
        border: Color = System.color.border.disabled,
        text: Color = System.color.text.base,
    ) = SelectableItemColors(
        selectedContainer = selectedContainer,
        unselectedContainer = unselectedContainer,
        border = border,
        text = text,
    )

    /**
     * Creates default dimensions.
     */
    fun dimens(
        shape: Shape = RoundedCornerShape(16.dp),
        contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        iconSpacing: Dp = 12.dp,
        borderWidth: Dp = 1.dp,
    ) = SelectableItemDimens(
        shape = shape,
        contentPadding = contentPadding,
        iconSpacing = iconSpacing,
        borderWidth = borderWidth,
    )
}

/**
 * Single-choice selectable item with a check indicator.
 *
 * Renders a [Surface][androidx.compose.material3.Surface] with a title, optional leading icon,
 * and a trailing check icon when selected. The border disappears and the background changes
 * to indicate selection.
 *
 * The default text style and color for [title] are injected via
 * [ProvideTextStyle][androidx.compose.material3.ProvideTextStyle]; a plain
 * [Text][androidx.compose.material3.Text] in the slot inherits them automatically.
 *
 * Designed for use inside [SelectionSheet] or any single-choice list.
 *
 * ## Usage
 *
 * ```kotlin
 * SelectableItem(
 *     title = { Text("English") },
 *     isSelected = currentLanguage == "en",
 *     onSelect = { selectLanguage("en") },
 *     icon = { Icon(YallaIcons.Globe, null) },
 * )
 * ```
 *
 * @param title Primary content; receives [System.font.body.small.medium] style by default.
 * @param isSelected Whether this item is currently selected.
 * @param onSelect Called when the item is tapped.
 * @param modifier Applied to the root surface.
 * @param icon Optional leading icon composable.
 * @param colors Color configuration, defaults to [SelectableItemDefaults.colors].
 * @param dimens Dimension configuration, defaults to [SelectableItemDefaults.dimens].
 *
 * @see SelectableItemDefaults
 * @see uz.yalla.composites.sheet.SelectionSheet
 * @since 0.0.1
 */
@Composable
fun SelectableItem(
    title: @Composable () -> Unit,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    colors: SelectableItemColors = SelectableItemDefaults.colors(),
    dimens: SelectableItemDimens = SelectableItemDefaults.dimens(),
) {
    Surface(
        modifier = modifier,
        onClick = onSelect,
        shape = dimens.shape,
        color = if (isSelected) colors.selectedContainer else colors.unselectedContainer,
        border =
            BorderStroke(
                width = if (isSelected) 0.dp else dimens.borderWidth,
                color = colors.border,
            ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(dimens.contentPadding),
        ) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(dimens.iconSpacing))
            }

            ProvideTextStyle(System.font.body.small.medium.copy(color = colors.text)) {
                title()
            }

            Spacer(modifier = Modifier.weight(1f))

            if (isSelected) {
                Icon(
                    painter = rememberVectorPainter(YallaIcons.Checked),
                    contentDescription = null,
                    tint = Color.Unspecified,
                )
            }
        }
    }
}
