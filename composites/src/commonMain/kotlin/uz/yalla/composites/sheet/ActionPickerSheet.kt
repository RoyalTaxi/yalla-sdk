package uz.yalla.composites.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.platform.button.SheetIconButton
import uz.yalla.platform.model.IconType

/**
 * Single action item for [ActionPickerSheet].
 *
 * @param title Action text.
 * @param icon Icon painter.
 * @param onClick Called when action tapped.
 * @param isDestructive Whether this is a destructive action (shows icon in red).
 */
data class ActionPickerItem(
    val title: String,
    val icon: Painter,
    val onClick: () -> Unit,
    val isDestructive: Boolean = false,
)

/**
 * State for [ActionPickerSheet].
 *
 * @property isVisible Whether sheet is visible.
 * @property title Sheet header title.
 * @property items List of selectable actions.
 */
data class ActionPickerSheetState(
    val isVisible: Boolean,
    val title: String,
    val items: List<ActionPickerItem>,
)

/**
 * Effects emitted by [ActionPickerSheet].
 */
sealed interface ActionPickerSheetEffect {
    /** User dismissed the sheet. */
    data object Dismiss : ActionPickerSheetEffect
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
 *     state = ActionPickerSheetState(
 *         isVisible = showActions,
 *         title = "Change Photo",
 *         items = listOf(
 *             ActionPickerItem(
 *                 title = "Take Photo",
 *                 icon = painterResource(Res.drawable.ic_camera),
 *                 onClick = { viewModel.takePhoto() },
 *             ),
 *             ActionPickerItem(
 *                 title = "Choose from Gallery",
 *                 icon = painterResource(Res.drawable.ic_gallery),
 *                 onClick = { viewModel.openGallery() },
 *             ),
 *         ),
 *     ),
 *     onEffect = { effect ->
 *         when (effect) {
 *             ActionPickerSheetEffect.Dismiss -> viewModel.hideActions()
 *         }
 *     },
 * )
 * ```
 *
 * @param state Sheet state including visibility, title, and items.
 * @param onEffect Callback for sheet effects (dismiss).
 * @param modifier Applied to sheet.
 * @param colors Color configuration, defaults to [ActionPickerDefaults.colors].
 * @param dimens Dimension configuration, defaults to [ActionPickerDefaults.dimens].
 *
 * @see ActionPickerSheetState for state configuration
 * @see ActionPickerSheetEffect for available effects
 * @see ActionPickerDefaults for default values
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionPickerSheet(
    state: ActionPickerSheetState,
    onEffect: (ActionPickerSheetEffect) -> Unit,
    modifier: Modifier = Modifier,
    colors: ActionPickerDefaults.ActionPickerColors = ActionPickerDefaults.colors(),
    dimens: ActionPickerDefaults.ActionPickerDimens = ActionPickerDefaults.dimens(),
) {
    Sheet(
        isVisible = state.isVisible,
        onDismissRequest = { onEffect(ActionPickerSheetEffect.Dismiss) },
        modifier = modifier,
        shape = dimens.shape,
        colors =
            SheetDefaults.colors(
                container = colors.container,
            ),
    ) {
        Column(modifier = Modifier.padding(dimens.contentPadding)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                SheetIconButton(
                    iconType = IconType.CLOSE,
                    onClick = { onEffect(ActionPickerSheetEffect.Dismiss) },
                    modifier = Modifier.align(Alignment.CenterStart),
                )

                Text(
                    text = state.title,
                    color = colors.title,
                    style = System.font.body.large.medium,
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            Spacer(modifier = Modifier.height(dimens.titleItemsSpacing))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(dimens.itemSpacing)) {
                items(state.items) { item ->
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
    colors: ActionPickerDefaults.ActionPickerColors,
    dimens: ActionPickerDefaults.ActionPickerDimens,
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

/**
 * Default values for [ActionPickerSheet].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 */
object ActionPickerDefaults {
    /**
     * Color configuration for [ActionPickerSheet].
     *
     * @param container Background color.
     * @param title Title text color.
     * @param itemBackground Item background color.
     * @param itemIcon Item icon tint.
     * @param itemText Item text color.
     * @param destructiveItemIcon Icon tint for destructive actions.
     */
    data class ActionPickerColors(
        val container: Color,
        val title: Color,
        val itemBackground: Color,
        val itemIcon: Color,
        val itemText: Color,
        val destructiveItemIcon: Color,
    )

    @Composable
    fun colors(
        container: Color = System.color.backgroundBase,
        title: Color = System.color.textBase,
        itemBackground: Color = System.color.backgroundSecondary,
        itemIcon: Color = System.color.iconBase,
        itemText: Color = System.color.textBase,
        destructiveItemIcon: Color = System.color.iconRed,
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
     * Dimension configuration for [ActionPickerSheet].
     *
     * @param shape Sheet corner shape.
     * @param contentPadding Content padding.
     * @param titleItemsSpacing Spacing between title and items.
     * @param itemSpacing Spacing between items.
     * @param itemShape Item button shape.
     * @param itemPadding Item button padding.
     * @param itemIconTextSpacing Spacing between icon and text in item.
     */
    data class ActionPickerDimens(
        val shape: Shape,
        val contentPadding: PaddingValues,
        val titleItemsSpacing: Dp,
        val itemSpacing: Dp,
        val itemShape: Shape,
        val itemPadding: PaddingValues,
        val itemIconTextSpacing: Dp,
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
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
