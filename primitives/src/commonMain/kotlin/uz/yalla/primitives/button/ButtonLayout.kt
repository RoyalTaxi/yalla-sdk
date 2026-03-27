package uz.yalla.primitives.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.platform.indicator.NativeLoadingIndicator

/**
 * Shared button layout infrastructure used by all button variants.
 *
 * Implements Container → Provider → Layout → Content pattern:
 * - [Surface] handles click, shape, color, and accessibility semantics
 * - [CompositionLocalProvider] establishes content color for all children
 * - [Row] arranges icons and content with center alignment
 * - Loading state replaces content with platform-native spinner
 *
 * This is internal API — not exposed to SDK consumers.
 *
 * @param onClick Called when the button is clicked.
 * @param modifier Applied to the root Surface.
 * @param enabled Whether the button accepts user input.
 * @param loading When true, replaces content with loading indicator.
 * @param shape Container shape (rounded corners, etc.).
 * @param containerColor Background color.
 * @param contentColor Foreground color inherited by text and icons.
 * @param contentPadding Internal padding between container edge and content.
 * @param minHeight Minimum button height for touch target compliance.
 * @param iconSize Size constraint applied to leading/trailing icon containers.
 * @param iconSpacing Horizontal space between icons and main content.
 * @param leadingIcon Optional composable rendered before main content.
 * @param trailingIcon Optional composable rendered after main content.
 * @param content Main button content, typically Text.
 */
@Composable
internal fun ButtonLayout(
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    loading: Boolean,
    shape: Shape,
    containerColor: Color,
    contentColor: Color,
    contentPadding: PaddingValues,
    minHeight: Dp,
    iconSize: Dp = 20.dp,
    iconSpacing: Dp = 8.dp,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .defaultMinSize(minHeight = minHeight)
            .semantics { role = Role.Button },
        enabled = enabled && !loading,
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Row(
                modifier = Modifier.padding(contentPadding),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (loading) {
                    NativeLoadingIndicator(
                        modifier = Modifier.size(iconSize),
                        color = contentColor,
                        backgroundColor = containerColor,
                    )
                } else {
                    leadingIcon?.let { icon ->
                        Box(
                            modifier = Modifier.size(iconSize),
                            contentAlignment = Alignment.Center,
                            content = { icon() }
                        )

                        Spacer(Modifier.width(iconSpacing))
                    }

                    content()

                    trailingIcon?.let { icon ->
                        Spacer(Modifier.width(iconSpacing))

                        Box(
                            modifier = Modifier.size(iconSize),
                            contentAlignment = Alignment.Center,
                            content = { icon() }
                        )
                    }
                }
            }
        }
    }
}
