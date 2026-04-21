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

/**
 * Color configuration for [SearchField].
 *
 * Defines the visual palette used by the search field container, text, and placeholder.
 * Use [SearchFieldDefaults.colors] to create with theme-aware defaults.
 *
 * @param container Background color of the search field card.
 * @param text Color of the input text.
 * @param placeholder Color of the placeholder text shown when the field is empty.
 * @since 0.0.1
 */
@Immutable
data class SearchFieldColors(
    val container: Color,
    val text: Color,
    val placeholder: Color,
)

/**
 * Dimension configuration for [SearchField].
 *
 * Controls the shape, padding, icon spacing, and minimum height of the search field.
 * Use [SearchFieldDefaults.dimens] to create with standard values.
 *
 * @param shape Corner shape of the search field card container.
 * @param contentPadding Padding inside the card between the container edge and content row.
 * @param iconSpacing Horizontal space between leading/trailing icons and the text input.
 * @param minHeight Minimum height of the search field for touch target compliance.
 * @since 0.0.1
 */
@Immutable
data class SearchFieldDimens(
    val shape: Shape,
    val contentPadding: PaddingValues,
    val iconSpacing: Dp,
    val minHeight: Dp,
)

/**
 * Default configuration values for [SearchField].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be individually overridden.
 * @since 0.0.1
 */
object SearchFieldDefaults {

    /**
     * Creates theme-aware color configuration for [SearchField].
     *
     * @param container Background color of the card container.
     * @param text Color of the input text.
     * @param placeholder Color of the placeholder text.
     */
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

    /**
     * Creates dimension configuration for [SearchField].
     *
     * @param shape Corner shape of the container card.
     * @param contentPadding Padding inside the card.
     * @param iconSpacing Space between icons and text input.
     * @param minHeight Minimum height for touch target compliance.
     */
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

/**
 * Search input field with card-style container and optional icon slots.
 *
 * Renders a [BasicTextField] inside a [Card] with leading and trailing icon slots.
 * The field displays a placeholder when empty and uses bold text styling
 * consistent with the Yalla design system.
 *
 * ## Usage
 *
 * ```kotlin
 * val searchState = rememberTextFieldState()
 *
 * SearchField(
 *     state = searchState,
 *     placeholder = "Search for a place...",
 *     leadingIcon = { Icon(YallaIcons.Search, contentDescription = null) },
 *     modifier = Modifier.fillMaxWidth(),
 * )
 * ```
 *
 * ## With Trailing Clear Button
 *
 * ```kotlin
 * SearchField(
 *     state = searchState,
 *     placeholder = "Search",
 *     trailingIcon = {
 *         if (searchState.text.isNotEmpty()) {
 *             IconButton(onClick = { searchState.clearText() }) {
 *                 Icon(Icons.Default.Clear, contentDescription = "Clear")
 *             }
 *         }
 *     },
 * )
 * ```
 *
 * @param state Text field state managing the input value and selection.
 * @param modifier [Modifier] applied to the root card container.
 * @param placeholder Text shown when the field is empty.
 * @param focusRequester Optional focus requester for programmatic focus control.
 * @param colors [SearchFieldColors] that define container, text, and placeholder colors.
 *   See [SearchFieldDefaults.colors].
 * @param dimens [SearchFieldDimens] that define shape, padding, and sizing.
 *   See [SearchFieldDefaults.dimens].
 * @param leadingIcon Optional composable displayed before the text input.
 * @param trailingIcon Optional composable displayed after the text input.
 *
 * @see PrimaryField for outlined text input variant
 * @see SearchFieldDefaults for default values
 * @since 0.0.1
 */
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
                    textStyle = System.font.body.base.bold
                        .copy(color = colors.text),
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
