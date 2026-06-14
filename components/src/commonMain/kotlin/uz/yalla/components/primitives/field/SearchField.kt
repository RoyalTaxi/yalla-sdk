package uz.yalla.components.primitives.field

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.icons.Flag
import uz.yalla.resources.icons.Location
import uz.yalla.resources.icons.YallaIcons

@Composable
public fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingPainter: Painter? = null,
    leading: (@Composable (focused: Boolean) -> Unit)? = null,
    trailingPainter: Painter? = null,
    onClickTrailingPainter: (() -> Unit)? = null,
    focusRequester: FocusRequester? = null,
    onFocusChanged: ((Boolean) -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = System.color.background.secondary,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(60.dp).padding(10.dp)
        ) {
            when {
                leading != null -> leading(isFocused)

                leadingPainter != null -> Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            color = if (isFocused) System.color.background.brand else Color.Transparent,
                            shape = RoundedCornerShape(11.dp)
                        )
                ) {
                    Image(
                        painter = leadingPainter,
                        contentDescription = null,
                        modifier = Modifier.size(42.dp)
                    )
                }
            }

            val selectionColors = TextSelectionColors(
                handleColor = System.color.text.link,
                backgroundColor = System.color.text.link.copy(.3f)
            )
            CompositionLocalProvider(LocalTextSelectionColors provides selectionColors) {
                var fieldValue by remember { mutableStateOf(TextFieldValue(value, TextRange(value.length))) }
                LaunchedEffect(value) {
                    if (fieldValue.text != value) {
                        fieldValue = fieldValue.copy(text = value, selection = TextRange(value.length))
                    }
                }
                BasicTextField(
                    value = fieldValue,
                    onValueChange = { fv ->
                        val textChanged = fv.text != fieldValue.text
                        fieldValue = fv
                        if (textChanged) onValueChange(fv.text)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .let { if (focusRequester != null) it.focusRequester(focusRequester) else it }
                        .let { m -> if (onFocusChanged != null) m.onFocusChanged { onFocusChanged(it.isFocused) } else m },
                    textStyle = System.font.body.base.bold.copy(color = System.color.text.base),
                    maxLines = 1,
                    singleLine = true,
                    cursorBrush = SolidColor(System.color.text.link),
                    interactionSource = interactionSource,
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (fieldValue.text.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    color = System.color.text.subtle,
                                    style = System.font.body.base.bold
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }

            trailingPainter?.let { painter ->
                Surface(
                    shape = RoundedCornerShape(11.dp),
                    color = System.color.background.tertiary,
                    onClick = onClickTrailingPainter ?: { },
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            painter = painter,
                            contentDescription = null,
                            tint = System.color.icon.base,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() = YallaTheme {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .background(System.color.background.base)
            .padding(16.dp)
    ) {
        var value1 by remember { mutableStateOf("") }
        SearchField(
            value = value1,
            onValueChange = { value1 = it },
            placeholder = "Search..."
        )

        var value2 by remember { mutableStateOf("Tashkent") }
        SearchField(
            value = value2,
            onValueChange = { value2 = it },
            placeholder = "Where to?",
            leadingPainter = rememberVectorPainter(YallaIcons.Location)
        )

        var value3 by remember { mutableStateOf("Samarkand") }
        SearchField(
            value = value3,
            onValueChange = { value3 = it },
            placeholder = "Search destination",
            leadingPainter = rememberVectorPainter(YallaIcons.Flag),
            trailingPainter = rememberVectorPainter(YallaIcons.Location),
            onClickTrailingPainter = { value3 = "" }
        )
    }
}
