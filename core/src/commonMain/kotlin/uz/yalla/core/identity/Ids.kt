package uz.yalla.core.identity

import kotlin.jvm.JvmInline

@JvmInline
value class OrderId(
    val raw: Int
)

@JvmInline
value class DriverId(
    val raw: Int
)

@JvmInline
value class TaxiId(
    val raw: Int
)

@JvmInline
value class ExtraServiceId(
    val raw: Int
)

@JvmInline
value class ServiceBrandId(
    val raw: Int
)

@JvmInline
value class PlaceId(
    val raw: Int
)

@JvmInline
value class CardId(
    val raw: String
)
