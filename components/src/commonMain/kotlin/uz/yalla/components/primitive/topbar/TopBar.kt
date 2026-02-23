package uz.yalla.components.primitive.topbar

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.components.primitive.button.NavigationButton
import uz.yalla.components.primitive.button.NavigationButtonState
import uz.yalla.design.theme.System

/**
 * Standard top bar with optional navigation, title, and actions.
 *
 * ## Usage
 *
 * ```kotlin
 * TopBar(
 *     title = "Settings",
 *     onNavigationClick = onBack,
 * )
 * ```
 *
 * ## With Actions
 *
 * ```kotlin
 * TopBar(
 *     title = "Profile",
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
 * @param title Optional title text.
 * @param onNavigationClick If provided, shows back button.
 * @param colors Color configuration, defaults to [TopBarDefaults.colors].
 * @param style Text style configuration, defaults to [TopBarDefaults.style].
 * @param dimens Dimension configuration, defaults to [TopBarDefaults.dimens].
 * @param actions Optional action buttons on the right.
 *
 * @see LargeTopBar for large title variant
 * @see TopBarDefaults for default values
 */
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    onNavigationClick: (() -> Unit)? = null,
    colors: TopBarDefaults.TopBarColors = TopBarDefaults.colors(),
    style: TopBarDefaults.TopBarStyle = TopBarDefaults.style(),
    dimens: TopBarDefaults.TopBarDimens = TopBarDefaults.dimens(),
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
            NavigationButton(
                state = NavigationButtonState(),
                onClick = onNavigationClick
            )
        } else {
            Spacer(Modifier.width(dimens.navigationButtonSize))
        }

        // Title
        if (title != null) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = title,
                    style = style.title,
                    color = colors.title,
                )
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
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object TopBarDefaults {
    /**
     * Color configuration for [TopBar].
     *
     * @param container Background color.
     * @param title Title text color.
     */
    data class TopBarColors(
        val container: Color,
        val title: Color,
    )

    @Composable
    fun colors(
        container: Color = Color.Transparent,
        title: Color = System.color.textBase,
    ) = TopBarColors(
        container = container,
        title = title,
    )

    /**
     * Text style configuration for [TopBar].
     *
     * @param title Title text style.
     */
    data class TopBarStyle(
        val title: TextStyle,
    )

    @Composable
    fun style(title: TextStyle = System.font.body.base.medium) =
        TopBarStyle(
            title = title,
        )

    /**
     * Dimension configuration for [TopBar].
     *
     * @param contentPadding Padding around content.
     * @param navigationButtonSize Size of navigation button placeholder.
     * @param titleSpacing Spacing around title.
     */
    data class TopBarDimens(
        val contentPadding: PaddingValues,
        val navigationButtonSize: Dp,
        val titleSpacing: Dp,
    )

    @Composable
    fun dimens(
        contentPadding: PaddingValues = PaddingValues(16.dp),
        navigationButtonSize: Dp = 40.dp,
        titleSpacing: Dp = 16.dp,
    ) = TopBarDimens(
        contentPadding = contentPadding,
        navigationButtonSize = navigationButtonSize,
        titleSpacing = titleSpacing,
    )
}

@Preview
@Composable
private fun TopBarPreview() {
    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp)
    ) {
        TopBar(
            title = "Settings",
            onNavigationClick = {},
        )
    }
}
