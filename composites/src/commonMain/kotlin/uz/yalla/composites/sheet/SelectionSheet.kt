package uz.yalla.composites.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.platform.sheet.NativeSheet

/**
 * Dimension configuration for [SelectionSheet].
 *
 * @param shape Corner shape for the native sheet container.
 * @param contentPadding Padding around the entire sheet content.
 * @param headerContentSpacing Vertical spacing between the header and the item list.
 * @param itemSpacing Vertical spacing between individual items.
 * @since 0.0.5-alpha12
 */
@Immutable
data class SelectionSheetDimens(
    val shape: Shape,
    val contentPadding: PaddingValues,
    val headerContentSpacing: Dp,
    val itemSpacing: Dp,
)

/**
 * Default values for [SelectionSheet].
 *
 * Provides sensible defaults for [dimens] that can be overridden.
 * @since 0.0.5-alpha12
 */
object SelectionSheetDefaults {

    /**
     * Creates default dimensions.
     *
     * @param shape Sheet corner shape.
     * @param contentPadding Padding around the sheet content.
     * @param headerContentSpacing Spacing between header and item list.
     * @param itemSpacing Spacing between individual items.
     * @since 0.0.5-alpha12
     */
    fun dimens(
        shape: Shape = RoundedCornerShape(topStart = 38.dp, topEnd = 38.dp),
        contentPadding: PaddingValues = PaddingValues(10.dp),
        headerContentSpacing: Dp = 24.dp,
        itemSpacing: Dp = 10.dp,
    ): SelectionSheetDimens =
        SelectionSheetDimens(
            shape = shape,
            contentPadding = contentPadding,
            headerContentSpacing = headerContentSpacing,
            itemSpacing = itemSpacing,
        )
}

/**
 * Generic single-selection sheet using platform-native presentation.
 *
 * Composes [NativeSheet] + [SheetHeader] + [LazyColumn] to display a scrollable
 * list of selectable items. The caller provides [itemContent] to render each item,
 * receiving the item and whether it is currently selected.
 *
 * ## Usage
 *
 * ```kotlin
 * SelectionSheet(
 *     isVisible = state.showLanguagePicker,
 *     onDismissRequest = { viewModel.dismiss() },
 *     title = "Language",
 *     items = state.languages,
 *     selectedItem = state.currentLanguage,
 *     onSelect = { viewModel.selectLanguage(it) },
 *     itemKey = { it.code },
 * ) { language, isSelected ->
 *     SelectableItem(
 *         title = language.name,
 *         isSelected = isSelected,
 *         onClick = { viewModel.selectLanguage(language) },
 *     )
 * }
 * ```
 *
 * @param T Item type. Must implement [equals] for selection comparison.
 * @param isVisible Whether the sheet is visible.
 * @param onDismissRequest Called when the sheet is dismissed.
 * @param title Header title text.
 * @param items List of selectable items.
 * @param selectedItem Currently selected item (compared via [equals]).
 * @param onSelect Called when the user selects an item.
 * @param modifier Applied to the content column inside the sheet.
 * @param dimens Dimension configuration, defaults to [SelectionSheetDefaults.dimens].
 * @param itemKey Optional stable key factory for [LazyColumn] items.
 * @param itemContent Composable rendering each item with its selection state.
 *
 * @see NativeSheet for the platform-native sheet primitive
 * @see SheetHeader for the header component
 * @see SelectionSheetDefaults for default values
 * @since 0.0.5-alpha12
 */
@Composable
fun <T> SelectionSheet(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    items: List<T>,
    selectedItem: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    dimens: SelectionSheetDimens = SelectionSheetDefaults.dimens(),
    itemKey: ((T) -> Any)? = null,
    itemContent: @Composable (item: T, isSelected: Boolean) -> Unit,
) {
    NativeSheet(
        isVisible = isVisible,
        shape = dimens.shape,
        containerColor = System.color.background.base,
        onDismissRequest = onDismissRequest,
    ) {
        Column(modifier.padding(dimens.contentPadding)) {
            SheetHeader(onClose = onDismissRequest, title = title)
            Spacer(Modifier.height(dimens.headerContentSpacing))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(dimens.itemSpacing)) {
                items(items = items, key = itemKey) { item ->
                    itemContent(item, item == selectedItem)
                }
            }
        }
    }
}
