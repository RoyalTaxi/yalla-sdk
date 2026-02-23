package uz.yalla.components.primitive.button

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import uz.yalla.components.util.resource
import uz.yalla.core.kind.GenderKind
import uz.yalla.design.theme.System
import uz.yalla.resources.Res
import uz.yalla.resources.ic_checked
import uz.yalla.resources.ic_unchecked

/**
 * State for [GenderButton] component.
 *
 * @property gender GenderKind option to display.
 * @property isSelected Whether this option is selected.
 */
data class GenderButtonState(
    val gender: GenderKind,
    val isSelected: Boolean,
)

/**
 * Default configuration values for [GenderButton].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object GenderButtonDefaults {
    /**
     * Color configuration for [GenderButton].
     *
     * @param container Button background color.
     * @param text Text color.
     */
    data class GenderButtonColors(
        val container: Color,
        val text: Color
    )

    @Composable
    fun colors(
        container: Color = System.color.backgroundSecondary,
        text: Color = System.color.textBase
    ) = GenderButtonColors(
        container = container,
        text = text
    )

    /**
     * Text style configuration for [GenderButton].
     *
     * @param label Style applied to the gender text.
     */
    data class GenderButtonStyle(
        val label: TextStyle
    )

    @Composable
    fun style(label: TextStyle = System.font.body.base.medium) =
        GenderButtonStyle(
            label = label
        )

    /**
     * Dimension configuration for [GenderButton].
     *
     * @param shape Button shape.
     * @param contentPadding Padding inside the button.
     * @param innerStartPadding Padding before the text.
     * @param iconSize Size of the checkbox icon.
     */
    data class GenderButtonDimens(
        val shape: Shape,
        val contentPadding: PaddingValues,
        val innerStartPadding: Dp,
        val iconSize: Dp
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(16.dp),
        contentPadding: PaddingValues = PaddingValues(12.dp),
        innerStartPadding: Dp = 12.dp,
        iconSize: Dp = 24.dp
    ) = GenderButtonDimens(
        shape = shape,
        contentPadding = contentPadding,
        innerStartPadding = innerStartPadding,
        iconSize = iconSize
    )
}

/**
 * Selectable button for gender selection.
 *
 * ## Usage
 *
 * ```kotlin
 * GenderButton(
 *     state = GenderButtonState(
 *         gender = GenderKind.Male,
 *         isSelected = selectedGender == GenderKind.Male,
 *     ),
 *     onClick = { selectGender(GenderKind.Male) },
 *     modifier = Modifier.weight(1f),
 * )
 * ```
 *
 * @param state Button state containing gender and selection state.
 * @param onClick Invoked when button is clicked.
 * @param modifier Applied to button.
 * @param colors Color configuration, defaults to [GenderButtonDefaults.colors].
 * @param style Text style configuration, defaults to [GenderButtonDefaults.style].
 * @param dimens Dimension configuration, defaults to [GenderButtonDefaults.dimens].
 *
 * @see GenderButtonState for state configuration
 * @see GenderButtonDefaults for default values
 */
@Composable
fun GenderButton(
    state: GenderButtonState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: GenderButtonDefaults.GenderButtonColors = GenderButtonDefaults.colors(),
    style: GenderButtonDefaults.GenderButtonStyle = GenderButtonDefaults.style(),
    dimens: GenderButtonDefaults.GenderButtonDimens = GenderButtonDefaults.dimens()
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = dimens.shape,
        contentPadding = dimens.contentPadding,
        colors = ButtonDefaults.buttonColors(colors.container)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = dimens.innerStartPadding)
        ) {
            Text(
                text =
                    state.gender.resource
                        ?.let { res -> stringResource(res) }
                        .orEmpty(),
                color = colors.text,
                style = style.label
            )

            Image(
                contentDescription = null,
                modifier = Modifier.size(dimens.iconSize),
                painter =
                    if (state.isSelected) {
                        painterResource(Res.drawable.ic_checked)
                    } else {
                        painterResource(Res.drawable.ic_unchecked)
                    }
            )
        }
    }
}
