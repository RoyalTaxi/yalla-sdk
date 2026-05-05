package uz.yalla.composites.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Single location within a route.
 *
 * @property isOrigin Whether this is an origin point. The first item in the list
 *   is always treated as origin regardless of this flag.
 */
data class RouteLocation(
    val name: String,
    val isOrigin: Boolean = false
)

/**
 * Dimension configuration for [RouteView].
 */
@Immutable
data class RouteViewDimens(
    val itemSpacing: Dp
)

/**
 * Default configuration values for [RouteView].
 */
object RouteViewDefaults {
    /**
     * Creates default dimensions.
     */
    fun dimens(itemSpacing: Dp = 8.dp): RouteViewDimens =
        RouteViewDimens(
            itemSpacing = itemSpacing
        )
}

/**
 * Vertical list of [LocationPoint] items showing an origin-to-destination route.
 *
 * The first location (or any location with [RouteLocation.isOrigin] = true) is rendered
 * with the [originIcon] and bold styling; all others use the [destinationIcon] and
 * caption styling.
 *
 * ## Usage
 *
 * ```kotlin
 * RouteView(
 *     locations = listOf(
 *         RouteLocation("Home", isOrigin = true),
 *         RouteLocation("Work"),
 *     ),
 *     originIcon = painterResource(Res.drawable.ic_origin),
 *     destinationIcon = painterResource(Res.drawable.ic_destination),
 * )
 * ```
 *
 * @param locations Ordered list of route locations. First item is treated as origin.
 * @param dimens Dimension configuration, defaults to [RouteViewDefaults.dimens].
 *
 * @see LocationPoint
 * @see RouteLocation
 * @see RouteViewDefaults
 */
@Composable
fun RouteView(
    locations: List<RouteLocation>,
    originIcon: Painter,
    destinationIcon: Painter,
    modifier: Modifier = Modifier,
    dimens: RouteViewDimens = RouteViewDefaults.dimens()
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimens.itemSpacing)
    ) {
        locations.forEachIndexed { index, location ->
            val isOrigin = index == 0 || location.isOrigin

            LocationPoint(
                icon = if (isOrigin) originIcon else destinationIcon,
                label = location.name,
                labelStyle = if (isOrigin) System.font.body.small.bold else System.font.body.caption,
                colors =
                    if (isOrigin) {
                        LocationPointDefaults.colors()
                    } else {
                        LocationPointDefaults.destinationColors()
                    }
            )
        }
    }
}
