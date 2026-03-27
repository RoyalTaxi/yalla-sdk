package uz.yalla.primitives.button

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import uz.yalla.core.profile.GenderKind
import uz.yalla.design.theme.System
import uz.yalla.primitives.util.resource
import uz.yalla.resources.icons.Checked
import uz.yalla.resources.icons.Unchecked
import uz.yalla.resources.icons.YallaIcons

/**
 * Color configuration for [GenderButton].
 *
 * Use [GenderButtonDefaults.colors] to create with theme-aware defaults.
 *
 * @param containerColor Background color.
 * @param textColor Text color for the gender label.
 */
@Immutable
data class GenderButtonColors(
    val containerColor: Color,
    val textColor: Color,
)

/**
 * Dimension configuration for [GenderButton].
 *
 * Use [GenderButtonDefaults.dimens] to create with standard values.
 *
 * @param shape Container shape.
 * @param contentPadding Padding inside the button.
 * @param innerStartPadding Padding before the text label.
 * @param iconSize Size of the check/uncheck icon.
 * @param textStyle Text style for the gender label.
 */
@Immutable
data class GenderButtonDimens(
    val shape: Shape,
    val contentPadding: PaddingValues,
    val innerStartPadding: Dp,
    val iconSize: Dp,
)

/**
 * Selectable button for gender selection.
 *
 * Renders the gender name as text and a check/uncheck icon based on selection state.
 * This button has a specialized layout — it does not use [ButtonLayout].
 *
 * ## Usage
 * ```kotlin
 * GenderButton(
 *     gender = GenderKind.Male,
 *     isSelected = selectedGender == GenderKind.Male,
 *     onClick = { selectGender(GenderKind.Male) },
 * )
 * ```
 *
 * ## In a Row
 * ```kotlin
 * Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
 *     GenderKind.entries.forEach { gender ->
 *         GenderButton(
 *             gender = gender,
 *             isSelected = selectedGender == gender,
 *             onClick = { selectGender(gender) },
 *             modifier = Modifier.weight(1f),
 *         )
 *     }
 * }
 * ```
 *
 * @param gender [GenderKind] option to display.
 * @param isSelected Whether this gender option is currently selected.
 * @param onClick Called when the button is clicked.
 * @param modifier [Modifier] applied to the root container.
 * @param colors [GenderButtonColors] that define container and text colors.
 *   See [GenderButtonDefaults.colors].
 * @param dimens [GenderButtonDimens] that define dimensions, shape, and text style.
 *   See [GenderButtonDefaults.dimens].
 *
 * @see GenderButtonDefaults
 */
@Composable
fun GenderButton(
    gender: GenderKind,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = System.font.body.base.medium,
    colors: GenderButtonColors = GenderButtonDefaults.colors(),
    dimens: GenderButtonDimens = GenderButtonDefaults.dimens(),
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = dimens.shape,
        color = colors.containerColor,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimens.contentPadding)
                .padding(start = dimens.innerStartPadding),
        ) {
            Text(
                text = gender.resource
                    ?.let { res -> stringResource(res) }
                    .orEmpty(),
                color = colors.textColor,
                style = textStyle,
            )

            Image(
                contentDescription = null,
                modifier = Modifier.size(dimens.iconSize),
                painter = if (isSelected) {
                    rememberVectorPainter(YallaIcons.Checked)
                } else {
                    rememberVectorPainter(YallaIcons.Unchecked)
                },
            )
        }
    }
}

/**
 * Default configuration values for [GenderButton].
 *
 * Provides theme-aware [colors] and standard [dimens] that can be individually overridden.
 */
object GenderButtonDefaults {
    /** Default button shape. */
    val Shape: Shape = RoundedCornerShape(16.dp)

    /** Default content padding. */
    val ContentPadding: PaddingValues = PaddingValues(12.dp)

    /**
     * Creates [GenderButtonColors] with theme-aware defaults.
     *
     * @param containerColor Background color.
     * @param textColor Text color for the gender label.
     */
    @Composable
    fun colors(
        containerColor: Color = System.color.background.secondary,
        textColor: Color = System.color.text.base,
    ): GenderButtonColors = GenderButtonColors(
        containerColor = containerColor,
        textColor = textColor,
    )

    /**
     * Creates [GenderButtonDimens] with standard values.
     *
     * @param shape Container shape.
     * @param contentPadding Padding inside the button.
     * @param innerStartPadding Padding before the text label.
     * @param iconSize Size of the check/uncheck icon.
     * @param textStyle Text style for the gender label.
     */
    fun dimens(
        shape: Shape = Shape,
        contentPadding: PaddingValues = ContentPadding,
        innerStartPadding: Dp = 12.dp,
        iconSize: Dp = 24.dp,
    ): GenderButtonDimens = GenderButtonDimens(
        shape = shape,
        contentPadding = contentPadding,
        innerStartPadding = innerStartPadding,
        iconSize = iconSize,
    )
}
