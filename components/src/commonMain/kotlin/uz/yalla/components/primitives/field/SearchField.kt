package uz.yalla.components.primitives.field

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.icons.Flag
import uz.yalla.resources.icons.Location
import uz.yalla.resources.icons.YallaIcons

@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    leadingPainter: Painter? = null,
    trailingPainter: Painter? = null,
    onClickTrailingPainter: (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocusing by interactionSource.collectIsFocusedAsState()

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = System.color.background.secondary
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(10.dp)
        ) {
            leadingPainter?.let { painter ->
                Surface(
                    color = if (isFocusing) System.color.background.brand else Color.Transparent,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        painter = painter,
                        contentDescription = null,
                        tint = if (isFocusing) System.color.icon.white else System.color.background.brand,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(24.dp)
                    )
                }
            }

            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                textStyle = System.font.body.base.bold,
                maxLines = 1,
                placeholder = {
                    Text(
                        text = placeholder,
                        color = System.color.text.subtle,
                        style = System.font.body.base.bold
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = System.color.text.base,
                    unfocusedTextColor = System.color.text.base,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = System.color.text.link,
                    selectionColors = TextSelectionColors(
                        handleColor = System.color.text.link,
                        backgroundColor = System.color.text.link.copy(.3f)
                    )
                )
            )

            trailingPainter?.let { painter ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = System.color.background.tertiary,
                    onClick = onClickTrailingPainter ?: { }
                ) {
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(24.dp)
                    )
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
