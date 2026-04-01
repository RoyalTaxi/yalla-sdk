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

/**
 * State for [CarNumber] display.
 *
 * @property code Region code portion of the license plate (e.g., "01").
 * @property number Number segments of the license plate (e.g., ["A", "123", "BC"]).
 * @since 0.0.1
 */
data class CarNumberState(
    val code: String,
    val number: List<String>,
)

/**
 * Color configuration for [CarNumber].
 *
 * @param container Plate background color.
 * @param border Plate border color.
 * @param text Text and dot color.
 * @param countryCode Country code text color below the flag.
 * @since 0.0.1
 */
@Immutable
data class CarNumberColors(
    val container: Color,
    val border: Color,
    val text: Color,
    val countryCode: Color,
)

/**
 * Dimension configuration for [CarNumber].
 *
 * All dimensions scale proportionally based on [height] relative to the [CarNumberDefaults.BASE_HEIGHT].
 *
 * @param height Overall plate height. Controls scaling of all internal dimensions.
 * @param cornerRadius Corner radius of the plate card.
 * @param borderWidth Border stroke width.
 * @param dotSize Diameter of the decorative dots at plate edges.
 * @since 0.0.1
 */
@Immutable
data class CarNumberDimens(
    val height: Dp,
    val cornerRadius: Dp,
    val borderWidth: Dp,
    val dotSize: Dp,
)

/**
 * Uzbekistan-style car license plate display.
 *
 * Renders a styled card mimicking the Uzbekistan vehicle plate format: region code,
 * number segments, country flag, and "Uz" label. All internal dimensions scale
 * proportionally with [CarNumberDimens.height].
 *
 * ## Usage
 *
 * ```kotlin
 * CarNumber(
 *     state = CarNumberState(
 *         code = "01",
 *         number = listOf("A", "123", "BC"),
 *     ),
 * )
 * ```
 *
 * @param state License plate data containing code and number segments.
 * @param modifier Applied to the root card.
 * @param numberStyle Text style for plate characters, defaults to a custom monospaced car number font.
 * @param colors Color configuration, defaults to [CarNumberDefaults.colors].
 * @param dimens Dimension configuration, defaults to [CarNumberDefaults.dimens].
 *
 * @see CarNumberState
 * @see CarNumberDefaults
 * @since 0.0.1
 */
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

/**
 * Default configuration values for [CarNumber].
 *
 * @since 0.0.1
 */
object CarNumberDefaults {
    internal const val BASE_HEIGHT: Float = 24f

    /**
     * Creates theme-aware default colors.
     */
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

    /**
     * Creates default dimensions.
     */
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
