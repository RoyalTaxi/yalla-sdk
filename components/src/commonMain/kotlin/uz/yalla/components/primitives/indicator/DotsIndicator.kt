package uz.yalla.components.primitives.indicator

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

@Composable
public fun DotsIndicator(
    pageCount: Int,
    currentPage: Int,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            val width by animateDpAsState(if (isSelected) 24.dp else 10.dp)

            Box(
                modifier = Modifier
                    .size(width = width, height = 10.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) System.color.background.brand
                        else System.color.background.tertiary
                    )
                    .clickable(enabled = !isSelected) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onPageChange(index)
                    }
            )
        }
    }
}
