package uz.yalla.components.primitives.field

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme

@Composable
fun PinField(
    value: String,
    onValueChange: (String) -> Unit,
    length: Int,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    error: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(error) {
        if (error.not()) {
            shakeOffset.snapTo(0f)
            return@LaunchedEffect
        }

        repeat(3) {
            shakeOffset.animateTo(10f, tween(40))
            shakeOffset.animateTo(-10f, tween(40))
        }

        shakeOffset.animateTo(0f, tween(40))
    }

    BasicTextField(
        value = value,
        onValueChange = { onValueChange(it.filter(Char::isDigit).take(length)) },
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { translationX = shakeOffset.value }
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        decorationBox = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(length) { index ->
                    PinBox(
                        value = value.getOrNull(index),
                        error = error,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    )
}

@Composable
private fun PinBox(
    value: Char?,
    error: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor = when {
        error -> System.color.text.red
        value != null -> System.color.border.filled
        else -> System.color.border.disabled
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        value?.let {
            Text(
                text = it.toString(),
                style = System.font.title.large,
                color = if (error) System.color.text.red else System.color.text.base,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
private fun Preview() = YallaTheme {
    var value by remember { mutableStateOf("12") }
    PinField(
        value = value,
        onValueChange = { value = it },
        length = 5,
        modifier = Modifier.padding(16.dp)
    )
}
