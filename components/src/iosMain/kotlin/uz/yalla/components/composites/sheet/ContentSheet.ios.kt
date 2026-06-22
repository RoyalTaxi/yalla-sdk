package uz.yalla.components.composites.sheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.coroutines.delay
import uz.yalla.components.config.composites.ContentSheetHandle
import uz.yalla.components.config.requireConfig
import uz.yalla.components.platform.findKeyWindowRootController
import uz.yalla.design.theme.YallaTheme
import uz.yalla.foundation.theme.rememberIsDarkTheme

private const val PRESENTATION_POLL_MILLIS = 16L

@OptIn(ExperimentalComposeUiApi::class)
@Composable
public actual fun ContentSheet(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier,
    title: String?,
    onClose: (() -> Unit)?,
    fullHeight: Boolean,
    sheetSwipeEnabled: Boolean,
    onFullyExpanded: (() -> Unit)?,
    content: @Composable (padding: PaddingValues) -> Unit
) {
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)
    val currentOnClose by rememberUpdatedState(onClose)
    val currentContent by rememberUpdatedState(content)
    val handleRef = remember { mutableStateOf<ContentSheetHandle?>(null) }

    val handle =
        remember {
            val rootController =
                ComposeUIViewController(configure = { opaque = false }) {
                    YallaTheme(isDark = rememberIsDarkTheme()) {
                        SheetBody(
                            fullHeight = fullHeight,
                            padding =
                                PaddingValues(
                                    bottom = WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding()
                                ),
                            onContentHeightChanged = { height ->
                                handleRef.value?.updateContentHeight(height.value.toDouble())
                            },
                            content = currentContent
                        )
                    }
                }
            requireConfig()
                .sheet
                .createContent(
                    fullHeight = fullHeight,
                    sheetSwipeEnabled = sheetSwipeEnabled,
                    title = title,
                    showClose = onClose != null,
                    contentController = rootController,
                    onClose = if (onClose != null) ({ currentOnClose?.invoke() }) else null,
                    onDismissRequest = { currentOnDismissRequest() }
                ).also { handleRef.value = it }
        }

    PresentationLifecycle(handle = handle, isVisible = isVisible, onFullyExpanded = onFullyExpanded)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun SheetBody(
    fullHeight: Boolean,
    padding: PaddingValues,
    onContentHeightChanged: (Dp) -> Unit,
    content: @Composable (padding: PaddingValues) -> Unit
) {
    val density = LocalDensity.current
    val safeAreaBottom = WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding()

    val outerModifier =
        if (fullHeight) {
            Modifier.fillMaxSize()
        } else {
            Modifier
                .fillMaxWidth()
                .wrapContentHeight(align = Alignment.Top, unbounded = true)
                .onSizeChanged {
                    val raw = with(density) { it.height.toDp() }
                    onContentHeightChanged((raw - safeAreaBottom).coerceAtLeast(0.dp))
                }
        }

    Box(modifier = outerModifier) {
        content(padding)
    }
}

@Composable
internal fun PresentationLifecycle(
    handle: ContentSheetHandle,
    isVisible: Boolean,
    onFullyExpanded: (() -> Unit)? = null
) {
    var isPresented by remember { mutableStateOf(false) }
    val currentOnFullyExpanded by rememberUpdatedState(onFullyExpanded)

    LaunchedEffect(isVisible, isPresented) {
        if (isVisible && !isPresented) {
            val parent = findKeyWindowRootController() ?: return@LaunchedEffect
            isPresented = true
            handle.present(parent)
        } else if (!isVisible && isPresented) {
            isPresented = false
            handle.dismiss()
        }
    }

    LaunchedEffect(isPresented) {
        if (!isPresented) return@LaunchedEffect
        val onPresented = currentOnFullyExpanded ?: return@LaunchedEffect
        while (handle.viewController.isBeingPresented()) {
            delay(PRESENTATION_POLL_MILLIS)
        }
        onPresented()
    }

    DisposableEffect(Unit) {
        onDispose {
            if (isPresented) {
                isPresented = false
                handle.dismiss()
            }
        }
    }
}
