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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.platform.sheet.NativeSheet

/**
 * Color configuration for [SelectionSheet].
 */
@Immutable
data class SelectionSheetColors(
    val containerColor: Color
)

/**
 * Dimension configuration for [SelectionSheet].
 */
@Immutable
data class SelectionSheetDimens(
    val shape: Shape,
    val contentPadding: PaddingValues,
    val headerContentSpacing: Dp,
    val itemSpacing: Dp
)

/**
 * Default values for [SelectionSheet].
 *
 * Provides sensible defaults for [colors] and [dimens] that can be overridden.
 */
object SelectionSheetDefaults {
    @Composable
    fun colors(containerColor: Color = System.color.background.base): SelectionSheetColors =
        SelectionSheetColors(
            containerColor = containerColor
        )

    fun dimens(
        shape: Shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
        headerContentSpacing: Dp = 24.dp,
        itemSpacing: Dp = 8.dp
    ): SelectionSheetDimens =
        SelectionSheetDimens(
            shape = shape,
            contentPadding = contentPadding,
            headerContentSpacing = headerContentSpacing,
            itemSpacing = itemSpacing
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
 *         title = { Text(language.name) },
 *         isSelected = isSelected,
 *         onSelect = { viewModel.selectLanguage(language) },
 *     )
 * }
 * ```
 *
 * @param T Item type. Must implement [equals] for selection comparison.
 * @param colors Color configuration, defaults to [SelectionSheetDefaults.colors].
 * @param dimens Dimension configuration, defaults to [SelectionSheetDefaults.dimens].
 *
 * @see NativeSheet for the platform-native sheet primitive
 * @see SheetHeader for the header component
 * @see SelectionSheetDefaults for default values
 */
@Composable
@Suppress("UnusedParameter") // onSelect callback is exposed for callers that wire it through itemContent
fun <T> SelectionSheet(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    items: List<T>,
    selectedItem: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    colors: SelectionSheetColors = SelectionSheetDefaults.colors(),
    dimens: SelectionSheetDimens = SelectionSheetDefaults.dimens(),
    itemKey: ((T) -> Any)? = null,
    itemContent: @Composable (item: T, isSelected: Boolean) -> Unit
) {
    NativeSheet(
        isVisible = isVisible,
        shape = dimens.shape,
        containerColor = colors.containerColor,
        onDismissRequest = onDismissRequest
    ) {
        Column(modifier) {
            SheetHeader(onClose = onDismissRequest, title = title)
            Spacer(Modifier.height(dimens.headerContentSpacing))
            LazyColumn(
                modifier = Modifier.padding(dimens.contentPadding),
                verticalArrangement = Arrangement.spacedBy(dimens.itemSpacing)
            ) {
                items(items = items, key = itemKey) { item ->
                    itemContent(item, item == selectedItem)
                }
            }
        }
    }
}
