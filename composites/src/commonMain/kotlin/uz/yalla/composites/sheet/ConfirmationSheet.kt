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
 *
 * @param container Background color.
 * @param title Title text color.
 * @param description Description text color.
 * @since 0.0.1
 */
@Immutable
data class ConfirmationSheetColors(
    val container: Color,
    val title: Color,
    val description: Color,
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
 * @since 0.0.1
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
    val actionBottomSpacing: Dp,
)

/**
 * Default values for [ConfirmationSheet].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 * @since 0.0.1
 */
object ConfirmationSheetDefaults {
    /**
     * Creates theme-aware default colors.
     *
     * @since 0.0.1
     */
    @Composable
    fun colors(
        container: Color = System.color.background.base,
        title: Color = System.color.text.base,
        description: Color = System.color.text.subtle,
    ): ConfirmationSheetColors =
        ConfirmationSheetColors(
            container = container,
            title = title,
            description = description,
        )

    /**
     * Creates default dimensions.
     *
     * @since 0.0.1
     */
    fun dimens(
        shape: Shape = RoundedCornerShape(topStart = 38.dp, topEnd = 38.dp),
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
 * @param isVisible Whether sheet is visible.
 * @param onDismissRequest Called when sheet is dismissed.
 * @param image Illustration image.
 * @param title Main title text.
 * @param description Description text.
 * @param actionText Primary action button text.
 * @param onAction Called when action button is tapped.
 * @param modifier Applied to sheet.
 * @param sheetName Optional header title displayed at top.
 * @param colors Color configuration, defaults to [ConfirmationSheetDefaults.colors].
 * @param dimens Dimension configuration, defaults to [ConfirmationSheetDefaults.dimens].
 *
 * @see ConfirmationSheetDefaults for default values
 * @since 0.0.1
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
) {
    Sheet(
        isVisible = isVisible,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        colors = SheetDefaults.colors(container = colors.container),
        dimens = SheetDefaults.dimens(shape = dimens.shape),
        dragHandle = null,
    ) {
        Column(modifier = Modifier.background(colors.container)) {
            Spacer(Modifier.height(dimens.headerTopPadding))

            SheetHeader(
                onClose = onDismissRequest,
                title = sheetName,
                dimens = SheetHeaderDefaults.dimens(
                    contentPadding = PaddingValues(horizontal = dimens.headerHorizontalPadding),
                ),
            )

            Spacer(Modifier.height(dimens.contentTopPadding))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = dimens.contentHorizontalPadding),
            ) {
                Image(
                    painter = image,
                    contentDescription = null,
                    contentScale = ContentScale.Inside,
                    modifier = Modifier
                        .fillMaxWidth(dimens.imageWidthFraction)
                        .aspectRatio(1f),
                )

                Spacer(Modifier.height(dimens.imageBottomSpacing))

                Text(
                    text = title,
                    style = System.font.title.base,
                    color = colors.title,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(dimens.titleDescriptionSpacing))

                Text(
                    text = description,
                    style = System.font.body.base.medium,
                    color = colors.description,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.height(dimens.actionTopSpacing))

            PrimaryButton(
                onClick = onAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimens.actionHorizontalPadding),
            ) {
                Text(actionText)
            }

            Spacer(Modifier.height(dimens.actionBottomSpacing))
        }
    }
}
