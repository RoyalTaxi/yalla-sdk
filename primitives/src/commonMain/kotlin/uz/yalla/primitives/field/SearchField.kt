package uz.yalla.primitives.field

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

@Immutable
data class SearchFieldColors(
    val container: Color,
    val text: Color,
    val placeholder: Color,
)

@Immutable
data class SearchFieldDimens(
    val shape: Shape,
    val contentPadding: PaddingValues,
    val iconSpacing: Dp,
    val minHeight: Dp,
)

object SearchFieldDefaults {

    @Composable
    fun colors(
        container: Color = System.color.background.secondary,
        text: Color = System.color.text.base,
        placeholder: Color = System.color.text.subtle,
    ): SearchFieldColors = SearchFieldColors(
        container = container,
        text = text,
        placeholder = placeholder,
    )

    fun dimens(
        shape: Shape = RoundedCornerShape(16.dp),
        contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        iconSpacing: Dp = 8.dp,
        minHeight: Dp = 48.dp,
    ): SearchFieldDimens = SearchFieldDimens(
        shape = shape,
        contentPadding = contentPadding,
        iconSpacing = iconSpacing,
        minHeight = minHeight,
    )
}

@Composable
fun SearchField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    focusRequester: FocusRequester? = null,
    colors: SearchFieldColors = SearchFieldDefaults.colors(),
    dimens: SearchFieldDimens = SearchFieldDefaults.dimens(),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Card(
        modifier = modifier,
        shape = dimens.shape,
        colors = CardDefaults.cardColors(colors.container),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .heightIn(min = dimens.minHeight)
                .height(IntrinsicSize.Min)
                .padding(dimens.contentPadding)
                .fillMaxWidth(),
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(dimens.iconSpacing))
            }

            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier.weight(1f),
            ) {
                if (state.text.isEmpty() && placeholder.isNotEmpty()) {
                    Text(
                        text = placeholder,
                        color = colors.placeholder,
                        style = System.font.body.base.bold,
                    )
                }

                BasicTextField(
                    state = state,
                    modifier = Modifier
                        .fillMaxWidth()
                        .applyFocusRequester(focusRequester),
                    cursorBrush = SolidColor(System.color.text.link),
                    textStyle = System.font.body.base.bold.copy(color = colors.text),
                    lineLimits = TextFieldLineLimits.SingleLine,
                )
            }

            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(dimens.iconSpacing))
                trailingIcon()
            }
        }
    }
}

private fun Modifier.applyFocusRequester(focusRequester: FocusRequester?): Modifier =
    if (focusRequester != null) this.focusRequester(focusRequester) else this
