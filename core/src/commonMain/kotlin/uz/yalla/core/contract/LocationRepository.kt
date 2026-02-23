package uz.yalla.core.contract

import uz.yalla.core.error.DataError
import uz.yalla.core.error.Either
import uz.yalla.core.model.location.GetRoutingModel
import uz.yalla.core.model.location.GetRoutingRequestItemDto
import uz.yalla.core.model.location.PlaceNameModel
import uz.yalla.core.model.location.PolygonRemoteItem
import uz.yalla.core.model.location.SearchForAddressItemModel
import uz.yalla.core.model.location.SecondaryAddressItemModel

interface LocationRepository {
    suspend fun getPolygon(): Either<List<PolygonRemoteItem>, DataError.Network>

    suspend fun getAddress(lat: Double, lng: Double): Either<PlaceNameModel, DataError.Network>

    suspend fun searchForAddress(
        lat: Double,
        lng: Double,
        query: String
    ): Either<List<SearchForAddressItemModel>, DataError.Network>

    suspend fun getRouting(addresses: List<GetRoutingRequestItemDto>): Either<GetRoutingModel, DataError.Network>

    suspend fun searchSecondaryAddress(
        lat: Double,
        lng: Double
    ): Either<List<SecondaryAddressItemModel>, DataError.Network>
}
