package uz.yalla.components.composite.sheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.components.primitive.button.PrimaryButton
import uz.yalla.components.primitive.button.PrimaryButtonState
import uz.yalla.design.theme.System
import uz.yalla.platform.button.SheetIconButton
import uz.yalla.platform.model.IconType

/**
 * State for [ConfirmationSheet].
 *
 * @property isVisible Whether sheet is visible.
 * @property sheetName Optional header title displayed at top.
 * @property image Illustration image.
 * @property title Main title text.
 * @property description Description text.
 * @property actionText Primary action button text.
 */
data class ConfirmationSheetState(
    val isVisible: Boolean,
    val sheetName: String?,
    val image: Painter,
    val title: String,
    val description: String,
    val actionText: String,
)

/**
 * Effects emitted by [ConfirmationSheet].
 */
sealed interface ConfirmationSheetEffect {
    /** User dismissed the sheet. */
    data object Dismiss : ConfirmationSheetEffect

    /** User confirmed the action. */
    data object Confirm : ConfirmationSheetEffect
}

/**
 * Confirmation sheet with image, title, description, and action.
 *
 * Use for confirmations, success messages, or informational modals.
 *
 * ## Usage
 *
 * ```kotlin
 * ConfirmationSheet(
 *     state = ConfirmationSheetState(
 *         isVisible = state.showSuccess,
 *         sheetName = "Order Status",
 *         image = painterResource(Res.drawable.img_success),
 *         title = "Order Placed",
 *         description = "Your driver is on the way.",
 *         actionText = "Got it",
 *     ),
 *     onEffect = { effect ->
 *         when (effect) {
 *             ConfirmationSheetEffect.Dismiss -> viewModel.dismissSheet()
 *             ConfirmationSheetEffect.Confirm -> viewModel.confirmOrder()
 *         }
 *     },
 * )
 * ```
 *
 * @param state Sheet state including visibility and content.
 * @param onEffect Callback for sheet effects (dismiss, confirm).
 * @param modifier Applied to sheet.
 * @param colors Color configuration, defaults to [ConfirmationSheetDefaults.colors].
 * @param dimens Dimension configuration, defaults to [ConfirmationSheetDefaults.dimens].
 *
 * @see ConfirmationSheetState for content configuration
 * @see ConfirmationSheetEffect for available effects
 * @see ConfirmationSheetDefaults for default values
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationSheet(
    state: ConfirmationSheetState,
    onEffect: (ConfirmationSheetEffect) -> Unit,
    modifier: Modifier = Modifier,
    colors: ConfirmationSheetDefaults.ConfirmationSheetColors = ConfirmationSheetDefaults.colors(),
    dimens: ConfirmationSheetDefaults.ConfirmationSheetDimens = ConfirmationSheetDefaults.dimens(),
) {
    Sheet(
        isVisible = state.isVisible,
        onDismissRequest = { onEffect(ConfirmationSheetEffect.Dismiss) },
        modifier = modifier,
        shape = dimens.shape,
        colors =
            SheetDefaults.colors(
                container = colors.container,
            ),
        dragHandle = null,
    ) {
        Column(modifier = Modifier.background(colors.container)) {
            Spacer(Modifier.height(dimens.headerTopPadding))

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimens.headerHorizontalPadding),
            ) {
                state.sheetName?.let { title ->
                    Text(
                        text = title,
                        style = System.font.body.large.medium,
                        color = colors.title,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                SheetIconButton(
                    iconType = IconType.CLOSE,
                    onClick = { onEffect(ConfirmationSheetEffect.Dismiss) },
                )
            }

            Spacer(Modifier.height(dimens.contentTopPadding))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = dimens.contentHorizontalPadding),
            ) {
                Image(
                    painter = state.image,
                    contentDescription = null,
                    contentScale = ContentScale.Inside,
                    modifier =
                        Modifier
                            .fillMaxWidth(dimens.imageWidthFraction)
                            .aspectRatio(1f),
                )

                Spacer(Modifier.height(dimens.imageBottomSpacing))

                Text(
                    text = state.title,
                    style = System.font.title.base,
                    color = colors.title,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(dimens.titleDescriptionSpacing))

                Text(
                    text = state.description,
                    style = System.font.body.base.medium,
                    color = colors.description,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.height(dimens.actionTopSpacing))

            PrimaryButton(
                state = PrimaryButtonState(text = state.actionText),
                onClick = { onEffect(ConfirmationSheetEffect.Confirm) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimens.actionHorizontalPadding),
            )

            Spacer(Modifier.height(dimens.actionBottomSpacing))
        }
    }
}

/**
 * Default values for [ConfirmationSheet].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 */
object ConfirmationSheetDefaults {
    /**
     * Color configuration for [ConfirmationSheet].
     *
     * @param container Background color.
     * @param title Title text color.
     * @param description Description text color.
     */
    data class ConfirmationSheetColors(
        val container: Color,
        val title: Color,
        val description: Color,
    )

    @Composable
    fun colors(
        container: Color = System.color.backgroundBase,
        title: Color = System.color.textBase,
        description: Color = System.color.textSubtle,
    ): ConfirmationSheetColors =
        ConfirmationSheetColors(
            container = container,
            title = title,
            description = description,
        )

    /**
     * Dimension configuration for [ConfirmationSheet].
     *
     * @param shape Sheet corner shape.
     * @param headerTopPadding Top padding for header.
     * @param headerHorizontalPadding Horizontal padding for header.
     * @param contentTopPadding Top padding for content.
     * @param contentHorizontalPadding Horizontal padding for content.
     * @param imageWidthFraction Image width as fraction of parent.
     * @param imageBottomSpacing Spacing below image.
     * @param titleDescriptionSpacing Spacing between title and description.
     * @param actionTopSpacing Spacing above action button.
     * @param actionHorizontalPadding Horizontal padding for action button.
     * @param actionBottomSpacing Bottom spacing.
     */
    data class ConfirmationSheetDimens(
        val shape: Shape,
        val headerTopPadding: Dp,
        val headerHorizontalPadding: Dp,
        val contentTopPadding: Dp,
        val contentHorizontalPadding: Dp,
        val imageWidthFraction: Float,
        val imageBottomSpacing: Dp,
        val titleDescriptionSpacing: Dp,
        val actionTopSpacing: Dp,
        val actionHorizontalPadding: Dp,
        val actionBottomSpacing: Dp,
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        headerTopPadding: Dp = 10.dp,
        headerHorizontalPadding: Dp = 10.dp,
        contentTopPadding: Dp = 44.dp,
        contentHorizontalPadding: Dp = 36.dp,
        imageWidthFraction: Float = 0.6f,
        imageBottomSpacing: Dp = 36.dp,
        titleDescriptionSpacing: Dp = 12.dp,
        actionTopSpacing: Dp = 64.dp,
        actionHorizontalPadding: Dp = 20.dp,
        actionBottomSpacing: Dp = 12.dp,
    ): ConfirmationSheetDimens =
        ConfirmationSheetDimens(
            shape = shape,
            headerTopPadding = headerTopPadding,
            headerHorizontalPadding = headerHorizontalPadding,
            contentTopPadding = contentTopPadding,
            contentHorizontalPadding = contentHorizontalPadding,
            imageWidthFraction = imageWidthFraction,
            imageBottomSpacing = imageBottomSpacing,
            titleDescriptionSpacing = titleDescriptionSpacing,
            actionTopSpacing = actionTopSpacing,
            actionHorizontalPadding = actionHorizontalPadding,
            actionBottomSpacing = actionBottomSpacing,
        )
}
