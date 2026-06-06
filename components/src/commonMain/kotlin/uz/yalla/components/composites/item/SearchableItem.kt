package uz.yalla.components.composites.item

import androidx.compose.runtime.Immutable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.icons.FocusLocation
import uz.yalla.resources.icons.YallaIcons

@Immutable
data class SearchableItemModel(
    val id: String,
    val title: String,
    val description: String? = null,
    val badge: String? = null
)

@Composable
fun SearchableItem(
    title: String,
    description: String? = null,
    badge: String? = null,
    leadingPainter: Painter? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.Transparent,
        shape = RectangleShape,
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(
                start = 20.dp,
                top = 10.dp,
                end = 30.dp,
                bottom = 10.dp
            )
        ) {
            leadingPainter?.let { painter ->
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.size(44.dp)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = title,
                        color = System.color.text.base,
                        style = System.font.body.base.bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    badge?.let {
                        Text(
                            text = badge,
                            color = System.color.text.subtle,
                            style = System.font.body.small.medium
                        )
                    }
                }

                description?.let { desc ->
                    Text(
                        text = desc,
                        color = System.color.text.subtle,
                        style = System.font.body.small.medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() = YallaTheme {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.background(System.color.background.base)
    ) {
        SearchableItem(
            title = "Andijan region",
            description = "Place for youth affeice and growth under ministory of Information Technology and something something something",
            badge = "10 km",
            leadingPainter = rememberVectorPainter(YallaIcons.FocusLocation),
            onClick = {}
        )

        SearchableItem(
            title = "Andijan region",
            description = "Launch Bar",
            badge = "10 km",
            leadingPainter = rememberVectorPainter(YallaIcons.FocusLocation),
            onClick = {}
        )

        SearchableItem(
            title = "Andijan region, Andijan region, Andijan region, Andijan region",
            description = "Launch Bar",
            badge = "10 km",
            leadingPainter = rememberVectorPainter(YallaIcons.FocusLocation),
            onClick = {}
        )
    }
}
