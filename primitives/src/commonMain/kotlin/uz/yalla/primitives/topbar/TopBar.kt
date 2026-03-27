package uz.yalla.primitives.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.primitives.button.NavigationButton

/**
 * Color configuration for [TopBar].
 *
 * @param container Background color.
 * @param title Title text color.
 * @since 0.0.1
 */
@Immutable
data class TopBarColors(
    val container: Color,
    val title: Color,
)

/**
 * Dimension configuration for [TopBar].
 *
 * @param contentPadding Padding around content.
 * @param navigationButtonSize Size of navigation button placeholder.
 * @since 0.0.1
 */
@Immutable
data class TopBarDimens(
    val contentPadding: PaddingValues,
    val navigationButtonSize: Dp,
)

/**
 * Standard top bar with optional navigation, title slot, and actions.
 *
 * ## Usage
 *
 * ```kotlin
 * TopBar(
 *     title = {
 *         Text(
 *             text = "Settings",
 *             style = System.font.body.base.medium,
 *             color = System.color.text.base,
 *         )
 *     },
 *     onNavigationClick = onBack,
 * )
 * ```
 *
 * ## With Actions
 *
 * ```kotlin
 * TopBar(
 *     title = {
 *         Text(
 *             text = "Profile",
 *             style = System.font.body.base.medium,
 *             color = System.color.text.base,
 *         )
 *     },
 *     onNavigationClick = onBack,
 *     actions = {
 *         IconButton(onClick = onEdit) {
 *             Icon(Icons.Default.Edit, null)
 *         }
 *     }
 * )
 * ```
 *
 * @param modifier Applied to top bar.
 * @param title Optional title slot.
 * @param onNavigationClick If provided, shows back button.
 * @param colors Color configuration, defaults to [TopBarDefaults.colors].
 * @param dimens Dimension configuration, defaults to [TopBarDefaults.dimens].
 * @param actions Optional action buttons on the right.
 *
 * @see LargeTopBar for large title variant
 * @see TopBarDefaults for default values
 * @since 0.0.1
 */
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)? = null,
    onNavigationClick: (() -> Unit)? = null,
    colors: TopBarColors = TopBarDefaults.colors(),
    dimens: TopBarDimens = TopBarDefaults.dimens(),
    actions: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(dimens.contentPadding)
                .height(dimens.navigationButtonSize),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Navigation button
        if (onNavigationClick != null) {
            NavigationButton(onClick = onNavigationClick)
        } else {
            Spacer(Modifier.width(dimens.navigationButtonSize))
        }

        // Title
        if (title != null) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                title()
            }
        } else {
            Spacer(Modifier.weight(1f))
        }

        // Actions
        Row(
            modifier = Modifier.height(dimens.navigationButtonSize),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            actions?.invoke()
        }
    }
}

/**
 * Default configuration values for [TopBar].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 * @since 0.0.1
 */
object TopBarDefaults {
    /** Creates color configuration for [TopBar]. */
    @Composable
    fun colors(
        container: Color = Color.Transparent,
        title: Color = System.color.text.base,
    ) = TopBarColors(
        container = container,
        title = title,
    )

    /** Creates dimension configuration for [TopBar]. */
    fun dimens(
        contentPadding: PaddingValues = PaddingValues(16.dp),
        navigationButtonSize: Dp = 40.dp,
    ) = TopBarDimens(
        contentPadding = contentPadding,
        navigationButtonSize = navigationButtonSize,
    )
}

@Preview
@Composable
private fun TopBarPreview() {
    YallaTheme {
        Box(
            modifier =
                Modifier
                    .background(Color.White)
                    .padding(16.dp)
        ) {
            TopBar(
                title = {
                    Text(
                        text = "Settings",
                        style = System.font.body.base.medium,
                        color = System.color.text.base,
                    )
                },
                onNavigationClick = {},
            )
        }
    }
}
