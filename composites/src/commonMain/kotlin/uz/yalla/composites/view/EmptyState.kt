package uz.yalla.composites.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

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
 * @param imageHeight Fixed height reserved for the illustration slot.
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
 * The default text style and color for [title] and [description] are injected via
 * [ProvideTextStyle][androidx.compose.material3.ProvideTextStyle]; a plain
 * [Text][androidx.compose.material3.Text] in the slot inherits them automatically.
 *
 * ## Usage
 *
 * ```kotlin
 * EmptyState(
 *     image = { Image(painterResource(Res.drawable.img_empty_history), null) },
 *     title = { Text("No rides yet") },
 *     description = { Text("Your ride history will appear here") },
 *     action = {
 *         PrimaryButton(onClick = { bookRide() }) {
 *             Text("Book a ride")
 *         }
 *     },
 * )
 * ```
 *
 * @param image Illustration slot rendered at the top.
 * @param title Primary title slot; receives [System.font.title.base] style by default.
 * @param modifier Applied to the root column.
 * @param description Optional secondary description slot; receives [System.font.body.base.medium] style.
 * @param action Optional action composable rendered below the description.
 * @param colors Color configuration, defaults to [EmptyStateDefaults.colors].
 * @param dimens Dimension configuration, defaults to [EmptyStateDefaults.dimens].
 *
 * @see EmptyStateDefaults
 * @since 0.0.1
 */
@Composable
fun EmptyState(
    image: @Composable () -> Unit,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    description: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
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
        image()

        Spacer(Modifier.height(dimens.imageTitleSpacing))

        ProvideTextStyle(
            System.font.title.base
                .copy(color = colors.title)
        ) {
            title()
        }

        if (description != null) {
            Spacer(Modifier.height(dimens.titleDescriptionSpacing))

            ProvideTextStyle(
                System.font.body.base.medium
                    .copy(color = colors.description)
            ) {
                description()
            }
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
