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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.primitives.button.PrimaryButton
import uz.yalla.primitives.button.PrimaryButtonState
import uz.yalla.primitives.button.SecondaryButton
import uz.yalla.primitives.button.SecondaryButtonState

/**
 * UI state for [ActionSheet].
 *
 * @property isVisible Whether the sheet is visible.
 * @property title Sheet title.
 * @property message Optional description text.
 * @property primaryAction Primary button text.
 * @property secondaryAction Optional secondary button text.
 */
data class ActionSheetState(
    val isVisible: Boolean,
    val title: String,
    val message: String? = null,
    val primaryAction: String,
    val secondaryAction: String? = null,
)

/**
 * Effects emitted by [ActionSheet].
 */
sealed interface ActionSheetEffect {
    /** User dismissed the sheet. */
    data object Dismiss : ActionSheetEffect

    /** User clicked primary action. */
    data object Primary : ActionSheetEffect

    /** User clicked secondary action. */
    data object Secondary : ActionSheetEffect
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
 *     state = ActionSheetState(
 *         isVisible = showDelete,
 *         title = "Delete Card",
 *         message = "Are you sure you want to delete this card?",
 *         primaryAction = "Delete",
 *         secondaryAction = "Cancel",
 *     ),
 *     onEffect = { effect ->
 *         when (effect) {
 *             ActionSheetEffect.Dismiss -> viewModel.dismissSheet()
 *             ActionSheetEffect.Primary -> viewModel.deleteCard()
 *             ActionSheetEffect.Secondary -> viewModel.cancelDelete()
 *         }
 *     },
 * )
 * ```
 *
 * @param state Sheet state including visibility and content.
 * @param onEffect Callback for sheet effects (dismiss, primary, secondary).
 * @param modifier Applied to sheet.
 * @param colors Color configuration, defaults to [ActionSheetDefaults.colors].
 * @param style Text style configuration, defaults to [ActionSheetDefaults.style].
 * @param dimens Dimension configuration, defaults to [ActionSheetDefaults.dimens].
 *
 * @see ActionSheetState for state configuration
 * @see ActionSheetEffect for available effects
 * @see ActionSheetDefaults for default values
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionSheet(
    state: ActionSheetState,
    onEffect: (ActionSheetEffect) -> Unit,
    modifier: Modifier = Modifier,
    colors: ActionSheetDefaults.ActionSheetColors = ActionSheetDefaults.colors(),
    style: ActionSheetDefaults.ActionSheetStyle = ActionSheetDefaults.style(),
    dimens: ActionSheetDefaults.ActionSheetDimens = ActionSheetDefaults.dimens(),
) {
    Sheet(
        isVisible = state.isVisible,
        onDismissRequest = { onEffect(ActionSheetEffect.Dismiss) },
        modifier = modifier,
        shape = dimens.shape,
        colors =
            SheetDefaults.colors(
                container = colors.container,
            ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(dimens.contentPadding),
        ) {
            Spacer(Modifier.height(dimens.topSpacing))

            Text(
                text = state.title,
                style = style.title,
                color = colors.title,
                textAlign = TextAlign.Center,
            )

            if (state.message != null) {
                Spacer(Modifier.height(dimens.titleMessageSpacing))

                Text(
                    text = state.message,
                    style = style.message,
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
                state = PrimaryButtonState(text = state.primaryAction),
                onClick = { onEffect(ActionSheetEffect.Primary) },
                modifier = Modifier.fillMaxWidth(),
            )

            if (state.secondaryAction != null) {
                Spacer(Modifier.height(dimens.buttonSpacing))

                SecondaryButton(
                    state = SecondaryButtonState(text = state.secondaryAction),
                    onClick = { onEffect(ActionSheetEffect.Secondary) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(dimens.bottomSpacing))
        }
    }
}

/**
 * Default configuration values for [ActionSheet].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object ActionSheetDefaults {
    /**
     * Color configuration for [ActionSheet].
     *
     * @param container Background color.
     * @param title Title text color.
     * @param message Message text color.
     * @param divider Divider color.
     */
    data class ActionSheetColors(
        val container: Color,
        val title: Color,
        val message: Color,
        val divider: Color,
    )

    @Composable
    fun colors(
        container: Color = System.color.backgroundBase,
        title: Color = System.color.textBase,
        message: Color = System.color.textSubtle,
        divider: Color = System.color.borderDisabled,
    ) = ActionSheetColors(
        container = container,
        title = title,
        message = message,
        divider = divider,
    )

    /**
     * Text style configuration for [ActionSheet].
     *
     * @param title Title text style.
     * @param message Message text style.
     */
    data class ActionSheetStyle(
        val title: TextStyle,
        val message: TextStyle,
    )

    @Composable
    fun style(
        title: TextStyle = System.font.title.base,
        message: TextStyle = System.font.body.base.medium,
    ) = ActionSheetStyle(
        title = title,
        message = message,
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
     */
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

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp),
        topSpacing: Dp = 16.dp,
        titleMessageSpacing: Dp = 8.dp,
        contentButtonsSpacing: Dp = 24.dp,
        dividerButtonSpacing: Dp = 16.dp,
        buttonSpacing: Dp = 8.dp,
        bottomSpacing: Dp = 12.dp,
        dividerThickness: Dp = 1.dp,
    ) = ActionSheetDimens(
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

@Preview
@Composable
private fun ActionSheetContentPreview() {
    val dimens = ActionSheetDefaults.dimens()
    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp)
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
                state = PrimaryButtonState(text = "Delete"),
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            SecondaryButton(
                state = SecondaryButtonState(text = "Cancel"),
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
