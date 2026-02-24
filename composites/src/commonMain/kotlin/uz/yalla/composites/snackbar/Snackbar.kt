package uz.yalla.composites.snackbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Snackbar variant for success/error states.
 */
enum class SnackbarVariant {
    Success,
    Error,
}

/**
 * State for [Snackbar].
 *
 * @property message Snackbar text.
 * @property variant Success or error variant.
 * @property icon Leading icon.
 * @property dismissIcon Dismiss button icon.
 */
data class SnackbarState(
    val message: String,
    val variant: SnackbarVariant,
    val icon: Painter,
    val dismissIcon: Painter,
)

/**
 * Snackbar message with icon, text, and dismiss action.
 *
 * Use for transient feedback messages.
 *
 * ## Usage
 *
 * ```kotlin
 * Snackbar(
 *     state = SnackbarState(
 *         message = "Card added successfully",
 *         variant = SnackbarVariant.Success,
 *         icon = painterResource(Res.drawable.ic_check_circle),
 *         dismissIcon = painterResource(Res.drawable.ic_x),
 *     ),
 *     onDismiss = { snackbarHostState.currentSnackbarData?.dismiss() },
 * )
 * ```
 *
 * @param state Snackbar state with message, variant, and icons.
 * @param onDismiss Called when dismiss clicked.
 * @param modifier Applied to snackbar.
 * @param colors Color configuration, defaults to [SnackbarDefaults.colors].
 * @param style Text style configuration, defaults to [SnackbarDefaults.style].
 * @param dimens Dimension configuration, defaults to [SnackbarDefaults.dimens].
 *
 * @see SnackbarState for state configuration
 * @see SnackbarDefaults for default values
 */
@Composable
fun Snackbar(
    state: SnackbarState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    colors: SnackbarDefaults.SnackbarColors = SnackbarDefaults.colors(state.variant),
    style: SnackbarDefaults.SnackbarStyle = SnackbarDefaults.style(),
    dimens: SnackbarDefaults.SnackbarDimens = SnackbarDefaults.dimens(),
) {
    Card(
        modifier = modifier,
        shape = dimens.shape,
        colors =
            CardDefaults.cardColors(
                containerColor = colors.container,
            ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.contentSpacing),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = dimens.verticalPadding,
                        horizontal = dimens.horizontalPadding,
                    ),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier =
                    Modifier.background(
                        color = colors.iconBackground,
                        shape = RoundedCornerShape(dimens.iconBackgroundRadius),
                    ),
            ) {
                Icon(
                    painter = state.icon,
                    contentDescription = null,
                    tint = colors.icon,
                    modifier =
                        Modifier
                            .padding(dimens.iconPadding)
                            .size(dimens.iconSize),
                )
            }

            Text(
                text = state.message,
                style = style.message,
                color = colors.text,
                modifier = Modifier.weight(1f),
                maxLines = dimens.messageMaxLines,
                overflow = TextOverflow.Ellipsis,
            )

            Icon(
                painter = state.dismissIcon,
                contentDescription = null,
                tint = colors.dismissIcon,
                modifier =
                    Modifier
                        .size(dimens.dismissIconSize)
                        .clickable(onClick = onDismiss),
            )
        }
    }
}

/**
 * Default configuration values for [Snackbar].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object SnackbarDefaults {
    /**
     * Color configuration for [Snackbar].
     *
     * @param container Background color.
     * @param iconBackground Background color for icon container.
     * @param icon Icon tint color.
     * @param text Message text color.
     * @param dismissIcon Dismiss icon tint color.
     */
    data class SnackbarColors(
        val container: Color,
        val iconBackground: Color,
        val icon: Color,
        val text: Color,
        val dismissIcon: Color,
    )

    @Composable
    fun colors(variant: SnackbarVariant): SnackbarColors =
        when (variant) {
            SnackbarVariant.Success ->
                SnackbarColors(
                    container = System.color.buttonActive,
                    iconBackground = Color.White.copy(alpha = 0.2f),
                    icon = System.color.iconWhite,
                    text = System.color.iconWhite,
                    dismissIcon = System.color.iconWhite,
                )
            SnackbarVariant.Error ->
                SnackbarColors(
                    container = System.color.borderError,
                    iconBackground = Color.White.copy(alpha = 0.2f),
                    icon = System.color.iconWhite,
                    text = System.color.iconWhite,
                    dismissIcon = System.color.iconWhite,
                )
        }

    @Composable
    fun colors(
        container: Color = System.color.buttonActive,
        iconBackground: Color = Color.White.copy(alpha = 0.2f),
        icon: Color = System.color.iconWhite,
        text: Color = System.color.iconWhite,
        dismissIcon: Color = System.color.iconWhite,
    ) = SnackbarColors(
        container = container,
        iconBackground = iconBackground,
        icon = icon,
        text = text,
        dismissIcon = dismissIcon,
    )

    /**
     * Text style configuration for [Snackbar].
     *
     * @param message Message text style.
     */
    data class SnackbarStyle(
        val message: TextStyle,
    )

    @Composable
    fun style(message: TextStyle = System.font.body.small.medium) =
        SnackbarStyle(
            message = message,
        )

    /**
     * Dimension configuration for [Snackbar].
     *
     * @param shape Snackbar corner shape.
     * @param contentSpacing Spacing between elements.
     * @param verticalPadding Vertical padding for content.
     * @param horizontalPadding Horizontal padding for content.
     * @param iconSize Leading icon size.
     * @param iconPadding Padding inside icon container.
     * @param iconBackgroundRadius Icon background corner radius.
     * @param dismissIconSize Dismiss icon size.
     * @param messageMaxLines Maximum lines for message text.
     */
    data class SnackbarDimens(
        val shape: Shape,
        val contentSpacing: Dp,
        val verticalPadding: Dp,
        val horizontalPadding: Dp,
        val iconSize: Dp,
        val iconPadding: Dp,
        val iconBackgroundRadius: Dp,
        val dismissIconSize: Dp,
        val messageMaxLines: Int,
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(12.dp),
        contentSpacing: Dp = 8.dp,
        verticalPadding: Dp = 12.dp,
        horizontalPadding: Dp = 16.dp,
        iconSize: Dp = 24.dp,
        iconPadding: Dp = 6.dp,
        iconBackgroundRadius: Dp = 36.dp,
        dismissIconSize: Dp = 20.dp,
        messageMaxLines: Int = 2,
    ) = SnackbarDimens(
        shape = shape,
        contentSpacing = contentSpacing,
        verticalPadding = verticalPadding,
        horizontalPadding = horizontalPadding,
        iconSize = iconSize,
        iconPadding = iconPadding,
        iconBackgroundRadius = iconBackgroundRadius,
        dismissIconSize = dismissIconSize,
        messageMaxLines = messageMaxLines,
    )
}
