package uz.yalla.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme

@Composable
fun CatalogFrame(
    width: Int = 420,
    dark: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(24.dp),
    content: @Composable () -> Unit,
) {
    YallaTheme(isDark = dark) {
        Surface(
            color = System.color.background.base,
            contentColor = System.color.text.base,
            modifier =
                Modifier
                    .width(width.dp)
                    .border(1.dp, System.color.border.disabled, RoundedCornerShape(8.dp)),
        ) {
            Box(Modifier.padding(contentPadding)) {
                content()
            }
        }
    }
}

@Composable
fun CatalogStack(
    modifier: Modifier = Modifier,
    spacing: Int = 12,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.dp),
    ) {
        content()
    }
}

@Composable
fun CatalogTitle(
    title: String,
    description: String? = null,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = System.font.body.large.bold,
            color = System.color.text.base,
        )
        if (description != null) {
            Text(
                text = description,
                style = System.font.body.caption,
                color = System.color.text.subtle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun CatalogIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = System.color.icon.base,
) {
    Icon(
        imageVector = imageVector,
        contentDescription = null,
        tint = tint,
        modifier = modifier.size(22.dp),
    )
}

@Composable
fun IconTile(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = System.color.icon.base,
) {
    Box(
        modifier =
            modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(System.color.background.secondary),
        contentAlignment = Alignment.Center,
    ) {
        CatalogIcon(imageVector = imageVector, tint = tint)
    }
}

@Composable
fun AvatarCircle(
    label: String = "YA",
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Color(0xff7f52ff), Color(0xffff6b8a)))),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = Color.White,
            style = System.font.title.base,
        )
    }
}

@Composable
fun InteractionCounter(
    label: String,
    count: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(System.color.background.secondary)
                .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = System.font.body.caption, color = System.color.text.subtle)
        Text(count.toString(), style = System.font.body.base.bold, color = System.color.text.base)
    }
}

@Composable
fun Separator() {
    Spacer(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(System.color.border.disabled),
    )
}
