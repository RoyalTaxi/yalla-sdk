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
 * @since 0.0.1
 */
interface Selectable {
    val name: StringResource
    val icon: ImageVector? get() = null
}

/**
 * Converts any [Selectable] to a [SelectableItemModel] for use in selection lists.
 *
 * @param iconColor Tint applied to the icon. Use [Color.Unspecified] for multi-color icons (e.g. flags).
 * @since 0.0.1
 */
@Composable
fun <T : Selectable> T.toSelectableItem(
    iconColor: Color = Color.Unspecified
) = SelectableItemModel(
    item = this,
    title = stringResource(name),
    icon = icon?.let { rememberVectorPainter(it) },
    iconColor = iconColor
)
