package uz.yalla.composites.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * List item with icon, text, and action button.
 *
 * Use for items with a specific action like delete.
 *
 * ## Usage
 *
 * ```kotlin
 * ActionItem(
 *     title = "Humo •••• 1234",
 *     leadingIcon = { Icon(painterResource(Res.drawable.ic_humo), null) },
 *     action = { Icon(painterResource(Res.drawable.ic_trash), null) },
 *     onActionClick = { deleteCard(cardId) },
 * )
 * ```
 *
 * @param title Item text.
 * @param action Action button content.
 * @param onActionClick Called when action is clicked.
 * @param modifier Applied to item.
 * @param leadingIcon Optional icon before title.
 * @param colors Color configuration, defaults to [ActionItemDefaults.colors].
 * @param style Text style configuration, defaults to [ActionItemDefaults.style].
 * @param dimens Dimension configuration, defaults to [ActionItemDefaults.dimens].
 *
 * @see ActionItemDefaults for default values
 */
@Composable
fun ActionItem(
    title: String,
    action: @Composable () -> Unit,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    colors: ActionItemDefaults.ActionItemColors = ActionItemDefaults.colors(),
    style: ActionItemDefaults.ActionItemStyle = ActionItemDefaults.style(),
    dimens: ActionItemDefaults.ActionItemDimens = ActionItemDefaults.dimens(),
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(dimens.contentPadding),
    ) {
        if (leadingIcon != null) {
            Box(
                modifier =
                    Modifier
                        .size(dimens.iconContainerSize)
                        .background(
                            color = colors.iconBackground,
                            shape = dimens.iconShape,
                        ).padding(dimens.iconPadding),
                contentAlignment = Alignment.Center,
            ) {
                leadingIcon()
            }
            Spacer(Modifier.width(dimens.contentSpacing))
        }

        Text(
            text = title,
            style = style.title,
            color = colors.title,
            modifier = Modifier.weight(1f),
        )

        Spacer(Modifier.width(dimens.contentSpacing))

        IconButton(onClick = onActionClick) {
            action()
        }
    }
}

/**
 * Default configuration values for [ActionItem].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object ActionItemDefaults {
    /**
     * Color configuration for [ActionItem].
     *
     * @param iconBackground Background color for icon container.
     * @param title Title text color.
     */
    data class ActionItemColors(
        val iconBackground: Color,
        val title: Color,
    )

    @Composable
    fun colors(
        iconBackground: Color = System.color.backgroundSecondary,
        title: Color = System.color.textBase,
    ) = ActionItemColors(
        iconBackground = iconBackground,
        title = title,
    )

    /**
     * Text style configuration for [ActionItem].
     *
     * @param title Title text style.
     */
    data class ActionItemStyle(
        val title: TextStyle,
    )

    @Composable
    fun style(title: TextStyle = System.font.body.base.bold) =
        ActionItemStyle(
            title = title,
        )

    /**
     * Dimension configuration for [ActionItem].
     *
     * @param iconShape Shape for icon container.
     * @param iconContainerSize Size of icon container.
     * @param iconPadding Padding inside icon container.
     * @param contentPadding Padding around content.
     * @param contentSpacing Spacing between elements.
     */
    data class ActionItemDimens(
        val iconShape: Shape,
        val iconContainerSize: Dp,
        val iconPadding: Dp,
        val contentPadding: PaddingValues,
        val contentSpacing: Dp,
    )

    @Composable
    fun dimens(
        iconShape: Shape = RoundedCornerShape(10.dp),
        iconContainerSize: Dp = 44.dp,
        iconPadding: Dp = 10.dp,
        contentPadding: PaddingValues = PaddingValues(vertical = 10.dp),
        contentSpacing: Dp = 16.dp,
    ) = ActionItemDimens(
        iconShape = iconShape,
        iconContainerSize = iconContainerSize,
        iconPadding = iconPadding,
        contentPadding = contentPadding,
        contentSpacing = contentSpacing,
    )
}

@Preview
@Composable
private fun ActionItemPreview() {
    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp)
    ) {
        ActionItem(
            title = "Humo •••• 1234",
            action = { Text("X") },
            onActionClick = {},
        )
    }
}
