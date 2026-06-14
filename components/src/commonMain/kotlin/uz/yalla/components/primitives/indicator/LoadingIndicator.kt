package uz.yalla.components.primitives.indicator

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
public expect fun LoadingIndicator(
    color: Color = Color.Unspecified,
    modifier: Modifier = Modifier
)
