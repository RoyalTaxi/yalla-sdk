package uz.yalla.components.primitives.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import uz.yalla.resources.icons.Add
import uz.yalla.resources.icons.YallaIcons
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.components.primitives.indicator.LoadingIndicator
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme

@Immutable
data class PrimaryButtonColors(
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
    fun contentColorFor(enabled: Boolean) = if (enabled) contentColor else disabledContentColor
}

@Immutable
data class PrimaryButtonDimens(
    val shape: Shape,
    val minHeight: Dp,
    val contentSpacing: Dp,
    val contentPadding: PaddingValues
)

@Immutable
data class PrimaryButtonStyles(
    val textStyle: TextStyle
)

object PrimaryButtonDefaults {
    @Composable
    fun colors(
        contentColor: Color = System.color.text.white,
        containerColor: Color = System.color.button.active,
        disabledContentColor: Color = System.color.text.white,
        disabledContainerColor: Color = System.color.button.disabled
    ) = PrimaryButtonColors(
        contentColor = contentColor,
        containerColor = containerColor,
        disabledContentColor = disabledContentColor,
        disabledContainerColor = disabledContainerColor
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(16.dp),
        minHeight: Dp = 24.dp,
        contentSpacing: Dp = 12.dp,
        contentPadding: PaddingValues = PaddingValues(20.dp)
    ) = PrimaryButtonDimens(
        shape = shape,
        minHeight = minHeight,
        contentSpacing = contentSpacing,
        contentPadding = contentPadding
    )

    @Composable
    fun styles(
        textStyle: TextStyle = System.font.body.base.medium
    ) = PrimaryButtonStyles(
        textStyle = textStyle
    )
}

@Composable
fun PrimaryButton(
    enabled: Boolean = true,
    loading: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    colors: PrimaryButtonColors = PrimaryButtonDefaults.colors(),
    dimens: PrimaryButtonDimens = PrimaryButtonDefaults.dimens(),
    styles: PrimaryButtonStyles = PrimaryButtonDefaults.styles(),
    leading: @Composable ((PrimaryButtonColors, PrimaryButtonDimens) -> Unit)? = null,
    trailing: @Composable ((PrimaryButtonColors, PrimaryButtonDimens) -> Unit)? = null,
    content: @Composable RowScope.(PrimaryButtonColors, PrimaryButtonDimens, PrimaryButtonStyles) -> Unit
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
                        leading?.invoke(colors, dimens)

                        content(colors, dimens, styles)

                        trailing?.invoke(colors, dimens)
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
        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { },
            content = { colors, _, _ ->
                Text(
                    text = "Button",
                    color = colors.contentColorFor(true)
                )
            }
        )

        PrimaryButton(
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            onClick = { },
            content = { colors, _, _ ->
                Text(
                    text = "Button",
                    color = colors.contentColorFor(true)
                )
            }
        )

        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { },
            leading = { colors, _ ->
                Icon(
                    imageVector = YallaIcons.Add,
                    tint = colors.contentColorFor(true),
                    contentDescription = null
                )
            },
            content = { colors, _, _ ->
                Text(
                    text = "Button",
                    color = colors.contentColorFor(true)
                )
            }
        )

        PrimaryButton(
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            onClick = { },
            leading = { colors, _ ->
                Icon(
                    imageVector = YallaIcons.Add,
                    tint = colors.contentColorFor(false),
                    contentDescription = null
                )
            },
            content = { colors, _, _ ->
                Text(
                    text = "Button",
                    color = colors.contentColorFor(true)
                )
            }
        )
    }
}
