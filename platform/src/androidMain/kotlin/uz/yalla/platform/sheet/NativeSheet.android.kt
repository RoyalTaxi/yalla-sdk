package uz.yalla.platform.sheet

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.union
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

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
    onFullyExpanded: (() -> Unit)?,
    content: @Composable () -> Unit
) {
    val darkMode = isDark ?: isSystemInDarkTheme()
    val properties =
        ModalBottomSheetProperties(
            isAppearanceLightStatusBars = !darkMode,
            isAppearanceLightNavigationBars = !darkMode,
        )

    var shouldShow by remember { mutableStateOf(false) }
    val currentDismissEnabled by rememberUpdatedState(dismissEnabled)
    val currentIsVisible by rememberUpdatedState(isVisible)
    val currentOnDismissAttempt by rememberUpdatedState(onDismissAttempt)
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)

    val sheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
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
            shape = shape,
            containerColor = containerColor,
            dragHandle = null,
            properties = properties,
            contentWindowInsets = { WindowInsets.ime.union(WindowInsets.navigationBars) },
            onDismissRequest = {
                if (currentDismissEnabled) {
                    shouldShow = false
                    currentOnDismissRequest()
                } else {
                    currentOnDismissAttempt()
                }
            },
            content = { content() }
        )
    }
}
