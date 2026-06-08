package uz.yalla.maps.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun MapHost(controller: MapController, modifier: Modifier)
