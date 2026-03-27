package uz.yalla.composites.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.Immutable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme

/**
 * Color configuration for [Sheet].
 *
 * @param container Background color.
 * @param scrim Scrim overlay color.
 * @since 0.0.5-alpha12
 */
@Immutable
data class SheetColors(
    val container: Color,
    val scrim: Color,
)

/**
 * Dimension configuration for [Sheet].
 *
 * @param shape Sheet corner shape.
 * @param maxWidth Maximum sheet width.
 * @param dragHandleWidth Drag handle width.
 * @param dragHandleHeight Drag handle height.
 * @param dragHandleContainerWidth Drag handle container width.
 * @param dragHandleContainerHeight Drag handle container height.
 * @since 0.0.5-alpha12
 */
@Immutable
data class SheetDimens(
    val shape: Shape,
    val maxWidth: Dp,
    val dragHandleWidth: Dp,
    val dragHandleHeight: Dp,
    val dragHandleContainerWidth: Dp,
    val dragHandleContainerHeight: Dp,
)

/**
 * Default values for [Sheet].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 * @since 0.0.1
 */
@OptIn(ExperimentalMaterial3Api::class)
object SheetDefaults {
    /**
     * Creates theme-aware default colors.
     *
     * @since 0.0.1
     */
    @Composable
    fun colors(
        container: Color = System.color.background.base,
        scrim: Color = BottomSheetDefaults.ScrimColor,
    ): SheetColors =
        SheetColors(
            container = container,
            scrim = scrim,
        )

    /**
     * Creates default dimensions.
     *
     * @since 0.0.5-alpha12
     */
    fun dimens(
        shape: Shape = RoundedCornerShape(topStart = 38.dp, topEnd = 38.dp),
        maxWidth: Dp = Dp.Unspecified,
        dragHandleWidth: Dp = 36.dp,
        dragHandleHeight: Dp = 5.dp,
        dragHandleContainerWidth: Dp = 36.dp,
        dragHandleContainerHeight: Dp = 16.dp,
    ): SheetDimens =
        SheetDimens(
            shape = shape,
            maxWidth = maxWidth,
            dragHandleWidth = dragHandleWidth,
            dragHandleHeight = dragHandleHeight,
            dragHandleContainerWidth = dragHandleContainerWidth,
            dragHandleContainerHeight = dragHandleContainerHeight,
        )
}

/**
 * Animated modal bottom sheet wrapper.
 *
 * Handles visibility animation and provides callbacks for sheet state.
 *
 * ## Usage
 *
 * ```kotlin
 * Sheet(
 *     isVisible = showSheet,
 *     onDismissRequest = { showSheet = false },
 * ) {
 *     Column(modifier = Modifier.padding(16.dp)) {
 *         Text("Sheet content")
 *     }
 * }
 * ```
 *
 * @param isVisible Whether sheet is visible.
 * @param onDismissRequest Called when sheet is dismissed.
 * @param modifier Applied to sheet.
 * @param sheetState State for sheet behavior.
 * @param colors Color configuration, defaults to [SheetDefaults.colors].
 * @param dimens Dimension configuration, defaults to [SheetDefaults.dimens].
 * @param dragHandle Optional drag handle composable.
 * @param contentWindowInsets Window insets for content.
 * @param properties Sheet properties.
 * @param snackbarHost Optional snackbar host rendered as a Popup overlay inside the sheet.
 * @param onFullyExpanded Called when sheet fully expands.
 * @param content Sheet content.
 *
 * @see SheetDefaults for default values
 * @since 0.0.1
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Sheet(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    colors: SheetColors = SheetDefaults.colors(),
    dimens: SheetDimens = SheetDefaults.dimens(),
    dragHandle: @Composable (() -> Unit)? = { SheetDragHandle() },
    contentWindowInsets: @Composable () -> WindowInsets = { WindowInsets.ime.union(WindowInsets.navigationBars) },
    properties: ModalBottomSheetProperties = ModalBottomSheetDefaults.properties,
    snackbarHost: @Composable (() -> Unit)? = null,
    onFullyExpanded: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
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
            contentColor = contentColorFor(colors.container),
            tonalElevation = 0.dp,
            scrimColor = colors.scrim,
            dragHandle = dragHandle,
            contentWindowInsets = contentWindowInsets,
            properties = properties,
            content = {
                Box {
                    Column { content() }
                    snackbarHost?.let { host ->
                        Box(
                            modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)
                        ) { host() }
                    }
                }
            },
        )
    }
}

/**
 * Standard drag handle for sheets.
 *
 * @param modifier Applied to drag handle.
 * @param dimens Dimension configuration.
 * @param color Handle color.
 * @since 0.0.1
 */
@Composable
fun SheetDragHandle(
    modifier: Modifier = Modifier,
    dimens: SheetDimens = SheetDefaults.dimens(),
    color: Color = System.color.background.tertiary,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier.size(
                width = dimens.dragHandleContainerWidth,
                height = dimens.dragHandleContainerHeight,
            ),
    ) {
        Box(
            modifier =
                Modifier
                    .background(shape = CircleShape, color = color)
                    .size(
                        width = dimens.dragHandleWidth,
                        height = dimens.dragHandleHeight,
                    ),
        )
    }
}

@Preview
@Composable
private fun SheetDragHandlePreview() {
    YallaTheme {
        Box(
            modifier =
                Modifier
                    .background(Color.White)
                    .padding(16.dp)
        ) {
            SheetDragHandle()
        }
    }
}
