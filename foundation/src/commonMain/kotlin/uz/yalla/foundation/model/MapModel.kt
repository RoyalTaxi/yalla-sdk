package uz.yalla.foundation.model

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import uz.yalla.core.settings.MapKind
import uz.yalla.resources.Res
import uz.yalla.resources.settings_map_google
import uz.yalla.resources.settings_map_libre

/**
 * UI-ready map provider model for settings screens.
 *
 * Sealed hierarchy mapping [MapKind] to display properties (localized name).
 *
 * @property name Localized map provider name
 * @property mapKind Corresponding [MapKind] for persistence
 * @since 0.0.1
 */
sealed class MapModel(
    val name: StringResource,
    val mapKind: MapKind
) {
    data object Google : MapModel(
        name = Res.string.settings_map_google,
        mapKind = MapKind.Google
    )

    data object Libre : MapModel(
        name = Res.string.settings_map_libre,
        mapKind = MapKind.Libre
    )

    @Composable
    fun toSelectableItemModel() =
        SelectableItemModel(
            item = this,
            title = stringResource(name)
        )

    companion object {
        /** All available map provider options. @since 0.0.1 */
        val all = listOf(Google, Libre)

        /**
         * Resolves a [MapModel] from the persisted [MapKind].
         *
         * @since 0.0.1
         */
        fun fromMapKind(mapKind: MapKind): MapModel =
            when (mapKind) {
                MapKind.Google -> Google
                MapKind.Libre -> Libre
            }
    }
}
