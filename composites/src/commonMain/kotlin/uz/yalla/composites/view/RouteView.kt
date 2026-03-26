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

data class RouteLocation(
    val name: String,
    val isOrigin: Boolean = false,
)

@Immutable
data class RouteViewDimens(
    val itemSpacing: Dp,
)

object RouteViewDefaults {

    fun dimens(
        itemSpacing: Dp = 8.dp,
    ): RouteViewDimens = RouteViewDimens(
        itemSpacing = itemSpacing,
    )
}

@Composable
fun RouteView(
    locations: List<RouteLocation>,
    originIcon: Painter,
    destinationIcon: Painter,
    modifier: Modifier = Modifier,
    dimens: RouteViewDimens = RouteViewDefaults.dimens(),
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimens.itemSpacing),
    ) {
        locations.forEachIndexed { index, location ->
            val isOrigin = index == 0 || location.isOrigin

            LocationPoint(
                icon = if (isOrigin) originIcon else destinationIcon,
                label = location.name,
                labelStyle = if (isOrigin) System.font.body.small.bold else System.font.body.caption,
                colors = if (isOrigin) {
                    LocationPointDefaults.colors()
                } else {
                    LocationPointDefaults.destinationColors()
                },
            )
        }
    }
}
