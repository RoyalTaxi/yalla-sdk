package uz.yalla.platform.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.UIKit.UIApplication

/**
 * iOS actual for [statusBarHeight].
 *
 * Reads the height from `UIApplication.sharedApplication.statusBarFrame`.
 *
 * Note: This uses the deprecated `statusBarFrame` property. On iOS 13+ with scene-based
 * lifecycle, the frame may return `.zero` in certain configurations. Prefer
 * [ToolbarState.contentPadding][uz.yalla.platform.navigation.ToolbarState.contentPadding]
 * for screens inside [NativeNavHost].
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun statusBarHeight(): Dp =
    UIApplication.sharedApplication.statusBarFrame.useContents { size.height.dp }
