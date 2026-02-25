package uz.yalla.foundation.model

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import uz.yalla.core.kind.MapKind
import uz.yalla.resources.Res
import uz.yalla.resources.settings_map_google
import uz.yalla.resources.settings_map_libre

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
        val MAPS = listOf(Google, Libre)

        fun fromMapKind(mapKind: MapKind): MapModel =
            when (mapKind) {
                MapKind.Google -> Google
                MapKind.Libre -> Libre
            }
    }
}
