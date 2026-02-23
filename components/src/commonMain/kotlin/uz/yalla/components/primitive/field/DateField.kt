package uz.yalla.components.primitive.field

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.painterResource
import uz.yalla.design.theme.System
import uz.yalla.resources.Res
import uz.yalla.resources.ic_calendar

/**
 * State for [DateField] component.
 *
 * @property date Currently selected date, or null if none.
 * @property placeholder Text shown when no date selected.
 * @property enabled When false, field is not clickable.
 * @property borderStroke Optional border around field.
 */
data class DateFieldState(
    val date: LocalDate?,
    val placeholder: String = "DD.MM.YYYY",
    val enabled: Boolean = true,
    val borderStroke: BorderStroke? = null
)

/**
 * Read-only date field that opens a date picker on click.
 *
 * Displays formatted date or placeholder when no date selected.
 *
 * ## Usage
 *
 * ```kotlin
 * var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
 * var showPicker by remember { mutableStateOf(false) }
 *
 * DateField(
 *     state = DateFieldState(
 *         date = selectedDate,
 *         placeholder = "Select date"
 *     ),
 *     onClick = { showPicker = true },
 * )
 * ```
 *
 * @param state Field state containing date, placeholder, enabled, and borderStroke.
 * @param onClick Invoked when field is clicked (open picker).
 * @param modifier Applied to field container.
 * @param colors Color configuration, defaults to [DateFieldDefaults.colors].
 * @param style Text style configuration, defaults to [DateFieldDefaults.style].
 * @param dimens Dimension configuration, defaults to [DateFieldDefaults.dimens].
 *
 * @see DateFieldDefaults for default values
 */
@Composable
fun DateField(
    state: DateFieldState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: DateFieldDefaults.DateFieldColors = DateFieldDefaults.colors(),
    style: DateFieldDefaults.DateFieldStyle = DateFieldDefaults.style(),
    dimens: DateFieldDefaults.DateFieldDimens = DateFieldDefaults.dimens(),
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        enabled = state.enabled,
        shape = dimens.shape,
        colors = CardDefaults.cardColors(colors.container),
        border = state.borderStroke,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(dimens.contentPadding),
        ) {
            Text(
                text = state.date?.formatDisplay() ?: state.placeholder,
                style = style.text,
                color = if (state.date != null) colors.text else colors.placeholder,
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                painter = painterResource(Res.drawable.ic_calendar),
                contentDescription = null,
                tint = colors.icon,
            )
        }
    }
}

/**
 * Formats LocalDate for display.
 */
private fun LocalDate.formatDisplay(): String =
    "${dayOfMonth.toString().padStart(2, '0')}." +
        "${monthNumber.toString().padStart(2, '0')}." +
        "$year"

/**
 * Default values for [DateField].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object DateFieldDefaults {
    /**
     * Color configuration for [DateField].
     *
     * @param container Background color.
     * @param text Selected date text color.
     * @param placeholder Placeholder text color.
     * @param icon Calendar icon color.
     */
    data class DateFieldColors(
        val container: Color,
        val text: Color,
        val placeholder: Color,
        val icon: Color,
    )

    @Composable
    fun colors(
        container: Color = Color.Transparent,
        text: Color = System.color.textBase,
        placeholder: Color = System.color.textSubtle,
        icon: Color = System.color.iconBase,
    ): DateFieldColors =
        DateFieldColors(
            container = container,
            text = text,
            placeholder = placeholder,
            icon = icon,
        )

    /**
     * Text style configuration for [DateField].
     *
     * @param text Text style for date and placeholder.
     */
    data class DateFieldStyle(
        val text: TextStyle,
    )

    @Composable
    fun style(text: TextStyle = System.font.body.base.medium): DateFieldStyle =
        DateFieldStyle(
            text = text,
        )

    /**
     * Dimension configuration for [DateField].
     *
     * @param shape Corner shape of the field.
     * @param contentPadding Padding inside the field.
     */
    data class DateFieldDimens(
        val shape: Shape,
        val contentPadding: Dp,
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(8.dp),
        contentPadding: Dp = 16.dp,
    ): DateFieldDimens =
        DateFieldDimens(
            shape = shape,
            contentPadding = contentPadding,
        )
}

@Preview
@Composable
private fun DateFieldPreview() {
    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp)
    ) {
        DateField(
            state = DateFieldState(date = null),
            onClick = {},
        )
    }
}
