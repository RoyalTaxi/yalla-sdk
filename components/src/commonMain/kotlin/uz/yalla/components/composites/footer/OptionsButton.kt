package uz.yalla.components.composites.footer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.resources.icons.Expand
import uz.yalla.resources.icons.Options
import uz.yalla.resources.icons.YallaIcons

@Composable
public fun OptionsButton(
    isExpanded: Boolean,
    badgeCount: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(System.color.background.secondary),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier.size(60.dp),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter =
                    when {
                        isExpanded -> rememberVectorPainter(YallaIcons.Expand)
                        else -> rememberVectorPainter(YallaIcons.Options)
                    },
                contentDescription = null,
                tint = System.color.icon.base
            )

            badgeCount?.let {
                Badge(
                    count = badgeCount,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
        }
    }
}

@Composable
private fun Badge(
    count: Int?,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .padding(8.dp)
                .clip(CircleShape)
                .background(System.color.icon.red)
                .size(18.dp)
    ) {
        Text(
            text = count?.toString().takeIf { count != 0 }.orEmpty(),
            color = System.color.text.white,
            style = System.font.body.small.bold
        )
    }
}
