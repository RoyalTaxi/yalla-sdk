package uz.yalla.core.identity

import kotlin.jvm.JvmInline

@JvmInline
public value class OrderId(
    public val raw: Int
)

@JvmInline
public value class DriverId(
    public val raw: Int
)

@JvmInline
public value class TaxiId(
    public val raw: Int
)

@JvmInline
public value class ExtraServiceId(
    public val raw: Int
)

@JvmInline
public value class ServiceBrandId(
    public val raw: Int
)

@JvmInline
public value class PlaceId(
    public val raw: Int
)

@JvmInline
public value class CardId(
    public val raw: String
)
