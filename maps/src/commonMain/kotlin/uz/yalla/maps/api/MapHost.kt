package uz.yalla.maps.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
public expect fun MapHost(
    controller: MapController,
    modifier: Modifier
)
