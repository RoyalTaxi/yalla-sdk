package uz.yalla.foundation.settings

import org.jetbrains.compose.resources.StringResource
import uz.yalla.core.settings.MapKind
import uz.yalla.resources.Res
import uz.yalla.resources.settings_map_google
import uz.yalla.resources.settings_map_libre

/**
 * Map provider option for settings screens.
 *
 * Sealed hierarchy mapping [MapKind] to display properties.
 * Has no icon — only a localized name.
 *
 */
sealed class MapOption(
    override val name: StringResource,
    val kind: MapKind
) : Selectable {

    data object Google : MapOption(
        name = Res.string.settings_map_google,
        kind = MapKind.Google
    )

    data object Libre : MapOption(
        name = Res.string.settings_map_libre,
        kind = MapKind.Libre
    )

    companion object {
        val all = listOf(Google, Libre)

        fun from(kind: MapKind): MapOption =
            when (kind) {
                MapKind.Google -> Google
                MapKind.Libre -> Libre
            }
    }
}
