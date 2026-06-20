package uz.yalla.core.identity

import kotlin.jvm.JvmInline

// Typed id wrappers so a value of one kind can't be passed where another is expected (this caught a
// real DriverId(0)-as-OrderId bug). They are `@JvmInline value class`es: the type distinction holds
// on JVM/Android but erases to the underlying type at the ObjC/Swift boundary.

/** Identifies an order. */
@JvmInline
public value class OrderId(
    public val raw: Int
)

/** Identifies a driver. */
@JvmInline
public value class DriverId(
    public val raw: Int
)

/** Identifies a taxi/vehicle. */
@JvmInline
public value class TaxiId(
    public val raw: Int
)

/** Identifies an extra (add-on) service. */
@JvmInline
public value class ExtraServiceId(
    public val raw: Int
)

/** Identifies a service brand. */
@JvmInline
public value class ServiceBrandId(
    public val raw: Int
)

/** Identifies a saved place. */
@JvmInline
public value class PlaceId(
    public val raw: Int
)

/** Identifies a saved payment card (an opaque backend token, not a PAN). */
@JvmInline
public value class CardId(
    public val raw: String
)
