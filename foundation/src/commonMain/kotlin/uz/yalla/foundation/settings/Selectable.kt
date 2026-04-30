package uz.yalla.foundation.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Common contract for settings options displayed in selection lists.
 *
 * Implemented by sealed hierarchies ([ThemeOption], [LanguageOption], [MapOption])
 * that map persistence enums to UI display properties.
 *
 * @property name Localized display name
 * @property icon Optional icon; null when the option has no visual indicator
 */
interface Selectable {
    val name: StringResource
    val icon: ImageVector? get() = null
}

/**
 * Converts any [Selectable] to an [OptionModel] for use in selection lists.
 *
 * Resolves the [Selectable.name] to a localized string and the optional [Selectable.icon]
 * to a remembered [VectorPainter][androidx.compose.ui.graphics.vector.VectorPainter].
 *
 * @param iconColor Tint applied to the icon. Use [Color.Unspecified] for multi-color icons (e.g. flags).
 */
@Composable
fun <T : Selectable> T.toSelectableItem(
    iconColor: Color = Color.Unspecified
) = OptionModel(
    item = this,
    title = stringResource(name),
    icon = icon?.let { rememberVectorPainter(it) },
    iconColor = iconColor
)
