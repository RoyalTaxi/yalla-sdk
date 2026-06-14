package uz.yalla.components.composites.sheet

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import uz.yalla.components.config.composites.ContentSheetHandle
import uz.yalla.components.config.requireConfig
import uz.yalla.design.theme.YallaTheme
import uz.yalla.foundation.theme.rememberIsDarkTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
public actual fun ShellSheet(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier,
    fullHeight: Boolean,
    sheetSwipeEnabled: Boolean,
    content: @Composable (padding: PaddingValues) -> Unit
) {
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)
    val currentContent by rememberUpdatedState(content)
    val handleRef = remember { mutableStateOf<ContentSheetHandle?>(null) }

    val handle = remember {
        val rootController = ComposeUIViewController(configure = { opaque = false }) {
            YallaTheme(isDark = rememberIsDarkTheme()) {
                SheetBody(
                    fullHeight = fullHeight,
                    padding = WindowInsets.safeDrawing.asPaddingValues(),
                    onContentHeightChanged = { height ->
                        handleRef.value?.updateContentHeight(height.value.toDouble())
                    },
                    content = currentContent
                )
            }
        }
        requireConfig().sheet.createShell(
            fullHeight = fullHeight,
            sheetSwipeEnabled = sheetSwipeEnabled,
            contentController = rootController,
            onDismissRequest = { currentOnDismissRequest() }
        ).also { handleRef.value = it }
    }

    PresentationLifecycle(handle = handle, isVisible = isVisible)
}
