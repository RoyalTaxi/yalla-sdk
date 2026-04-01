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
import androidx.compose.runtime.Immutable
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
import uz.yalla.design.theme.YallaTheme

/**
 * State for [EmptyState] display.
 *
 * @property image Illustration painter.
 * @property title Primary title text (e.g., "No rides yet").
 * @property description Optional secondary description text.
 * @since 0.0.1
 */
data class EmptyStateState(
    val image: Painter,
    val title: String,
    val description: String? = null,
)

/**
 * Color configuration for [EmptyState].
 *
 * @param title Title text color.
 * @param description Description text color.
 * @since 0.0.1
 */
@Immutable
data class EmptyStateColors(
    val title: Color,
    val description: Color,
)

/**
 * Dimension configuration for [EmptyState].
 *
 * @param contentPadding Padding around the entire empty state layout.
 * @param imageHeight Fixed height for the illustration image.
 * @param imageTitleSpacing Spacing between image and title.
 * @param titleDescriptionSpacing Spacing between title and description.
 * @param descriptionActionSpacing Spacing between description and action button.
 * @since 0.0.1
 */
@Immutable
data class EmptyStateDimens(
    val contentPadding: PaddingValues,
    val imageHeight: Dp,
    val imageTitleSpacing: Dp,
    val titleDescriptionSpacing: Dp,
    val descriptionActionSpacing: Dp,
)

/**
 * Centered empty state placeholder with illustration, title, description, and optional action.
 *
 * Use when a list or screen has no data to display (e.g., empty order history,
 * no saved places, no notifications).
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
 *     action = {
 *         PrimaryButton(onClick = { bookRide() }) {
 *             Text("Book a ride")
 *         }
 *     },
 * )
 * ```
 *
 * @param state Data containing image, title, and optional description.
 * @param modifier Applied to the root column.
 * @param action Optional action composable rendered below the description.
 * @param titleStyle Text style for the title.
 * @param descriptionStyle Text style for the description.
 * @param colors Color configuration, defaults to [EmptyStateDefaults.colors].
 * @param dimens Dimension configuration, defaults to [EmptyStateDefaults.dimens].
 *
 * @see EmptyStateState
 * @see EmptyStateDefaults
 * @since 0.0.1
 */
@Composable
fun EmptyState(
    state: EmptyStateState,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
    titleStyle: TextStyle = System.font.title.base,
    descriptionStyle: TextStyle = System.font.body.base.medium,
    colors: EmptyStateColors = EmptyStateDefaults.colors(),
    dimens: EmptyStateDimens = EmptyStateDefaults.dimens(),
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
            style = titleStyle,
            color = colors.title,
            textAlign = TextAlign.Center,
        )

        if (state.description != null) {
            Spacer(Modifier.height(dimens.titleDescriptionSpacing))

            Text(
                text = state.description,
                style = descriptionStyle,
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
 * @since 0.0.1
 */
object EmptyStateDefaults {
    /**
     * Creates theme-aware default colors.
     */
    @Composable
    fun colors(
        title: Color = System.color.text.base,
        description: Color = System.color.text.subtle,
    ): EmptyStateColors = EmptyStateColors(
        title = title,
        description = description,
    )

    /**
     * Creates default dimensions.
     */
    fun dimens(
        contentPadding: PaddingValues = PaddingValues(horizontal = 32.dp, vertical = 48.dp),
        imageHeight: Dp = 180.dp,
        imageTitleSpacing: Dp = 24.dp,
        titleDescriptionSpacing: Dp = 8.dp,
        descriptionActionSpacing: Dp = 24.dp,
    ): EmptyStateDimens = EmptyStateDimens(
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
    YallaTheme {
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
}
