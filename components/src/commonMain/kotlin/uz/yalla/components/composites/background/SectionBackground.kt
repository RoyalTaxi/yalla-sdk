package uz.yalla.components.composites.background

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

@Composable
public fun SectionBackground(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = System.color.background.secondary,
        modifier = modifier
    ) {
        Column(content = content)
    }
}

@Composable
public fun SectionDivider(startIndent: Dp = 60.dp) {
    HorizontalDivider(
        color = System.color.border.disabled,
        thickness = 1.dp,
        modifier = Modifier.padding(start = startIndent)
    )
}
