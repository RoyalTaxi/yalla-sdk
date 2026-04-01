package uz.yalla.platform.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import uz.yalla.design.theme.System

/**
 * Android actual for [NativeSheet].
 *
 * Uses Material3 [ModalBottomSheet] with a transparent container and custom background
 * shape applied via [Modifier.background]. The sheet respects keyboard insets
 * (`WindowInsets.ime`) and manages a two-phase show/hide lifecycle:
 * 1. `shouldShow = true` triggers composition of the [ModalBottomSheet].
 * 2. `isVisible = false` animates hide via [rememberModalBottomSheetState], then removes
 *    the sheet from composition.
 *
 * The `skipPartiallyExpanded` parameter is forwarded directly to the sheet state;
 * `onFullyExpanded` fires when [SheetValue.Expanded] is both current and target.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun NativeSheet(
    isVisible: Boolean,
    shape: Shape,
    containerColor: Color,
    onDismissRequest: () -> Unit,
    dismissEnabled: Boolean,
    onDismissAttempt: () -> Unit,
    isDark: Boolean?,
    skipPartiallyExpanded: Boolean,
    onFullyExpanded: (() -> Unit)?,
    content: @Composable () -> Unit
) {
    val darkMode = isDark ?: System.isDark
    val properties =
        ModalBottomSheetProperties(
            isAppearanceLightStatusBars = !darkMode,
            isAppearanceLightNavigationBars = !darkMode,
        )

    val density = LocalDensity.current
    val statusBarTopDp = with(density) {
        WindowInsets.statusBars.getTop(this).toDp()
    }

    var shouldShow by remember { mutableStateOf(false) }
    val currentDismissEnabled by rememberUpdatedState(dismissEnabled)
    val currentIsVisible by rememberUpdatedState(isVisible)
    val currentOnDismissAttempt by rememberUpdatedState(onDismissAttempt)
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)

    val sheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = skipPartiallyExpanded,
            confirmValueChange = { value ->
                val isHiding = value == SheetValue.Hidden
                if (!currentDismissEnabled && currentIsVisible && isHiding) {
                    currentOnDismissAttempt()
                    false
                } else {
                    true
                }
            },
        )

    LaunchedEffect(isVisible) {
        if (isVisible) {
            shouldShow = true
        } else if (shouldShow) {
            try {
                sheetState.hide()
            } finally {
                shouldShow = false
            }
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
            sheetState = sheetState,
            shape = RectangleShape,
            containerColor = Color.Transparent,
            dragHandle = null,
            properties = properties,
            contentWindowInsets = { WindowInsets.ime },
            onDismissRequest = {
                if (currentDismissEnabled) {
                    shouldShow = false
                    currentOnDismissRequest()
                } else {
                    currentOnDismissAttempt()
                }
            },
            content = {
                Column(
                    modifier = Modifier
                        .padding(top = statusBarTopDp)
                        .background(color = containerColor, shape = shape)
                        .navigationBarsPadding(),
                ) {
                    content()
                }
            },
        )
    }
}
