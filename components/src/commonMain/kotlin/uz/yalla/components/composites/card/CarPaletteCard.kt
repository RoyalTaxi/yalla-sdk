package uz.yalla.components.composites.card

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.Res
import uz.yalla.resources.img_flag_uz_square

@Immutable
data class CarPaletteCardDimens(
    val height: Dp,
    val cornerRadius: Dp,
    val borderWidth: Dp,
    val dotSize: Dp
)

object CarPaletteCardDefaults {
    internal const val BASE_HEIGHT: Float = 24f

    @Composable
    fun dimens(
        height: Dp = 24.dp,
        cornerRadius: Dp = 4.dp,
        borderWidth: Dp = 1.dp,
        dotSize: Dp = 2.dp
    ) = CarPaletteCardDimens(
        height = height,
        cornerRadius = cornerRadius,
        borderWidth = borderWidth,
        dotSize = dotSize
    )
}

@Composable
fun CarPaletteCard(
    code: String,
    number: List<String>,
    modifier: Modifier = Modifier,
    dimens: CarPaletteCardDimens = CarPaletteCardDefaults.dimens()
) {
    val scale = remember(dimens.height) { dimens.height.value / CarPaletteCardDefaults.BASE_HEIGHT }

    fun Dp.scaled(): Dp = (value * scale).dp

    fun TextUnit.scaled(): TextUnit = (value * scale).sp

    val numberStyle = System.font.custom.carNumber
    val baseFontSize = numberStyle.fontSize
    val baseLineHeight = numberStyle.lineHeight

    Card(
        modifier = modifier,
        border = BorderStroke(dimens.borderWidth.scaled(), System.color.border.filled),
        colors = CardDefaults.cardColors(Color.White),
        shape = RoundedCornerShape(dimens.cornerRadius.scaled())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .padding(horizontal = 4.dp.scaled())
        ) {
            Box(
                modifier = Modifier
                    .size(dimens.dotSize.scaled())
                    .background(shape = CircleShape, color = Color.Black)
            )

            Text(
                text = code,
                color = Color.Black,
                style = numberStyle.copy(
                    fontSize = baseFontSize.scaled(),
                    lineHeight = baseLineHeight.scaled()
                ),
                modifier = Modifier.padding(3.dp.scaled())
            )

            VerticalDivider(
                thickness = dimens.borderWidth.scaled(),
                color = System.color.border.filled
            )

            Text(
                text = number.joinToString(" "),
                color = Color.Black,
                style = numberStyle.copy(
                    fontSize = baseFontSize.scaled(),
                    lineHeight = baseLineHeight.scaled()
                ),
                modifier = Modifier.padding(3.dp.scaled())
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 3.dp.scaled())
            ) {
                Image(
                    painter = painterResource(Res.drawable.img_flag_uz_square),
                    contentDescription = null,
                    modifier = Modifier.height(4.dp.scaled())
                )

                Text(
                    text = "Uz",
                    color = Color(0xFF029BB7),
                    style = numberStyle.copy(
                        fontSize = 8.sp.scaled(),
                        lineHeight = 8.sp.scaled()
                    )
                )
            }

            Spacer(modifier = Modifier.width(3.dp.scaled()))

            Box(
                modifier = Modifier
                    .size(dimens.dotSize.scaled())
                    .background(shape = CircleShape, color = Color.Black)
            )
        }
    }
}

@Composable
fun CarPaletteCard(
    stateNumber: String,
    modifier: Modifier = Modifier,
    dimens: CarPaletteCardDimens = CarPaletteCardDefaults.dimens()
) {
    val code = remember(stateNumber) { stateNumber.take(2) }
    val number = remember(stateNumber) { parsePlateBody(stateNumber) }
    CarPaletteCard(
        code = code,
        number = number,
        modifier = modifier,
        dimens = dimens
    )
}

private val PlateBodyRegex = Regex("(\\d+|[A-Za-z]+)")

private fun parsePlateBody(stateNumber: String): List<String> {
    if (stateNumber.length <= 2) return emptyList()
    return PlateBodyRegex
        .findAll(stateNumber.substring(2))
        .map { it.value }
        .toList()
}

@Preview
@Composable
private fun Preview() = YallaTheme {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CarPaletteCard(
            code = "01",
            number = listOf("A", "123", "BC")
        )

        CarPaletteCard(
            code = "10",
            number = listOf("M", "777", "MM"),
            dimens = CarPaletteCardDefaults.dimens(height = 48.dp)
        )

        CarPaletteCard(
            code = "30",
            number = listOf("A", "001", "AA"),
            dimens = CarPaletteCardDefaults.dimens(height = 64.dp)
        )
    }
}
