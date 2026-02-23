package uz.yalla.components.composite.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * State for [VehiclePlate].
 *
 * @property plateNumber Main plate number.
 * @property region Optional region code.
 */
data class VehiclePlateState(
    val plateNumber: String,
    val region: String? = null,
)

/**
 * Vehicle license plate display.
 *
 * Shows car number in standard plate format.
 *
 * ## Usage
 *
 * ```kotlin
 * VehiclePlate(
 *     state = VehiclePlateState(
 *         plateNumber = "01 A 123 AA",
 *     ),
 * )
 * ```
 *
 * ## With Region
 *
 * ```kotlin
 * VehiclePlate(
 *     state = VehiclePlateState(
 *         plateNumber = "A 123 AA",
 *         region = "01",
 *     ),
 * )
 * ```
 *
 * @param state Plate state with number and optional region.
 * @param modifier Applied to component.
 * @param colors Color configuration, defaults to [VehiclePlateDefaults.colors].
 * @param dimens Dimension configuration, defaults to [VehiclePlateDefaults.dimens].
 *
 * @see VehiclePlateState for state configuration
 * @see VehiclePlateDefaults for default values
 */
@Composable
fun VehiclePlate(
    state: VehiclePlateState,
    modifier: Modifier = Modifier,
    colors: VehiclePlateDefaults.VehiclePlateColors = VehiclePlateDefaults.colors(),
    dimens: VehiclePlateDefaults.VehiclePlateDimens = VehiclePlateDefaults.dimens(),
) {
    Row(
        modifier =
            modifier
                .height(dimens.height)
                .border(
                    border =
                        BorderStroke(
                            width = dimens.borderWidth,
                            color = colors.border,
                        ),
                    shape = dimens.shape,
                ).background(
                    color = colors.container,
                    shape = dimens.shape,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (state.region != null) {
            Box(
                modifier =
                    Modifier
                        .padding(
                            start = dimens.regionHorizontalPadding,
                            end = dimens.regionHorizontalPadding,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = state.region,
                    style = System.font.body.small.bold,
                    color = colors.text,
                )
            }

            Box(
                modifier =
                    Modifier
                        .width(dimens.dividerWidth)
                        .height(dimens.height - dimens.dividerVerticalPadding * 2)
                        .background(colors.divider),
            )
        }

        Text(
            text = state.plateNumber,
            style = System.font.body.base.bold,
            color = colors.text,
            modifier =
                Modifier.padding(
                    horizontal = dimens.contentHorizontalPadding,
                ),
        )
    }
}

/**
 * Default values for [VehiclePlate].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 */
object VehiclePlateDefaults {
    /**
     * Color configuration for [VehiclePlate].
     *
     * @param container Background color.
     * @param text Text color.
     * @param border Border color.
     * @param divider Region divider color.
     */
    data class VehiclePlateColors(
        val container: Color,
        val text: Color,
        val border: Color,
        val divider: Color,
    )

    @Composable
    fun colors(
        container: Color = System.color.backgroundBase,
        text: Color = System.color.textBase,
        border: Color = System.color.borderDisabled,
        divider: Color = System.color.borderDisabled,
    ): VehiclePlateColors =
        VehiclePlateColors(
            container = container,
            text = text,
            border = border,
            divider = divider,
        )

    /**
     * Dimension configuration for [VehiclePlate].
     *
     * @param shape Plate corner shape.
     * @param height Plate height.
     * @param borderWidth Border width.
     * @param regionHorizontalPadding Region section horizontal padding.
     * @param contentHorizontalPadding Content horizontal padding.
     * @param dividerWidth Region divider width.
     * @param dividerVerticalPadding Region divider vertical padding.
     */
    data class VehiclePlateDimens(
        val shape: Shape,
        val height: Dp,
        val borderWidth: Dp,
        val regionHorizontalPadding: Dp,
        val contentHorizontalPadding: Dp,
        val dividerWidth: Dp,
        val dividerVerticalPadding: Dp,
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(4.dp),
        height: Dp = 32.dp,
        borderWidth: Dp = 1.dp,
        regionHorizontalPadding: Dp = 6.dp,
        contentHorizontalPadding: Dp = 8.dp,
        dividerWidth: Dp = 1.dp,
        dividerVerticalPadding: Dp = 4.dp,
    ): VehiclePlateDimens =
        VehiclePlateDimens(
            shape = shape,
            height = height,
            borderWidth = borderWidth,
            regionHorizontalPadding = regionHorizontalPadding,
            contentHorizontalPadding = contentHorizontalPadding,
            dividerWidth = dividerWidth,
            dividerVerticalPadding = dividerVerticalPadding,
        )
}

@Preview
@Composable
private fun VehiclePlatePreview() {
    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp)
    ) {
        VehiclePlate(
            state =
                VehiclePlateState(
                    plateNumber = "A 123 AA",
                    region = "01",
                ),
        )
    }
}
