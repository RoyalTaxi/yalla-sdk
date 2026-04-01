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
 * @property kind Corresponding [MapKind] for persistence
 * @since 0.0.1
 */
sealed class MapOption(
    override val name: StringResource,
    val kind: MapKind
) : Selectable {

    /** Google Maps provider option. @since 0.0.1 */
    data object Google : MapOption(
        name = Res.string.settings_map_google,
        kind = MapKind.Google
    )

    /** MapLibre (open-source) provider option. @since 0.0.1 */
    data object Libre : MapOption(
        name = Res.string.settings_map_libre,
        kind = MapKind.Libre
    )

    companion object {
        /** All available map provider options. @since 0.0.1 */
        val all = listOf(Google, Libre)

        /**
         * Resolves a [MapOption] from the persisted [MapKind].
         *
         * @param kind The persisted map kind to resolve.
         * @return Corresponding [MapOption] instance.
         * @since 0.0.1
         */
        fun from(kind: MapKind): MapOption =
            when (kind) {
                MapKind.Google -> Google
                MapKind.Libre -> Libre
            }
    }
}
