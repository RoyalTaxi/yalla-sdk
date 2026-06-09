package uz.yalla.components.feedback

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import uz.yalla.components.primitives.indicator.LoadingIndicator
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme

@Immutable
data class LoadingDialogColors(
    val container: Color,
    val indicator: Color
)

@Immutable
data class LoadingDialogDimens(
    val shape: Shape,
    val contentPadding: Dp,
    val indicatorSize: Dp
)

object LoadingDialogDefaults {
    @Composable
    fun colors(
        container: Color = System.color.background.base,
        indicator: Color = System.color.background.brand
    ) = LoadingDialogColors(
        container = container,
        indicator = indicator
    )

    @Composable
    fun dimens(
        shape: Shape = CircleShape,
        contentPadding: Dp = 20.dp,
        indicatorSize: Dp = 40.dp
    ) = LoadingDialogDimens(
        shape = shape,
        contentPadding = contentPadding,
        indicatorSize = indicatorSize
    )
}

@Composable
fun LoadingDialog(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    colors: LoadingDialogColors = LoadingDialogDefaults.colors(),
    dimens: LoadingDialogDimens = LoadingDialogDefaults.dimens()
) {
    if (!isVisible) return

    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            shape = dimens.shape,
            color = colors.container,
            modifier = modifier
        ) {
            LoadingIndicator(
                color = colors.indicator,
                modifier = Modifier
                    .padding(dimens.contentPadding)
                    .size(dimens.indicatorSize)
            )
        }
    }
}

@Preview
@Composable
private fun Preview() = YallaTheme {
    Surface(
        shape = CircleShape,
        color = System.color.background.base
    ) {
        Box(
            modifier = Modifier
                .padding(20.dp)
                .size(40.dp)
        )
    }
}
