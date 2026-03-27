package uz.yalla.composites.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.primitives.button.PrimaryButton
import uz.yalla.primitives.button.SecondaryButton

/**
 * Color configuration for [ActionSheet].
 *
 * @param container Background color.
 * @param title Title text color.
 * @param message Message text color.
 * @param divider Divider color.
 * @since 0.0.1
 */
@Immutable
data class ActionSheetColors(
    val container: Color,
    val title: Color,
    val message: Color,
    val divider: Color,
)

/**
 * Dimension configuration for [ActionSheet].
 *
 * @param shape Sheet corner shape.
 * @param contentPadding Padding around content.
 * @param topSpacing Top spacing.
 * @param titleMessageSpacing Spacing between title and message.
 * @param contentButtonsSpacing Spacing between content and divider.
 * @param dividerButtonSpacing Spacing between divider and buttons.
 * @param buttonSpacing Spacing between buttons.
 * @param bottomSpacing Bottom spacing.
 * @param dividerThickness Divider thickness.
 * @since 0.0.1
 */
@Immutable
data class ActionSheetDimens(
    val shape: Shape,
    val contentPadding: PaddingValues,
    val topSpacing: Dp,
    val titleMessageSpacing: Dp,
    val contentButtonsSpacing: Dp,
    val dividerButtonSpacing: Dp,
    val buttonSpacing: Dp,
    val bottomSpacing: Dp,
    val dividerThickness: Dp,
)

/**
 * Default configuration values for [ActionSheet].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 * @since 0.0.1
 */
object ActionSheetDefaults {
    /**
     * Creates theme-aware default colors.
     *
     * @since 0.0.1
     */
    @Composable
    fun colors(
        container: Color = System.color.background.base,
        title: Color = System.color.text.base,
        message: Color = System.color.text.subtle,
        divider: Color = System.color.border.disabled,
    ): ActionSheetColors =
        ActionSheetColors(
            container = container,
            title = title,
            message = message,
            divider = divider,
        )

    /**
     * Creates default dimensions.
     *
     * @since 0.0.1
     */
    fun dimens(
        shape: Shape = RoundedCornerShape(topStart = 38.dp, topEnd = 38.dp),
        contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp),
        topSpacing: Dp = 16.dp,
        titleMessageSpacing: Dp = 8.dp,
        contentButtonsSpacing: Dp = 24.dp,
        dividerButtonSpacing: Dp = 16.dp,
        buttonSpacing: Dp = 8.dp,
        bottomSpacing: Dp = 12.dp,
        dividerThickness: Dp = 1.dp,
    ): ActionSheetDimens =
        ActionSheetDimens(
            shape = shape,
            contentPadding = contentPadding,
            topSpacing = topSpacing,
            titleMessageSpacing = titleMessageSpacing,
            contentButtonsSpacing = contentButtonsSpacing,
            dividerButtonSpacing = dividerButtonSpacing,
            buttonSpacing = buttonSpacing,
            bottomSpacing = bottomSpacing,
            dividerThickness = dividerThickness,
        )
}

/**
 * Action sheet with title, message, and action buttons.
 *
 * Use for prompting user actions with primary/secondary options.
 *
 * ## Usage
 *
 * ```kotlin
 * ActionSheet(
 *     isVisible = showDelete,
 *     onDismissRequest = { viewModel.dismissSheet() },
 *     title = "Delete Card",
 *     primaryAction = "Delete",
 *     onPrimaryAction = { viewModel.deleteCard() },
 *     message = "Are you sure you want to delete this card?",
 *     secondaryAction = "Cancel",
 *     onSecondaryAction = { viewModel.cancelDelete() },
 * )
 * ```
 *
 * @param isVisible Whether the sheet is visible.
 * @param onDismissRequest Called when sheet is dismissed.
 * @param title Sheet title.
 * @param primaryAction Primary button text.
 * @param onPrimaryAction Called when primary button is tapped.
 * @param modifier Applied to sheet.
 * @param message Optional description text.
 * @param secondaryAction Optional secondary button text.
 * @param onSecondaryAction Called when secondary button is tapped.
 * @param colors Color configuration, defaults to [ActionSheetDefaults.colors].
 * @param dimens Dimension configuration, defaults to [ActionSheetDefaults.dimens].
 *
 * @see ActionSheetDefaults for default values
 * @since 0.0.1
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionSheet(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    primaryAction: String,
    onPrimaryAction: () -> Unit,
    modifier: Modifier = Modifier,
    message: String? = null,
    secondaryAction: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    colors: ActionSheetColors = ActionSheetDefaults.colors(),
    dimens: ActionSheetDimens = ActionSheetDefaults.dimens(),
) {
    Sheet(
        isVisible = isVisible,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        colors = SheetDefaults.colors(container = colors.container),
        dimens = SheetDefaults.dimens(shape = dimens.shape),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(dimens.contentPadding),
        ) {
            Spacer(Modifier.height(dimens.topSpacing))

            Text(
                text = title,
                style = System.font.title.base,
                color = colors.title,
                textAlign = TextAlign.Center,
            )

            if (message != null) {
                Spacer(Modifier.height(dimens.titleMessageSpacing))

                Text(
                    text = message,
                    style = System.font.body.base.medium,
                    color = colors.message,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.height(dimens.contentButtonsSpacing))

            HorizontalDivider(
                color = colors.divider,
                thickness = dimens.dividerThickness,
            )

            Spacer(Modifier.height(dimens.dividerButtonSpacing))

            PrimaryButton(
                onClick = onPrimaryAction,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(primaryAction)
            }

            if (secondaryAction != null && onSecondaryAction != null) {
                Spacer(Modifier.height(dimens.buttonSpacing))

                SecondaryButton(
                    onClick = onSecondaryAction,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(secondaryAction)
                }
            }

            Spacer(Modifier.height(dimens.bottomSpacing))
        }
    }
}

@Preview
@Composable
private fun ActionSheetContentPreview() {
    YallaTheme {
        val dimens = ActionSheetDefaults.dimens()
        Box(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(dimens.contentPadding),
            ) {
                Text(
                    text = "Delete Card",
                    style = System.font.title.base,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Are you sure you want to delete this card?",
                    style = System.font.body.base.medium,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(16.dp))
                PrimaryButton(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Delete")
                }
                Spacer(Modifier.height(8.dp))
                SecondaryButton(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}
