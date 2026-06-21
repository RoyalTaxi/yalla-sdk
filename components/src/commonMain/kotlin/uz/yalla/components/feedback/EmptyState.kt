package uz.yalla.components.feedback

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import uz.yalla.design.image.ThemedImage
import uz.yalla.design.image.rememberThemedPainter
import uz.yalla.design.theme.System

@Composable
public fun EmptyState(
    image: ThemedImage,
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = rememberThemedPainter(image),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )

        if (title != null) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                style = System.font.title.base,
                color = System.color.text.base,
                textAlign = TextAlign.Center
            )
        }

        if (description != null) {
            Spacer(modifier = Modifier.height(if (title != null) 8.dp else 16.dp))

            Text(
                text = description,
                style = System.font.body.base.regular,
                color = System.color.text.subtle,
                textAlign = TextAlign.Center
            )
        }
    }
}
