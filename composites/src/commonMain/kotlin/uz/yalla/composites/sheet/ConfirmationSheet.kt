package uz.yalla.composites.sheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.primitives.button.PrimaryButton

/**
 * Color configuration for [ConfirmationSheet].
 */
@Immutable
data class ConfirmationSheetColors(
    val container: Color,
    val title: Color,
    val description: Color
)

/**
 * Dimension configuration for [ConfirmationSheet].
 */
@Immutable
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
    val actionBottomSpacing: Dp
)

/**
 * Default values for [ConfirmationSheet].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 */
object ConfirmationSheetDefaults {
    /**
     * Creates theme-aware default colors.
     */
    @Composable
    fun colors(
        container: Color = System.color.background.base,
        title: Color = System.color.text.base,
        description: Color = System.color.text.subtle
    ): ConfirmationSheetColors =
        ConfirmationSheetColors(
            container = container,
            title = title,
            description = description
        )

    /**
     * Creates default dimensions.
     */
    fun dimens(
        shape: Shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        headerTopPadding: Dp = 16.dp,
        headerHorizontalPadding: Dp = 16.dp,
        contentTopPadding: Dp = 44.dp,
        contentHorizontalPadding: Dp = 36.dp,
        imageWidthFraction: Float = 0.6f,
        imageBottomSpacing: Dp = 36.dp,
        titleDescriptionSpacing: Dp = 12.dp,
        actionTopSpacing: Dp = 64.dp,
        actionHorizontalPadding: Dp = 20.dp,
        actionBottomSpacing: Dp = 12.dp
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
            actionBottomSpacing = actionBottomSpacing
        )
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
 *     isVisible = state.showSuccess,
 *     onDismissRequest = { viewModel.dismissSheet() },
 *     image = painterResource(Res.drawable.img_success),
 *     title = "Order Placed",
 *     description = "Your driver is on the way.",
 *     actionText = "Got it",
 *     onAction = { viewModel.confirmOrder() },
 *     sheetName = "Order Status",
 * )
 * ```
 *
 * @param colors Color configuration, defaults to [ConfirmationSheetDefaults.colors].
 * @param dimens Dimension configuration, defaults to [ConfirmationSheetDefaults.dimens].
 * @param dismissEnabled When `false`, hides the sheet header (no close button) and
 *   ignores tap-outside / drag-down dismissal — the only way to advance is via the
 *   action button. Use for blocker sheets (no-internet, force-update). Defaults to `true`.
 * @param onDismissAttempt Called when the user tries to dismiss while [dismissEnabled]
 *   is `false`. Useful for analytics or showing why the sheet can't be closed.
 * @param actionLoading When `true`, the action button shows a loading indicator and
 *   is non-interactive. Defaults to `false`.
 *
 * @see ConfirmationSheetDefaults for default values
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationSheet(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    image: Painter,
    title: String,
    description: String,
    actionText: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    sheetName: String? = null,
    colors: ConfirmationSheetColors = ConfirmationSheetDefaults.colors(),
    dimens: ConfirmationSheetDimens = ConfirmationSheetDefaults.dimens(),
    dismissEnabled: Boolean = true,
    onDismissAttempt: () -> Unit = {},
    actionLoading: Boolean = false
) {
    Sheet(
        isVisible = isVisible,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        colors = SheetDefaults.colors(container = colors.container),
        dimens = SheetDefaults.dimens(shape = dimens.shape),
        dragHandle = null,
        dismissEnabled = dismissEnabled,
        onDismissAttempt = onDismissAttempt
    ) {
        Column(modifier = Modifier.background(colors.container)) {
            Spacer(Modifier.height(dimens.headerTopPadding))

            if (dismissEnabled) {
                SheetHeader(
                    onClose = onDismissRequest,
                    title = sheetName,
                    dimens =
                        SheetHeaderDefaults.dimens(
                            contentPadding = PaddingValues(horizontal = dimens.headerHorizontalPadding)
                        )
                )
            }

            Spacer(Modifier.height(dimens.contentTopPadding))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimens.contentHorizontalPadding)
            ) {
                Image(
                    painter = image,
                    contentDescription = null,
                    contentScale = ContentScale.Inside,
                    modifier =
                        Modifier
                            .fillMaxWidth(dimens.imageWidthFraction)
                            .aspectRatio(1f)
                )

                Spacer(Modifier.height(dimens.imageBottomSpacing))

                Text(
                    text = title,
                    style = System.font.title.base,
                    color = colors.title,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(dimens.titleDescriptionSpacing))

                Text(
                    text = description,
                    style = System.font.body.base.medium,
                    color = colors.description,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(dimens.actionTopSpacing))

            PrimaryButton(
                onClick = onAction,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimens.actionHorizontalPadding),
                loading = actionLoading
            ) {
                Text(actionText)
            }

            Spacer(Modifier.height(dimens.actionBottomSpacing))
        }
    }
}
