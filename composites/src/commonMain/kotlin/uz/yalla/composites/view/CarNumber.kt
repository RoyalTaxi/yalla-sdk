package uz.yalla.composites.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import uz.yalla.core.settings.LocaleKind
import uz.yalla.design.theme.System
import uz.yalla.resources.Res
import uz.yalla.resources.img_flag_uz_square

data class CarNumberState(
    val code: String,
    val number: List<String>,
)

@Immutable
data class CarNumberColors(
    val container: Color,
    val border: Color,
    val text: Color,
    val countryCode: Color,
)

@Immutable
data class CarNumberDimens(
    val height: Dp,
    val cornerRadius: Dp,
    val borderWidth: Dp,
    val dotSize: Dp,
)

@Composable
fun CarNumber(
    state: CarNumberState,
    modifier: Modifier = Modifier,
    numberStyle: TextStyle = System.font.custom.carNumber,
    colors: CarNumberColors = CarNumberDefaults.colors(),
    dimens: CarNumberDimens = CarNumberDefaults.dimens(),
) {
    val scale = remember(dimens.height) { dimens.height.value / CarNumberDefaults.BASE_HEIGHT }

    fun Dp.scaled(): Dp = (value * scale).dp

    fun TextUnit.scaled(): TextUnit = (value * scale).sp

    val baseFontSize = numberStyle.fontSize
    val baseLineHeight = numberStyle.lineHeight

    Card(
        modifier = modifier,
        border = BorderStroke(dimens.borderWidth.scaled(), colors.border),
        colors = CardDefaults.cardColors(colors.container),
        shape = RoundedCornerShape(dimens.cornerRadius.scaled()),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(IntrinsicSize.Min).padding(horizontal = 4.dp.scaled()),
        ) {
            Box(
                modifier =
                    Modifier.size(dimens.dotSize.scaled()).background(
                        shape = CircleShape,
                        color = colors.text,
                    ),
            )

            Text(
                text = state.code,
                color = colors.text,
                style =
                    numberStyle.copy(
                        fontSize = baseFontSize.scaled(),
                        lineHeight = baseLineHeight.scaled(),
                    ),
                modifier = Modifier.padding(3.dp.scaled()),
            )

            VerticalDivider(
                thickness = dimens.borderWidth.scaled(),
                color = colors.border,
            )

            Text(
                text = state.number.joinToString(" "),
                color = colors.text,
                style =
                    numberStyle.copy(
                        fontSize = baseFontSize.scaled(),
                        lineHeight = baseLineHeight.scaled(),
                    ),
                modifier = Modifier.padding(3.dp.scaled()),
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 3.dp.scaled()),
            ) {
                Image(
                    painter = painterResource(Res.drawable.img_flag_uz_square),
                    contentDescription = null,
                    modifier = Modifier.height(4.dp.scaled()),
                )

                Text(
                    text = LocaleKind.Uz.name,
                    color = colors.countryCode,
                    style =
                        numberStyle.copy(
                            fontSize = 8.sp.scaled(),
                            lineHeight = 8.sp.scaled(),
                        ),
                )
            }

            Spacer(modifier = Modifier.width(3.dp.scaled()))

            Box(
                modifier =
                    Modifier.size(dimens.dotSize.scaled()).background(
                        shape = CircleShape,
                        color = colors.text,
                    ),
            )
        }
    }
}

object CarNumberDefaults {
    internal const val BASE_HEIGHT: Float = 24f

    @Composable
    fun colors(
        container: Color = Color.White,
        border: Color = System.color.border.filled,
        text: Color = Color.Black,
        countryCode: Color = Color(0xFF029BB7),
    ): CarNumberColors = CarNumberColors(
        container = container,
        border = border,
        text = text,
        countryCode = countryCode,
    )

    fun dimens(
        height: Dp = 24.dp,
        cornerRadius: Dp = 4.dp,
        borderWidth: Dp = 1.dp,
        dotSize: Dp = 2.dp,
    ): CarNumberDimens = CarNumberDimens(
        height = height,
        cornerRadius = cornerRadius,
        borderWidth = borderWidth,
        dotSize = dotSize,
    )
}
