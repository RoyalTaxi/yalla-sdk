package uz.yalla.components.composites.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import uz.yalla.components.primitives.toggle.Toggle
import uz.yalla.design.theme.System

@Composable
fun ToggleableItem(
    title: String,
    description: String? = null,
    painter: Painter? = null,
    checked: Boolean,
    onToggle: (checked: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color.Transparent,
        onClick = { onToggle(checked.not()) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(
                vertical = 4.dp,
                horizontal = 20.dp
            )
        ) {
            painter?.let {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = System.color.background.secondary
                ) {
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(16.dp)
                            .size(24.dp)
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = System.color.text.base,
                    style = System.font.body.base.bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                description?.let { desc ->
                    Text(
                        text = desc,
                        color = System.color.text.subtle,
                        style = System.font.body.small.medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Toggle(
                checked = checked,
                onCheckedChange = onToggle
            )
        }
    }
}

@Composable
fun ToggleableItem(
    title: String,
    description: String? = null,
    imageUrl: String?,
    checked: Boolean,
    onToggle: (checked: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val painter = imageUrl?.let { rememberAsyncImagePainter(model = it) }
    val painterState by painter?.state?.collectAsState() ?: return ToggleableItem(
        title = title,
        description = description,
        painter = null,
        checked = checked,
        onToggle = onToggle,
        modifier = modifier
    )

    ToggleableItem(
        title = title,
        description = description,
        painter = painter.takeIf { painterState is AsyncImagePainter.State.Success },
        checked = checked,
        onToggle = onToggle,
        modifier = modifier
    )
}
