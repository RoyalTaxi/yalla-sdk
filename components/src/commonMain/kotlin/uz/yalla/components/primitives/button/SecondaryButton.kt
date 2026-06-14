package uz.yalla.components.primitives.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.components.primitives.indicator.LoadingIndicator
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.icons.FocusLocation
import uz.yalla.resources.icons.YallaIcons

@Immutable
public data class SecondaryButtonColors(
    val contentColor: Color,
    val containerColor: Color,
    val disabledContentColor: Color,
    val disabledContainerColor: Color
) {
    @Composable
    internal fun asButtonColors() = ButtonDefaults.buttonColors(
        contentColor = contentColor,
        containerColor = containerColor,
        disabledContentColor = disabledContentColor,
        disabledContainerColor = disabledContainerColor
    )

    @Composable
    public fun contentColorFor(enabled: Boolean): Color = if (enabled) contentColor else disabledContentColor
}

@Immutable
public data class SecondaryButtonDimens(
    val shape: Shape,
    val minHeight: Dp,
    val contentSpacing: Dp,
    val contentPadding: PaddingValues
)

@Immutable
public data class SecondaryButtonStyles(
    val textStyle: TextStyle
)

public object SecondaryButtonDefaults {
    @Composable
    public fun colors(
        contentColor: Color = System.color.background.base,
        containerColor: Color = System.color.button.tertiary,
        disabledContentColor: Color = System.color.text.white,
        disabledContainerColor: Color = System.color.button.disabled
    ): SecondaryButtonColors = SecondaryButtonColors(
        contentColor = contentColor,
        containerColor = containerColor,
        disabledContentColor = disabledContentColor,
        disabledContainerColor = disabledContainerColor
    )

    @Composable
    public fun dimens(
        shape: Shape = RoundedCornerShape(16.dp),
        minHeight: Dp = 24.dp,
        contentSpacing: Dp = 12.dp,
        contentPadding: PaddingValues = PaddingValues(20.dp)
    ): SecondaryButtonDimens = SecondaryButtonDimens(
        shape = shape,
        minHeight = minHeight,
        contentSpacing = contentSpacing,
        contentPadding = contentPadding
    )

    @Composable
    public fun styles(
        textStyle: TextStyle = System.font.body.base.medium
    ): SecondaryButtonStyles = SecondaryButtonStyles(
        textStyle = textStyle
    )
}

@Composable
public fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    colors: SecondaryButtonColors = SecondaryButtonDefaults.colors(),
    dimens: SecondaryButtonDimens = SecondaryButtonDefaults.dimens(),
    styles: SecondaryButtonStyles = SecondaryButtonDefaults.styles(),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Button(
        enabled = enabled && !loading,
        modifier = modifier,
        onClick = onClick,
        colors = colors.asButtonColors(),
        shape = dimens.shape,
        contentPadding = dimens.contentPadding,
        content = {
            Box(
                modifier = Modifier.heightIn(min = dimens.minHeight),
                contentAlignment = Alignment.Center
            ) {
                if (loading) {
                    LoadingIndicator(
                        color = colors.contentColor,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            space = dimens.contentSpacing,
                            alignment = Alignment.CenterHorizontally
                        )
                    ) {
                        leadingIcon?.invoke()

                        Text(
                            text = text,
                            color = colors.contentColorFor(enabled),
                            style = styles.textStyle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        trailingIcon?.invoke()
                    }
                }
            }
        }
    )
}

@Preview
@Composable
private fun Preview() = YallaTheme {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SecondaryButton(
            text = "Cancel",
            modifier = Modifier.fillMaxWidth(),
            onClick = { }
        )

        SecondaryButton(
            text = "Cancel",
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            onClick = { }
        )

        SecondaryButton(
            text = "Enable location",
            onClick = { },
            colors = SecondaryButtonDefaults.colors(
                contentColor = System.color.icon.white,
                containerColor = System.color.icon.red
            ),
            dimens = SecondaryButtonDefaults.dimens(
                shape = CircleShape,
                contentPadding = PaddingValues(start = 22.dp, end = 12.dp)
            ),
            styles = SecondaryButtonDefaults.styles(
                textStyle = System.font.body.caption
            ),
            trailingIcon = {
                Icon(
                    imageVector = YallaIcons.FocusLocation,
                    contentDescription = null
                )
            }
        )
    }
}
