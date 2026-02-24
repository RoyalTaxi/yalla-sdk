package uz.yalla.primitives.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import uz.yalla.design.theme.System
import uz.yalla.primitives.button.NavigationButton
import uz.yalla.primitives.button.NavigationButtonState

/**
 * Large top bar with prominent title below navigation row.
 *
 * Use for screens where the title is the primary focus.
 *
 * ## Usage
 *
 * ```kotlin
 * LargeTopBar(
 *     title = "Order History",
 *     onNavigationClick = onBack,
 * )
 * ```
 *
 * @param title Screen title.
 * @param modifier Applied to top bar.
 * @param onNavigationClick If provided, shows back button.
 * @param colors Color configuration, defaults to [LargeTopBarDefaults.colors].
 * @param style Text style configuration, defaults to [LargeTopBarDefaults.style].
 * @param dimens Dimension configuration, defaults to [LargeTopBarDefaults.dimens].
 * @param actions Optional action buttons.
 *
 * @see TopBar for standard variant
 * @see LargeTopBarDefaults for default values
 */
@Composable
fun LargeTopBar(
    title: String?,
    modifier: Modifier = Modifier,
    onNavigationClick: (() -> Unit)? = null,
    colors: LargeTopBarDefaults.LargeTopBarColors = LargeTopBarDefaults.colors(),
    style: LargeTopBarDefaults.LargeTopBarStyle = LargeTopBarDefaults.style(),
    dimens: LargeTopBarDefaults.LargeTopBarDimens = LargeTopBarDefaults.dimens(),
    actions: @Composable (() -> Unit)? = null,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(dimens.contentPadding),
    ) {
        // Navigation row
        Row(
            modifier = Modifier.fillMaxWidth().height(dimens.navigationButtonSize),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onNavigationClick != null) {
                NavigationButton(
                    state = NavigationButtonState(),
                    onClick = onNavigationClick
                )
            } else {
                Spacer(Modifier.width(dimens.navigationButtonSize))
            }

            Row(
                modifier = Modifier.height(dimens.navigationButtonSize),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                actions?.invoke()
            }
        }

        // Title
        if (title != null) {
            Spacer(Modifier.height(dimens.titleTopSpacing))

            Text(
                text = title,
                style = style.title,
                color = colors.title,
            )
        }
    }
}

/**
 * Default configuration values for [LargeTopBar].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object LargeTopBarDefaults {
    /**
     * Color configuration for [LargeTopBar].
     *
     * @param container Background color.
     * @param title Title text color.
     */
    data class LargeTopBarColors(
        val container: Color,
        val title: Color,
    )

    @Composable
    fun colors(
        container: Color = Color.Transparent,
        title: Color = System.color.textBase,
    ) = LargeTopBarColors(
        container = container,
        title = title,
    )

    /**
     * Text style configuration for [LargeTopBar].
     *
     * @param title Title text style.
     */
    data class LargeTopBarStyle(
        val title: TextStyle,
    )

    @Composable
    fun style(title: TextStyle = System.font.title.xLarge) =
        LargeTopBarStyle(
            title = title,
        )

    /**
     * Dimension configuration for [LargeTopBar].
     *
     * @param contentPadding Padding around content.
     * @param navigationButtonSize Size of navigation button placeholder.
     * @param titleTopSpacing Spacing above title.
     */
    data class LargeTopBarDimens(
        val contentPadding: PaddingValues,
        val navigationButtonSize: Dp,
        val titleTopSpacing: Dp,
    )

    @Composable
    fun dimens(
        contentPadding: PaddingValues = PaddingValues(16.dp),
        navigationButtonSize: Dp = 40.dp,
        titleTopSpacing: Dp = 20.dp,
    ) = LargeTopBarDimens(
        contentPadding = contentPadding,
        navigationButtonSize = navigationButtonSize,
        titleTopSpacing = titleTopSpacing,
    )
}

@Preview
@Composable
private fun LargeTopBarPreview() {
    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp)
    ) {
        LargeTopBar(
            title = "Order History",
            onNavigationClick = {},
        )
    }
}
