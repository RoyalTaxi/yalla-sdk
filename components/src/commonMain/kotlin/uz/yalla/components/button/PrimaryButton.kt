package uz.yalla.components.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
    fun contentColorFor(enabled: Boolean) =
        if (enabled) contentColor else disabledContentColor
}

@Immutable
data class PrimaryButtonDimens(
    val shape: Shape,
    val contentSpacing: Dp,
    val contentPadding: PaddingValues
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
        shape: Shape = RoundedCornerShape(System.radius.l),
        contentSpacing: Dp = System.space.scale.m,
        contentPadding: PaddingValues = PaddingValues(
            vertical = System.space.scale.l,
            horizontal = System.space.scale.xxl
        )
    ) = PrimaryButtonDimens(
        shape = shape,
        contentSpacing = contentSpacing,
        contentPadding = contentPadding
    )
}

@Composable
fun PrimaryButton(
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    colors: PrimaryButtonColors = PrimaryButtonDefaults.colors(),
    dimens: PrimaryButtonDimens = PrimaryButtonDefaults.dimens(),
    leading: @Composable ((PrimaryButtonColors, PrimaryButtonDimens) -> Unit)? = null,
    trailing: @Composable ((PrimaryButtonColors, PrimaryButtonDimens) -> Unit)? = null,
    content: @Composable RowScope.(PrimaryButtonColors, PrimaryButtonDimens) -> Unit
) {
    Button(
        enabled = enabled,
        modifier = modifier,
        onClick = onClick,
        colors = colors.asButtonColors(),
        shape = dimens.shape,
        contentPadding = dimens.contentPadding,
        content = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    space = dimens.contentSpacing,
                    alignment = Alignment.CenterHorizontally
                )
            ) {
                leading?.invoke(colors, dimens)

                content(colors, dimens)

                trailing?.invoke(colors, dimens)
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
            content = { colors, _ ->
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
            content = { colors, _ ->
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
                    imageVector = Icons.Default.Add,
                    tint = colors.contentColorFor(true),
                    contentDescription = null
                )
            },
            content = { colors, _ ->
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
                    imageVector = Icons.Default.Add,
                    tint = colors.contentColorFor(false),
                    contentDescription = null
                )
            },
            content = { colors, _ ->
                Text(
                    text = "Button",
                    color = colors.contentColorFor(true)
                )
            }
        )
    }
}
