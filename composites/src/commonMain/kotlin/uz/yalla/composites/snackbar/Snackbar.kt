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
import androidx.compose.runtime.Immutable
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

enum class SnackbarVariant {
    Success,
    Error,
}

data class SnackbarState(
    val message: String,
    val variant: SnackbarVariant,
    val icon: Painter,
    val dismissIcon: Painter,
)

@Immutable
data class SnackbarColors(
    val container: Color,
    val iconBackground: Color,
    val icon: Color,
    val text: Color,
    val dismissIcon: Color,
)

@Immutable
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
fun Snackbar(
    state: SnackbarState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    messageStyle: TextStyle = System.font.body.small.medium,
    colors: SnackbarColors = SnackbarDefaults.colors(state.variant),
    dimens: SnackbarDimens = SnackbarDefaults.dimens(),
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
                style = messageStyle,
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

object SnackbarDefaults {
    @Composable
    fun colors(variant: SnackbarVariant): SnackbarColors =
        when (variant) {
            SnackbarVariant.Success ->
                SnackbarColors(
                    container = System.color.button.active,
                    iconBackground = Color.White.copy(alpha = 0.2f),
                    icon = System.color.icon.white,
                    text = System.color.icon.white,
                    dismissIcon = System.color.icon.white,
                )
            SnackbarVariant.Error ->
                SnackbarColors(
                    container = System.color.border.error,
                    iconBackground = Color.White.copy(alpha = 0.2f),
                    icon = System.color.icon.white,
                    text = System.color.icon.white,
                    dismissIcon = System.color.icon.white,
                )
        }

    @Composable
    fun colors(
        container: Color = System.color.button.active,
        iconBackground: Color = Color.White.copy(alpha = 0.2f),
        icon: Color = System.color.icon.white,
        text: Color = System.color.icon.white,
        dismissIcon: Color = System.color.icon.white,
    ): SnackbarColors = SnackbarColors(
        container = container,
        iconBackground = iconBackground,
        icon = icon,
        text = text,
        dismissIcon = dismissIcon,
    )

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
    ): SnackbarDimens = SnackbarDimens(
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
