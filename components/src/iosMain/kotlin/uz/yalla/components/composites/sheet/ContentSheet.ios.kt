package uz.yalla.components.composites.sheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import uz.yalla.components.config.composites.ContentSheetHandle
import uz.yalla.components.config.requireConfig
import uz.yalla.components.platform.findKeyWindowRootController
import uz.yalla.components.primitives.button.CloseButton
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme

private object SheetMetrics {
    val HeaderHeight = 72.dp
    val HeaderPadding = 16.dp
    val HeaderTitleSideReserve = 60.dp
    val CornerRadius = 38.dp
    val FooterShadowElevation = 6.dp
    val FooterHorizontalPadding = 20.dp
    val FooterTopPadding = 20.dp
    val FooterBottomPadding = 8.dp
    val TopChromeReserve = 21.dp
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun ContentSheet(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier,
    title: String?,
    onClose: (() -> Unit)?,
    fullHeight: Boolean,
    sheetSwipeEnabled: Boolean,
    footer: (@Composable () -> Unit)?,
    content: @Composable (padding: PaddingValues) -> Unit
) {
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)
    val currentOnClose by rememberUpdatedState(onClose)
    val currentContent by rememberUpdatedState(content)
    val currentFooter by rememberUpdatedState(footer)
    val hasHeader = title != null || onClose != null
    val hasFooter = footer != null
    val parentIsDark = System.isDark
    val currentIsDark by rememberUpdatedState(parentIsDark)
    val handleRef = remember { mutableStateOf<ContentSheetHandle?>(null) }

    val handle = remember {
        val rootController = ComposeUIViewController(configure = { opaque = false }) {
            YallaTheme(isDark = currentIsDark) {
                ContentSheetScaffold(
                    title = title,
                    onClose = currentOnClose,
                    hasHeader = hasHeader,
                    hasFooter = hasFooter,
                    fullHeight = fullHeight,
                    onContentHeightChanged = { height ->
                        handleRef.value?.updateContentHeight(height.value.toDouble())
                    },
                    content = currentContent,
                    footer = currentFooter
                )
            }
        }
        requireConfig().sheet.createContent(
            fullHeight = fullHeight,
            sheetSwipeEnabled = sheetSwipeEnabled,
            contentController = rootController,
            onDismissRequest = { currentOnDismissRequest() }
        ).also { handleRef.value = it }
    }

    PresentationLifecycle(handle = handle, isVisible = isVisible)
}

@Composable
private fun ContentSheetScaffold(
    title: String?,
    onClose: (() -> Unit)?,
    hasHeader: Boolean,
    hasFooter: Boolean,
    fullHeight: Boolean,
    onContentHeightChanged: (Dp) -> Unit,
    content: @Composable (padding: PaddingValues) -> Unit,
    footer: (@Composable () -> Unit)?
) {
    var footerHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val safeAreaBottom = WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding()

    val contentPadding = PaddingValues(
        top = statusBarTop + SheetMetrics.TopChromeReserve +
            (if (hasHeader) SheetMetrics.HeaderHeight else 0.dp),
        bottom = if (hasFooter) footerHeight else safeAreaBottom
    )

    val outerModifier = if (fullHeight) {
        Modifier.fillMaxSize()
    } else {
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .onSizeChanged {
                // The Compose Box wraps content INCLUDING our safeAreaBottom padding, but
                // the iOS custom detent expects a value that EXCLUDES the bottom safe area
                // (UIKit adds it itself). Subtract here so UIKit doesn't double-count.
                val raw = with(density) { it.height.toDp() }
                val reported = (raw - safeAreaBottom).coerceAtLeast(0.dp)
                onContentHeightChanged(reported)
            }
    }

    Box(modifier = outerModifier) {
        content(contentPadding)
        if (hasHeader) {
            SheetHeaderSurface(
                modifier = Modifier.align(Alignment.TopCenter),
                statusBarTop = statusBarTop,
                title = title,
                onClose = onClose
            )
        }
        if (hasFooter && footer != null) {
            SheetFooterSurface(
                modifier = Modifier.align(Alignment.BottomCenter),
                onMeasured = { measured -> if (measured != footerHeight) footerHeight = measured },
                density = density,
                content = footer
            )
        }
    }
}

@Composable
private fun SheetHeaderSurface(
    modifier: Modifier,
    statusBarTop: Dp,
    title: String?,
    onClose: (() -> Unit)?
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = System.color.background.base
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = statusBarTop)
                .padding(SheetMetrics.HeaderPadding)
        ) {
            if (onClose != null) {
                CloseButton(
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
            if (title != null) {
                Text(
                    text = title,
                    color = System.color.text.base,
                    style = System.font.body.large.medium,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = SheetMetrics.HeaderTitleSideReserve)
                )
            }
        }
    }
}

@Composable
private fun SheetFooterSurface(
    modifier: Modifier,
    onMeasured: (Dp) -> Unit,
    density: Density,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged { onMeasured(with(density) { it.height.toDp() }) },
        shape = RoundedCornerShape(
            topStart = SheetMetrics.CornerRadius,
            topEnd = SheetMetrics.CornerRadius
        ),
        color = System.color.background.base,
        shadowElevation = SheetMetrics.FooterShadowElevation
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
                .padding(
                    start = SheetMetrics.FooterHorizontalPadding,
                    end = SheetMetrics.FooterHorizontalPadding,
                    top = SheetMetrics.FooterTopPadding,
                    bottom = SheetMetrics.FooterBottomPadding
                )
        ) {
            content()
        }
    }
}

@Composable
private fun PresentationLifecycle(
    handle: ContentSheetHandle,
    isVisible: Boolean
) {
    var isPresented by remember { mutableStateOf(false) }

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

    DisposableEffect(Unit) {
        onDispose {
            if (isPresented) {
                isPresented = false
                handle.dismiss()
            }
        }
    }
}
