package uz.yalla.primitives.field

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import uz.yalla.design.theme.System
import uz.yalla.resources.Res
import uz.yalla.resources.auth_phone_country_code
import uz.yalla.resources.auth_phone_placeholder

@Composable
fun NumberField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null
) {
    var isFocused by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier,
        color = System.color.backgroundBase,
        shape = RoundedCornerShape(10.dp),
        border =
            BorderStroke(
                width = 1.dp,
                color = if (isFocused) System.color.borderFilled else System.color.borderDisabled
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            Text(
                text = stringResource(Res.string.auth_phone_country_code),
                color = System.color.textBase,
                style = System.font.body.base.medium,
                modifier = Modifier.padding(16.dp)
            )

            VerticalDivider(
                thickness = 1.dp,
                color = System.color.borderDisabled,
                modifier = Modifier.padding(vertical = 6.dp)
            )

            TextField(
                state = state,
                textStyle = System.font.body.base.medium,
                lineLimits = TextFieldLineLimits.SingleLine,
                inputTransformation = DigitsOnlyTransformation,
                outputTransformation = PhoneOutputTransformation,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier =
                    Modifier
                        .weight(1f)
                        .onFocusChanged { isFocused = it.isFocused }
                        .then(focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier),
                placeholder = @Composable {
                    Text(
                        text = stringResource(Res.string.auth_phone_placeholder),
                        color = System.color.textSubtle,
                        style = System.font.body.base.medium
                    )
                },
                colors =
                    TextFieldDefaults.colors(
                        focusedContainerColor = System.color.backgroundBase,
                        unfocusedContainerColor = System.color.backgroundBase,
                        disabledContainerColor = System.color.backgroundBase,
                        errorContainerColor = System.color.backgroundBase,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        focusedTextColor = System.color.textBase,
                        unfocusedTextColor = System.color.textBase,
                        disabledTextColor = System.color.textBase,
                        errorTextColor = System.color.textBase,
                        cursorColor = System.color.textLink,
                        selectionColors =
                            TextSelectionColors(
                                handleColor = System.color.textLink,
                                backgroundColor = System.color.textLink.copy(.3f)
                            )
                    )
            )
        }
    }
}

private object DigitsOnlyTransformation : InputTransformation {
    override fun TextFieldBuffer.transformInput() {
        if (length > 9 || !asCharSequence().all { it.isDigit() }) {
            revertAllChanges()
        }
    }
}

private object PhoneOutputTransformation : OutputTransformation {
    override fun TextFieldBuffer.transformOutput() {
        if (length > 0) insert(0, "(")
        if (length > 3) insert(3, ") ")
        if (length > 8) insert(8, " ")
        if (length > 11) insert(11, " ")
    }
}
