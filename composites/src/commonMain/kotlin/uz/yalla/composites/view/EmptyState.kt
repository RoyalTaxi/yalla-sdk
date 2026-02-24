package uz.yalla.composites.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * State for [EmptyState].
 *
 * @property image Illustration image.
 * @property title Empty state title.
 * @property description Optional description text.
 */
data class EmptyStateState(
    val image: Painter,
    val title: String,
    val description: String? = null,
)

/**
 * Empty state view for lists with no content.
 *
 * Shows illustration, title, and optional description.
 *
 * ## Usage
 *
 * ```kotlin
 * EmptyState(
 *     state = EmptyStateState(
 *         image = painterResource(Res.drawable.img_empty_history),
 *         title = "No rides yet",
 *         description = "Your ride history will appear here",
 *     ),
 * )
 * ```
 *
 * ## With Action
 *
 * ```kotlin
 * EmptyState(
 *     state = EmptyStateState(
 *         image = painterResource(Res.drawable.img_empty_notifications),
 *         title = "No notifications",
 *     ),
 *     action = {
 *         TextButton(onClick = { refresh() }) {
 *             Text("Refresh")
 *         }
 *     },
 * )
 * ```
 *
 * @param state Empty state configuration with image and text.
 * @param modifier Applied to component.
 * @param action Optional action composable.
 * @param colors Color configuration, defaults to [EmptyStateDefaults.colors].
 * @param style Text style configuration, defaults to [EmptyStateDefaults.style].
 * @param dimens Dimension configuration, defaults to [EmptyStateDefaults.dimens].
 *
 * @see EmptyStateState for state configuration
 * @see EmptyStateDefaults for default values
 */
@Composable
fun EmptyState(
    state: EmptyStateState,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
    colors: EmptyStateDefaults.EmptyStateColors = EmptyStateDefaults.colors(),
    style: EmptyStateDefaults.EmptyStateStyle = EmptyStateDefaults.style(),
    dimens: EmptyStateDefaults.EmptyStateDimens = EmptyStateDefaults.dimens(),
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(dimens.contentPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = state.image,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.height(dimens.imageHeight),
        )

        Spacer(Modifier.height(dimens.imageTitleSpacing))

        Text(
            text = state.title,
            style = style.title,
            color = colors.title,
            textAlign = TextAlign.Center,
        )

        if (state.description != null) {
            Spacer(Modifier.height(dimens.titleDescriptionSpacing))

            Text(
                text = state.description,
                style = style.description,
                color = colors.description,
                textAlign = TextAlign.Center,
            )
        }

        if (action != null) {
            Spacer(Modifier.height(dimens.descriptionActionSpacing))
            action()
        }
    }
}

/**
 * Default configuration values for [EmptyState].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object EmptyStateDefaults {
    /**
     * Color configuration for [EmptyState].
     *
     * @param title Title text color.
     * @param description Description text color.
     */
    data class EmptyStateColors(
        val title: Color,
        val description: Color,
    )

    @Composable
    fun colors(
        title: Color = System.color.textBase,
        description: Color = System.color.textSubtle,
    ) = EmptyStateColors(
        title = title,
        description = description,
    )

    /**
     * Text style configuration for [EmptyState].
     *
     * @param title Title text style.
     * @param description Description text style.
     */
    data class EmptyStateStyle(
        val title: TextStyle,
        val description: TextStyle,
    )

    @Composable
    fun style(
        title: TextStyle = System.font.title.base,
        description: TextStyle = System.font.body.base.medium,
    ) = EmptyStateStyle(
        title = title,
        description = description,
    )

    /**
     * Dimension configuration for [EmptyState].
     *
     * @param contentPadding Padding around content.
     * @param imageHeight Height of the illustration.
     * @param imageTitleSpacing Spacing between image and title.
     * @param titleDescriptionSpacing Spacing between title and description.
     * @param descriptionActionSpacing Spacing between description and action.
     */
    data class EmptyStateDimens(
        val contentPadding: PaddingValues,
        val imageHeight: Dp,
        val imageTitleSpacing: Dp,
        val titleDescriptionSpacing: Dp,
        val descriptionActionSpacing: Dp,
    )

    @Composable
    fun dimens(
        contentPadding: PaddingValues = PaddingValues(horizontal = 32.dp, vertical = 48.dp),
        imageHeight: Dp = 180.dp,
        imageTitleSpacing: Dp = 24.dp,
        titleDescriptionSpacing: Dp = 8.dp,
        descriptionActionSpacing: Dp = 24.dp,
    ) = EmptyStateDimens(
        contentPadding = contentPadding,
        imageHeight = imageHeight,
        imageTitleSpacing = imageTitleSpacing,
        titleDescriptionSpacing = titleDescriptionSpacing,
        descriptionActionSpacing = descriptionActionSpacing,
    )
}

@Preview
@Composable
private fun EmptyStatePreview() {
    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp),
        ) {
            Text(
                text = "No rides yet",
                style = System.font.title.base,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Your ride history will appear here",
                style = System.font.body.base.medium,
                textAlign = TextAlign.Center,
            )
        }
    }
}
