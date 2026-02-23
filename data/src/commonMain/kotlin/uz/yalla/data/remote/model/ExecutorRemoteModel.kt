package uz.yalla.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ExecutorRemoteModel(
    val distance: Double?,
    val heading: Double?,
    val id: Int?,
    val lat: Double?,
    val lng: Double?
)
