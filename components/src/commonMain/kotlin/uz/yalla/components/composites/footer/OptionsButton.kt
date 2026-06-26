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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import uz.yalla.design.theme.System
import uz.yalla.resources.Res
import uz.yalla.resources.icons.Expand
import uz.yalla.resources.icons.Options
import uz.yalla.resources.icons.XInSquare
import uz.yalla.resources.icons.YallaIcons
import uz.yalla.resources.order_comment_title
import uz.yalla.resources.order_services_unavailable

@Composable
public fun OptionsButton(
    isExpanded: Boolean,
    hasInvalidServices: Boolean,
    badgeCount: Int,
    hasContent: Boolean,
    onClick: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val optionsLabel =
        if (hasInvalidServices) {
            stringResource(Res.string.order_services_unavailable)
        } else {
            stringResource(Res.string.order_comment_title)
        }
    Button(
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(System.color.background.secondary),
        contentPadding = PaddingValues(0.dp),
        modifier =
            modifier
                .size(60.dp)
                .semantics { contentDescription = optionsLabel },
        onClick = {
            if (hasInvalidServices) {
                onClear()
            } else {
                onClick()
            }
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter =
                    when {
                        hasInvalidServices -> rememberVectorPainter(YallaIcons.XInSquare)
                        isExpanded -> rememberVectorPainter(YallaIcons.Expand)
                        else -> rememberVectorPainter(YallaIcons.Options)
                    },
                contentDescription = null,
                tint = System.color.icon.base
            )

            if (hasContent) {
                OptionsBadge(
                    count = badgeCount,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
        }
    }
}

@Composable
private fun OptionsBadge(
    count: Int,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .padding(4.dp)
                .clip(CircleShape)
                .background(System.color.icon.red)
                .size(18.dp)
    ) {
        Text(
            text = count.toString().takeIf { count != 0 }.orEmpty(),
            color = System.color.text.white,
            style = System.font.body.small.bold
        )
    }
}
