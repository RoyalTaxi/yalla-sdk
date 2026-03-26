package uz.yalla.primitives.field

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import uz.yalla.design.theme.System
import uz.yalla.resources.icons.Calendar
import uz.yalla.resources.icons.YallaIcons

/**
 * Color configuration for [DateField].
 *
 * Use [DateFieldDefaults.colors] to create with theme-aware defaults.
 *
 * @param containerColor Background color of the field.
 * @param textColor Color of the selected date text.
 * @param placeholderColor Color of the placeholder text.
 * @param iconColor Color of the calendar icon.
 */
@Immutable
data class DateFieldColors(
    val containerColor: Color,
    val textColor: Color,
    val placeholderColor: Color,
    val iconColor: Color,
)

/**
 * Dimension configuration for [DateField].
 *
 * Use [DateFieldDefaults.dimens] to create with standard values.
 *
 * @param shape Corner shape of the field.
 * @param contentPadding Padding inside the field container.
 */
@Immutable
data class DateFieldDimens(
    val shape: Shape,
    val contentPadding: PaddingValues,
)

/**
 * Default configuration values for [DateField].
 *
 * Provides theme-aware defaults for [colors], [textStyle], and [dimens].
 * @since 0.0.1
 */
object DateFieldDefaults {

    /** Creates theme-aware color configuration for [DateField]. */
    @Composable
    fun colors(
        containerColor: Color = Color.Transparent,
        textColor: Color = System.color.text.base,
        placeholderColor: Color = System.color.text.subtle,
        iconColor: Color = System.color.icon.base,
    ): DateFieldColors = DateFieldColors(
        containerColor = containerColor,
        textColor = textColor,
        placeholderColor = placeholderColor,
        iconColor = iconColor,
    )

    /** Creates theme-aware text style for [DateField]. */
    @Composable
    fun textStyle(): TextStyle = System.font.body.base.medium

    /** Creates dimension configuration for [DateField]. */
    fun dimens(
        shape: Shape = RoundedCornerShape(8.dp),
        contentPadding: PaddingValues = PaddingValues(16.dp),
    ): DateFieldDimens = DateFieldDimens(
        shape = shape,
        contentPadding = contentPadding,
    )
}

/**
 * Read-only date field that opens a date picker on click.
 *
 * Displays formatted date or placeholder when no date is selected.
 * Includes a trailing calendar icon.
 *
 * ## Usage
 *
 * ```kotlin
 * var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
 * var showPicker by remember { mutableStateOf(false) }
 *
 * DateField(
 *     date = selectedDate,
 *     onClick = { showPicker = true },
 *     modifier = Modifier.fillMaxWidth(),
 * )
 * ```
 *
 * @param date Currently selected date, or null if none.
 * @param onClick Invoked when the field is clicked (typically to open a picker).
 * @param modifier Applied to the root card container.
 * @param placeholder Text shown when no date is selected.
 * @param enabled Whether the field is clickable.
 * @param borderStroke Optional border around the field.
 * @param colors Color configuration, defaults to [DateFieldDefaults.colors].
 * @param textStyle Text style for date and placeholder, defaults to [DateFieldDefaults.textStyle].
 * @param dimens Dimension configuration, defaults to [DateFieldDefaults.dimens].
 *
 * @see DateFieldDefaults for default values
 * @since 0.0.1
 */
@Composable
fun DateField(
    date: LocalDate?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "DD.MM.YYYY",
    enabled: Boolean = true,
    borderStroke: BorderStroke? = null,
    colors: DateFieldColors = DateFieldDefaults.colors(),
    textStyle: TextStyle = DateFieldDefaults.textStyle(),
    dimens: DateFieldDimens = DateFieldDefaults.dimens(),
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        shape = dimens.shape,
        colors = CardDefaults.cardColors(colors.containerColor),
        border = borderStroke,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimens.contentPadding),
        ) {
            Text(
                text = date?.formatDisplay() ?: placeholder,
                style = textStyle,
                color = if (date != null) colors.textColor else colors.placeholderColor,
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                painter = rememberVectorPainter(YallaIcons.Calendar),
                contentDescription = null,
                tint = colors.iconColor,
            )
        }
    }
}

/**
 * Formats [LocalDate] as DD.MM.YYYY for display.
 */
private fun LocalDate.formatDisplay(): String =
    "${day.toString().padStart(2, '0')}." +
        "${month.number.toString().padStart(2, '0')}." +
        "$year"
