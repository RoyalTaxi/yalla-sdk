package uz.yalla.platform.indicator

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
expect fun NativeLoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified
)
