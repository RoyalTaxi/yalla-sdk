package uz.yalla.components.primitives.field

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.icons.Calendar
import uz.yalla.resources.icons.YallaIcons

@Composable
public fun PrimaryField(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    placeholder: String = "",
    textAlign: TextAlign = TextAlign.Start,
    trailingIcon: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    singleLine: Boolean = false,
    minHeight: Dp? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    focusRequester: FocusRequester? = null,
    modifier: Modifier = Modifier
) {
    var fieldValue by remember { mutableStateOf(TextFieldValue(value, TextRange(value.length))) }
    LaunchedEffect(value) {
        if (fieldValue.text != value) {
            fieldValue = fieldValue.copy(text = value, selection = TextRange(value.length))
        }
    }
    Box(modifier = modifier) {
        OutlinedTextField(
            value = fieldValue,
            onValueChange = { fv ->
                val textChanged = fv.text != fieldValue.text
                fieldValue = fv
                if (textChanged) onValueChange(fv.text)
            },
            readOnly = !enabled,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .then(if (minHeight != null) Modifier.heightIn(min = minHeight) else Modifier)
                    .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier),
            singleLine = singleLine,
            shape = RoundedCornerShape(10.dp),
            textStyle =
                System.font.body.base.medium
                    .copy(textAlign = textAlign),
            trailingIcon = trailingIcon,
            placeholder = {
                Text(
                    text = placeholder,
                    color = System.color.text.subtle,
                    style = System.font.body.base.medium,
                    textAlign = textAlign,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            keyboardOptions = keyboardOptions,
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedTextColor = System.color.text.base,
                    unfocusedTextColor = System.color.text.base,
                    focusedBorderColor = System.color.border.filled,
                    unfocusedBorderColor = System.color.border.disabled,
                    focusedPlaceholderColor = System.color.text.subtle,
                    unfocusedPlaceholderColor = System.color.text.subtle,
                    cursorColor = System.color.text.link,
                    selectionColors =
                        TextSelectionColors(
                            handleColor = System.color.text.link,
                            backgroundColor =
                                System.color.text.link
                                    .copy(.3f)
                        )
                )
        )

        if (onClick != null) {
            Box(
                modifier =
                    Modifier
                        .matchParentSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onClick
                        )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() =
    YallaTheme {
        Column {
            var valu1 by remember { mutableStateOf("") }
            PrimaryField(
                value = valu1,
                modifier = Modifier.padding(16.dp),
                onValueChange = { valu1 = it }
            )

            var value2 by remember { mutableStateOf("") }
            PrimaryField(
                value = value2,
                modifier = Modifier.padding(16.dp),
                onValueChange = { value2 = it },
                enabled = false,
                trailingIcon = {
                    Icon(
                        imageVector = YallaIcons.Calendar,
                        contentDescription = null
                    )
                }
            )
        }
    }
