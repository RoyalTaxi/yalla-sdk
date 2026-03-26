package uz.yalla.composites.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Single action item for [ActionPickerSheet].
 *
 * @param title Action text.
 * @param icon Icon painter.
 * @param onClick Called when action tapped.
 * @param isDestructive Whether this is a destructive action (shows icon in red).
 * @since 0.0.1
 */
data class ActionPickerItem(
    val title: String,
    val icon: Painter,
    val onClick: () -> Unit,
    val isDestructive: Boolean = false,
)

/**
 * Color configuration for [ActionPickerSheet].
 *
 * @param container Background color.
 * @param title Title text color.
 * @param itemBackground Item background color.
 * @param itemIcon Item icon tint.
 * @param itemText Item text color.
 * @param destructiveItemIcon Icon tint for destructive actions.
 * @since 0.0.1
 */
@Immutable
data class ActionPickerColors(
    val container: Color,
    val title: Color,
    val itemBackground: Color,
    val itemIcon: Color,
    val itemText: Color,
    val destructiveItemIcon: Color,
)

/**
 * Dimension configuration for [ActionPickerSheet].
 *
 * @param shape Sheet corner shape.
 * @param contentPadding Content padding.
 * @param titleItemsSpacing Spacing between title and items.
 * @param itemSpacing Spacing between items.
 * @param itemShape Item button shape.
 * @param itemPadding Item button padding.
 * @param itemIconTextSpacing Spacing between icon and text in item.
 * @since 0.0.1
 */
@Immutable
data class ActionPickerDimens(
    val shape: Shape,
    val contentPadding: PaddingValues,
    val titleItemsSpacing: Dp,
    val itemSpacing: Dp,
    val itemShape: Shape,
    val itemPadding: PaddingValues,
    val itemIconTextSpacing: Dp,
)

/**
 * Default values for [ActionPickerSheet].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 * @since 0.0.1
 */
object ActionPickerDefaults {
    /**
     * Creates theme-aware default colors.
     *
     * @since 0.0.1
     */
    @Composable
    fun colors(
        container: Color = System.color.background.base,
        title: Color = System.color.text.base,
        itemBackground: Color = System.color.background.secondary,
        itemIcon: Color = System.color.icon.base,
        itemText: Color = System.color.text.base,
        destructiveItemIcon: Color = System.color.icon.red,
    ): ActionPickerColors =
        ActionPickerColors(
            container = container,
            title = title,
            itemBackground = itemBackground,
            itemIcon = itemIcon,
            itemText = itemText,
            destructiveItemIcon = destructiveItemIcon,
        )

    /**
     * Creates default dimensions.
     *
     * @since 0.0.1
     */
    fun dimens(
        shape: Shape = RoundedCornerShape(topStart = 38.dp, topEnd = 38.dp),
        contentPadding: PaddingValues = PaddingValues(10.dp),
        titleItemsSpacing: Dp = 24.dp,
        itemSpacing: Dp = 10.dp,
        itemShape: Shape = RoundedCornerShape(16.dp),
        itemPadding: PaddingValues = PaddingValues(18.dp),
        itemIconTextSpacing: Dp = 12.dp,
    ): ActionPickerDimens =
        ActionPickerDimens(
            shape = shape,
            contentPadding = contentPadding,
            titleItemsSpacing = titleItemsSpacing,
            itemSpacing = itemSpacing,
            itemShape = itemShape,
            itemPadding = itemPadding,
            itemIconTextSpacing = itemIconTextSpacing,
        )
}

/**
 * Sheet for selecting from multiple actions.
 *
 * Displays a list of selectable action items with icons.
 * Use for action menus, option pickers, and context actions.
 *
 * ## Usage
 *
 * ```kotlin
 * ActionPickerSheet(
 *     isVisible = showActions,
 *     onDismissRequest = { viewModel.hideActions() },
 *     title = "Change Photo",
 *     items = listOf(
 *         ActionPickerItem(
 *             title = "Take Photo",
 *             icon = painterResource(Res.drawable.ic_camera),
 *             onClick = { viewModel.takePhoto() },
 *         ),
 *         ActionPickerItem(
 *             title = "Choose from Gallery",
 *             icon = painterResource(Res.drawable.ic_gallery),
 *             onClick = { viewModel.openGallery() },
 *         ),
 *     ),
 * )
 * ```
 *
 * @param isVisible Whether sheet is visible.
 * @param onDismissRequest Called when sheet is dismissed.
 * @param title Sheet header title.
 * @param items List of selectable actions.
 * @param modifier Applied to sheet.
 * @param colors Color configuration, defaults to [ActionPickerDefaults.colors].
 * @param dimens Dimension configuration, defaults to [ActionPickerDefaults.dimens].
 *
 * @see ActionPickerDefaults for default values
 * @since 0.0.1
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionPickerSheet(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    items: List<ActionPickerItem>,
    modifier: Modifier = Modifier,
    colors: ActionPickerColors = ActionPickerDefaults.colors(),
    dimens: ActionPickerDimens = ActionPickerDefaults.dimens(),
) {
    Sheet(
        isVisible = isVisible,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        colors = SheetDefaults.colors(container = colors.container),
        dimens = SheetDefaults.dimens(shape = dimens.shape),
    ) {
        Column(modifier = Modifier.padding(dimens.contentPadding)) {
            SheetHeader(
                onClose = onDismissRequest,
                title = title,
                colors = SheetHeaderDefaults.colors(title = colors.title),
            )

            Spacer(modifier = Modifier.height(dimens.titleItemsSpacing))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(dimens.itemSpacing)) {
                items(items) { item ->
                    ActionPickerItemRow(
                        item = item,
                        colors = colors,
                        dimens = dimens,
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionPickerItemRow(
    item: ActionPickerItem,
    colors: ActionPickerColors,
    dimens: ActionPickerDimens,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = item.onClick,
        shape = dimens.itemShape,
        contentPadding = dimens.itemPadding,
        colors = ButtonDefaults.buttonColors(colors.itemBackground),
        modifier = modifier,
    ) {
        Icon(
            painter = item.icon,
            contentDescription = null,
            tint = if (item.isDestructive) colors.destructiveItemIcon else colors.itemIcon,
        )

        Spacer(modifier = Modifier.width(dimens.itemIconTextSpacing))

        Text(
            text = item.title,
            color = colors.itemText,
            style = System.font.body.base.medium,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
