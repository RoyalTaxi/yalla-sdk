package uz.yalla.components.composite.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Data class representing a location in a route.
 *
 * @property name Location display name.
 * @property isOrigin Whether this is the origin point.
 */
data class RouteLocation(
    val name: String,
    val isOrigin: Boolean = false,
)

/**
 * State for [RouteView].
 *
 * @property locations List of route locations.
 * @property originIcon Icon for origin point.
 * @property destinationIcon Icon for destination points.
 */
data class RouteViewState(
    val locations: List<RouteLocation>,
    val originIcon: Painter,
    val destinationIcon: Painter,
)

/**
 * Route view showing origin and destination points.
 *
 * Displays a vertical list of locations with appropriate styling.
 *
 * ## Usage
 *
 * ```kotlin
 * RouteView(
 *     state = RouteViewState(
 *         locations = listOf(
 *             RouteLocation("123 Main Street", isOrigin = true),
 *             RouteLocation("456 Oak Avenue"),
 *         ),
 *         originIcon = painterResource(Res.drawable.ic_origin),
 *         destinationIcon = painterResource(Res.drawable.ic_destination),
 *     ),
 * )
 * ```
 *
 * @param state Route view state containing locations and icons.
 * @param modifier Applied to component.
 * @param dimens Dimension configuration, defaults to [RouteViewDefaults.dimens].
 *
 * @see RouteViewState for state configuration
 * @see RouteViewDefaults for default values
 */
@Composable
fun RouteView(
    state: RouteViewState,
    modifier: Modifier = Modifier,
    dimens: RouteViewDefaults.RouteViewDimens = RouteViewDefaults.dimens(),
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimens.itemSpacing),
    ) {
        state.locations.forEachIndexed { index, location ->
            val isOrigin = index == 0 || location.isOrigin

            LocationPoint(
                state =
                    LocationPointState(
                        icon = if (isOrigin) state.originIcon else state.destinationIcon,
                        label = location.name,
                    ),
                style =
                    if (isOrigin) {
                        LocationPointDefaults.originStyle()
                    } else {
                        LocationPointDefaults.destinationStyle()
                    },
            )
        }
    }
}

/**
 * Default configuration values for [RouteView].
 *
 * Provides defaults for [dimens] that can be overridden.
 */
object RouteViewDefaults {
    /**
     * Dimension configuration for [RouteView].
     *
     * @param itemSpacing Spacing between route locations.
     */
    data class RouteViewDimens(
        val itemSpacing: Dp,
    )

    @Composable
    fun dimens(itemSpacing: Dp = 8.dp) =
        RouteViewDimens(
            itemSpacing = itemSpacing,
        )
}

@Preview
@Composable
private fun RouteViewPreview() {
    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "123 Main Street",
                style = System.font.body.small.bold,
            )
            Text(
                text = "456 Oak Avenue",
                style = System.font.body.caption,
            )
        }
    }
}
