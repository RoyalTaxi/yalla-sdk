package uz.yalla.maps.compose

internal interface MapNode {
    fun onAttached() {}

    fun onRemoved() {}

    fun onCleared() {}
}

internal object MapNodeRoot : MapNode
