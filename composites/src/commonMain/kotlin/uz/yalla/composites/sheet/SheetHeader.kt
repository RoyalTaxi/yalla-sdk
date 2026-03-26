package uz.yalla.composites.sheet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.platform.button.SheetIconButton
import uz.yalla.platform.model.IconType

/**
 * Color configuration for [SheetHeader].
 *
 * @param title Title text color.
 * @since 0.0.5-alpha12
 */
@Immutable
data class SheetHeaderColors(
    val title: Color,
)

/**
 * Dimension configuration for [SheetHeader].
 *
 * @param contentPadding Padding around header content.
 * @since 0.0.5-alpha12
 */
@Immutable
data class SheetHeaderDimens(
    val contentPadding: PaddingValues,
)

/**
 * Default values for [SheetHeader].
 *
 * @since 0.0.5-alpha12
 */
object SheetHeaderDefaults {
    /** Creates theme-aware default colors. */
    @Composable
    fun colors(
        title: Color = System.color.text.base,
    ): SheetHeaderColors =
        SheetHeaderColors(title = title)

    /** Creates default dimensions. */
    fun dimens(
        contentPadding: PaddingValues = PaddingValues(10.dp),
    ): SheetHeaderDimens =
        SheetHeaderDimens(contentPadding = contentPadding)
}

/**
 * Standard header for bottom sheets with close button and optional title.
 *
 * Replaces the manual `Box { SheetIconButton(CLOSE) + Text(title) }` pattern
 * used in 13+ sheet implementations.
 *
 * ## Usage
 *
 * ```kotlin
 * NativeSheet(...) {
 *     Column {
 *         SheetHeader(onClose = { dismiss() }, title = "Settings")
 *         // content...
 *     }
 * }
 * ```
 *
 * ## With Actions
 *
 * ```kotlin
 * SheetHeader(
 *     onClose = { dismiss() },
 *     title = "Edit",
 *     actions = {
 *         SheetIconButton(iconType = IconType.DONE, onClick = { save() })
 *     },
 * )
 * ```
 *
 * @param onClose Called when close button is tapped.
 * @param modifier Applied to header.
 * @param title Optional centered title text.
 * @param colors Color configuration, defaults to [SheetHeaderDefaults.colors].
 * @param dimens Dimension configuration, defaults to [SheetHeaderDefaults.dimens].
 * @param closeButtonBorder Optional border for close button.
 * @param actions Optional trailing actions (e.g., done button).
 *
 * @see SheetHeaderDefaults for default values
 * @since 0.0.5-alpha12
 */
@Composable
fun SheetHeader(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    colors: SheetHeaderColors = SheetHeaderDefaults.colors(),
    dimens: SheetHeaderDimens = SheetHeaderDefaults.dimens(),
    closeButtonBorder: BorderStroke? = null,
    actions: @Composable (() -> Unit)? = null,
) {
    Box(
        modifier = modifier.fillMaxWidth().padding(dimens.contentPadding),
    ) {
        SheetIconButton(
            iconType = IconType.CLOSE,
            onClick = onClose,
            modifier = Modifier.align(Alignment.CenterStart),
            border = closeButtonBorder,
        )

        title?.let {
            Text(
                text = it,
                style = System.font.body.large.medium,
                color = colors.title,
                modifier = Modifier.align(Alignment.Center),
            )
        }

        actions?.let {
            Box(modifier = Modifier.align(Alignment.CenterEnd)) { it() }
        }
    }
}
