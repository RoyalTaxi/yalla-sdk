package uz.yalla.components.button

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.icons.Checked
import uz.yalla.resources.icons.FocusDestination
import uz.yalla.resources.icons.FocusOrigin
import uz.yalla.resources.icons.Unchecked
import uz.yalla.resources.icons.YallaIcons

@Immutable
data class SelectableButtonColors(
    val textColor: Color,
    val containerColor: Color,
    val selectedTextColor: Color,
    val selectedContainerColor: Color
) {
    @Composable
    fun textColorFor(selected: Boolean) =
        if (selected) selectedTextColor else textColor

    @Composable
    fun containerColorFor(selected: Boolean) =
        if (selected) selectedContainerColor else containerColor
}

@Immutable
data class SelectableButtonDimens(
    val shape: Shape,
    val contentSpacing: Dp,
    val contentPadding: PaddingValues,
    val iconSize: Dp,
    val selectIconSize: Dp
)

@Immutable
data class SelectableButtonStyles(
    val textStyle: TextStyle
)

object SelectableButtonDefaults {
    @Composable
    fun colors(
        textColor: Color = System.color.text.subtle,
        containerColor: Color = System.color.background.secondary,
        selectedTextColor: Color = System.color.text.base,
        selectedContainerColor: Color = System.color.background.base
    ) = SelectableButtonColors(
        textColor = textColor,
        containerColor = containerColor,
        selectedTextColor = selectedTextColor,
        selectedContainerColor = selectedContainerColor
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(System.radius.l),
        contentSpacing: Dp = System.space.scale.xs,
        contentPadding: PaddingValues = PaddingValues(
            start = System.space.scale.xl,
            top = System.space.scale.m,
            end = System.space.scale.m,
            bottom = System.space.scale.m
        ),
        iconSize: Dp = 24.dp,
        selectIconSize: Dp = 24.dp
    ) = SelectableButtonDimens(
        shape = shape,
        contentSpacing = contentSpacing,
        contentPadding = contentPadding,
        iconSize = iconSize,
        selectIconSize = selectIconSize
    )

    @Composable
    fun styles(
        textStyle: TextStyle = System.font.body.base.medium
    ) = SelectableButtonStyles(
        textStyle = textStyle
    )
}

@Composable
fun SelectableButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingPainter: Painter? = null,
    selectedPainter: Painter? = rememberVectorPainter(YallaIcons.Checked),
    unselectedPainter: Painter? = rememberVectorPainter(YallaIcons.Unchecked),
    colors: SelectableButtonColors = SelectableButtonDefaults.colors(),
    dimens: SelectableButtonDimens = SelectableButtonDefaults.dimens(),
    styles: SelectableButtonStyles = SelectableButtonDefaults.styles()
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = dimens.shape,
        color = colors.containerColorFor(selected)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.contentSpacing),
            modifier = Modifier.padding(dimens.contentPadding)
        ) {
            leadingPainter?.let { painter ->
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.size(dimens.iconSize)
                )
            }

            Text(
                text = text,
                color = colors.textColorFor(selected),
                style = styles.textStyle,
                modifier = Modifier.weight(1f)
            )

            if (selected) selectedPainter?.let { painter ->
                Image(
                    contentDescription = null,
                    modifier = Modifier.size(dimens.selectIconSize),
                    painter = painter
                )
            } else unselectedPainter?.let { painter ->
                Image(
                    contentDescription = null,
                    modifier = Modifier.size(dimens.selectIconSize),
                    painter = painter
                )
            }
        }
    }
}


@Preview
@Composable
private fun Preview() = YallaTheme {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SelectableButton(
                text = "Male",
                selected = true,
                onClick = {},
                modifier = Modifier.weight(1f)
            )

            SelectableButton(
                text = "Female",
                selected = false,
                onClick = {},
                modifier = Modifier.weight(1f)
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SelectableButton(
                text = "Space on baggage",
                selected = true,
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                dimens = SelectableButtonDefaults.dimens(
                    contentPadding = PaddingValues(
                        start = System.space.scale.l,
                        top = System.space.scale.m,
                        end = System.space.scale.m,
                        bottom = System.space.scale.m
                    )
                )
            )

            SelectableButton(
                text = "Space on baggage",
                selected = false,
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                dimens = SelectableButtonDefaults.dimens(
                    contentPadding = PaddingValues(
                        start = System.space.scale.l,
                        top = System.space.scale.m,
                        end = System.space.scale.m,
                        bottom = System.space.scale.m
                    )
                )
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SelectableButton(
                text = "Baggage",
                selected = true,
                onClick = {},
                modifier = Modifier.weight(1f),
                leadingPainter = rememberVectorPainter(YallaIcons.FocusOrigin),
                dimens = SelectableButtonDefaults.dimens(
                    contentPadding = PaddingValues(
                        start = System.space.scale.m,
                        top = System.space.scale.s,
                        end = System.space.scale.m,
                        bottom = System.space.scale.s
                    )
                )
            )

            SelectableButton(
                text = "Conditioner",
                selected = false,
                onClick = {},
                modifier = Modifier.weight(1f),
                leadingPainter = rememberVectorPainter(YallaIcons.FocusDestination),
                dimens = SelectableButtonDefaults.dimens(
                    contentPadding = PaddingValues(
                        start = System.space.scale.m,
                        top = System.space.scale.s,
                        end = System.space.scale.m,
                        bottom = System.space.scale.s
                    )
                )
            )
        }
    }
}
