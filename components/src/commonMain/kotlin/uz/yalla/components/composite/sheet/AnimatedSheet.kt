package uz.yalla.components.composite.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import uz.yalla.design.theme.System

/**
 * Default configuration values for [AnimatedSheet].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 */
object AnimatedSheetDefaults {
    /**
     * Color configuration for [AnimatedSheet].
     *
     * @param container Background color of the sheet.
     * @param content Content color.
     * @param scrim Scrim color behind the sheet.
     * @param dragHandle Color of the drag handle.
     */
    data class AnimatedSheetColors(
        val container: Color,
        val content: Color,
        val scrim: Color,
        val dragHandle: Color
    )

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun colors(
        container: Color = BottomSheetDefaults.ContainerColor,
        content: Color = contentColorFor(container),
        scrim: Color = BottomSheetDefaults.ScrimColor,
        dragHandle: Color = System.color.backgroundTertiary
    ) = AnimatedSheetColors(
        container = container,
        content = content,
        scrim = scrim,
        dragHandle = dragHandle
    )

    /**
     * Dimension configuration for [AnimatedSheet].
     *
     * @param shape Shape of the sheet.
     * @param maxWidth Maximum width of the sheet.
     * @param tonalElevation Tonal elevation.
     * @param dragHandleWidth Width of the drag handle.
     * @param dragHandleHeight Height of the drag handle.
     * @param dragHandleContainerHeight Height of the drag handle container.
     */
    data class AnimatedSheetDimens(
        val shape: Shape,
        val maxWidth: Dp,
        val tonalElevation: Dp,
        val dragHandleWidth: Dp,
        val dragHandleHeight: Dp,
        val dragHandleContainerHeight: Dp
    )

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun dimens(
        shape: Shape = BottomSheetDefaults.ExpandedShape,
        maxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,
        tonalElevation: Dp = 0.dp,
        dragHandleWidth: Dp = 36.dp,
        dragHandleHeight: Dp = 5.dp,
        dragHandleContainerHeight: Dp = 16.dp
    ) = AnimatedSheetDimens(
        shape = shape,
        maxWidth = maxWidth,
        tonalElevation = tonalElevation,
        dragHandleWidth = dragHandleWidth,
        dragHandleHeight = dragHandleHeight,
        dragHandleContainerHeight = dragHandleContainerHeight
    )
}

/**
 * Modal bottom sheet with visibility animation.
 *
 * ## Usage
 *
 * ```kotlin
 * var showSheet by remember { mutableStateOf(false) }
 *
 * AnimatedSheet(
 *     isVisible = showSheet,
 *     onDismissRequest = { showSheet = false }
 * ) {
 *     // Sheet content
 * }
 * ```
 *
 * @param isVisible Whether sheet should be visible.
 * @param onDismissRequest Called when sheet should be dismissed.
 * @param modifier Applied to sheet.
 * @param sheetState Sheet state for controlling expansion.
 * @param colors Color configuration, defaults to [AnimatedSheetDefaults.colors].
 * @param dimens Dimension configuration, defaults to [AnimatedSheetDefaults.dimens].
 * @param contentWindowInsets Window insets for content.
 * @param properties Sheet properties.
 * @param dragHandle Drag handle composable.
 * @param onFullyExpanded Called when sheet is fully expanded.
 * @param content Sheet content.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedSheet(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(true),
    colors: AnimatedSheetDefaults.AnimatedSheetColors = AnimatedSheetDefaults.colors(),
    dimens: AnimatedSheetDefaults.AnimatedSheetDimens = AnimatedSheetDefaults.dimens(),
    contentWindowInsets: @Composable () -> WindowInsets = { WindowInsets.ime.union(WindowInsets.navigationBars) },
    properties: ModalBottomSheetProperties? = null,
    dragHandle: @Composable (() -> Unit)? = {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(width = dimens.dragHandleWidth, height = dimens.dragHandleContainerHeight)
        ) {
            Box(
                modifier =
                    Modifier
                        .background(shape = CircleShape, color = colors.dragHandle)
                        .size(width = dimens.dragHandleWidth, height = dimens.dragHandleHeight)
            )
        }
    },
    onFullyExpanded: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val resolvedProperties = properties ?: ModalBottomSheetDefaults.properties

    var shouldShow by remember { mutableStateOf(false) }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            shouldShow = true
        } else if (shouldShow) {
            sheetState.hide()
            shouldShow = false
        }
    }

    LaunchedEffect(sheetState, onFullyExpanded) {
        if (onFullyExpanded == null) return@LaunchedEffect
        snapshotFlow { sheetState.currentValue to sheetState.targetValue }
            .distinctUntilChanged()
            .filter { (current, target) -> current == SheetValue.Expanded && current == target }
            .collect { onFullyExpanded() }
    }

    if (shouldShow) {
        ModalBottomSheet(
            onDismissRequest = {
                shouldShow = false
                onDismissRequest()
            },
            modifier = modifier,
            sheetState = sheetState,
            sheetMaxWidth = dimens.maxWidth,
            shape = dimens.shape,
            containerColor = colors.container,
            contentColor = colors.content,
            tonalElevation = dimens.tonalElevation,
            scrimColor = colors.scrim,
            dragHandle = dragHandle,
            contentWindowInsets = contentWindowInsets,
            properties = resolvedProperties,
            content = content
        )
    }
}
