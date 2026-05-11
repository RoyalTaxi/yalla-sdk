package uz.yalla.components.button

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import uz.yalla.core.profile.GenderKind
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.Res
import uz.yalla.resources.icons.Checked
import uz.yalla.resources.icons.Unchecked
import uz.yalla.resources.icons.YallaIcons
import uz.yalla.resources.register_gender_female
import uz.yalla.resources.register_gender_male

private val GenderKind.resource: StringResource?
    get() = when (this) {
        GenderKind.Male -> Res.string.register_gender_male
        GenderKind.Female -> Res.string.register_gender_female
        GenderKind.NotSelected -> null
    }

@Immutable
data class GenderButtonColors(
    val textColor: Color,
    val containerColor: Color
)

@Immutable
data class GenderButtonDimens(
    val shape: Shape,
    val contentPadding: PaddingValues,
    val iconSize: Dp
)

@Immutable
data class GenderButtonStyles(
    val textStyle: TextStyle
)

object GenderButtonDefaults {
    @Composable
    fun colors(
        textColor: Color = System.color.text.base,
        containerColor: Color = System.color.background.secondary
    ) = GenderButtonColors(
        textColor = textColor,
        containerColor = containerColor
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(System.radius.l),
        contentPadding: PaddingValues = PaddingValues(
            start = System.space.scale.xl,
            top = System.space.scale.m,
            end = System.space.scale.m,
            bottom = System.space.scale.m
        ),
        iconSize: Dp = 24.dp
    ) = GenderButtonDimens(
        shape = shape,
        contentPadding = contentPadding,
        iconSize = iconSize
    )

    @Composable
    fun styles(
        textStyle: TextStyle = System.font.body.base.medium
    ) = GenderButtonStyles(
        textStyle = textStyle
    )
}

@Composable
fun GenderButton(
    gender: GenderKind,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: GenderButtonColors = GenderButtonDefaults.colors(),
    dimens: GenderButtonDimens = GenderButtonDefaults.dimens(),
    styles: GenderButtonStyles = GenderButtonDefaults.styles()
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = dimens.shape,
        color = colors.containerColor
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(dimens.contentPadding)
        ) {
            gender.resource?.let { gender ->
                Text(
                    text = stringResource(gender),
                    color = colors.textColor,
                    style = styles.textStyle
                )
            }

            Image(
                contentDescription = null,
                modifier = Modifier.size(dimens.iconSize),
                painter =
                    if (selected) {
                        rememberVectorPainter(YallaIcons.Checked)
                    } else {
                        rememberVectorPainter(YallaIcons.Unchecked)
                    }
            )
        }
    }
}


@Preview
@Composable
private fun Preview() = YallaTheme {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GenderButton(
            gender = GenderKind.Male,
            selected = true,
            onClick = {}
        )

        GenderButton(
            gender = GenderKind.Female,
            selected = false,
            onClick = {}
        )

        GenderButton(
            gender = GenderKind.Male,
            selected = true,
            onClick = {}
        )

        GenderButton(
            gender = GenderKind.Female,
            selected = false,
            onClick = {}
        )
    }
}
