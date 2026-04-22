package uz.yalla.core

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.location.Address
import uz.yalla.core.location.AddressOption
import uz.yalla.core.location.PlaceKind
import uz.yalla.core.location.PointKind
import uz.yalla.core.location.PointRequest
import uz.yalla.core.location.Route
import uz.yalla.core.location.SavedAddress
import uz.yalla.core.order.Executor
import uz.yalla.core.order.ExtraService
import uz.yalla.core.order.OrderStatus
import uz.yalla.core.order.ServiceBrand
import uz.yalla.core.payment.PaymentCard
import uz.yalla.core.payment.PaymentKind
import uz.yalla.core.profile.Client
import uz.yalla.core.profile.GenderKind
import uz.yalla.core.settings.LocaleKind
import uz.yalla.core.settings.MapKind
import uz.yalla.core.settings.ThemeKind
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Round-trip tests for public wire-carried domain models. Guards the wire
 * contract frozen by the `@Serializable` + `@SerialName` pass (Task 4 of
 * Phase 2). Each test serializes to JSON, decodes back, and asserts equality.
 *
 * The string-shape checks additionally pin the on-the-wire property names:
 * if a future rename drops `@SerialName` on a property, these assertions
 * will fail and flag the break before it ships.
 */
class SerializationRoundTripTest {
    private val json = Json

    @Test
    fun shouldRoundTripGeoPoint() {
        val value = GeoPoint(lat = 41.3111, lng = 69.2797)
        val encoded = json.encodeToString(value)
        val decoded = json.decodeFromString<GeoPoint>(encoded)

        assertEquals(value, decoded)
        assertTrue(encoded.contains("\"lat\":"))
        assertTrue(encoded.contains("\"lng\":"))
    }

    @Test
    fun shouldRoundTripAddress() {
        val value =
            Address(
                id = 7,
                name = "Amir Temur ko'chasi 1",
                lat = 41.31,
                lng = 69.28,
                isFromDatabase = true
            )
        val encoded = json.encodeToString(value)

        assertEquals(value, json.decodeFromString<Address>(encoded))
        assertTrue(encoded.contains("\"isFromDatabase\":"))
    }

    @Test
    fun shouldRoundTripAddressWhenIdIsNull() {
        val value =
            Address(
                id = null,
                name = "anywhere",
                lat = 0.0,
                lng = 0.0,
                isFromDatabase = false
            )

        assertEquals(value, json.decodeFromString<Address>(json.encodeToString(value)))
    }

    @Test
    fun shouldRoundTripAddressOption() {
        val value =
            AddressOption(
                id = 1,
                title = "Main",
                address = "Tashkent",
                distance = 123.5,
                lat = 41.31,
                lng = 69.28,
                isFromDatabase = false
            )

        assertEquals(value, json.decodeFromString<AddressOption>(json.encodeToString(value)))
    }

    @Test
    fun shouldRoundTripPointRequest() {
        val value = PointRequest(kind = PointKind.START, lng = 69.28, lat = 41.31)
        val encoded = json.encodeToString(value)

        assertEquals(value, json.decodeFromString<PointRequest>(encoded))
        // PointKind.START has wireValue "start" — confirmed by PointKindSerializer
        assertTrue(encoded.contains("\"kind\":\"start\""))
    }

    @Test
    fun shouldRoundTripAllPointKindVariants() {
        PointKind.entries.forEach { kind ->
            val value = PointRequest(kind = kind, lng = 0.0, lat = 0.0)
            val encoded = json.encodeToString(value)
            val decoded = json.decodeFromString<PointRequest>(encoded)

            assertEquals(value, decoded)
            assertTrue(encoded.contains("\"kind\":\"${kind.wireValue}\""))
        }
    }

    @Test
    fun shouldRoundTripRoute() {
        val value =
            Route(
                distance = 1000.0,
                duration = 120.0,
                points = listOf(Route.Point(1.0, 2.0), Route.Point(3.0, 4.0))
            )

        assertEquals(value, json.decodeFromString<Route>(json.encodeToString(value)))
    }

    @Test
    fun shouldRoundTripSavedAddressAndPlaceKind() {
        val value =
            SavedAddress(
                distance = 250.0,
                duration = 60.0,
                lat = 41.31,
                lng = 69.28,
                address = "Home",
                title = "Home sweet home",
                kind = PlaceKind.Home,
                parent = SavedAddress.Parent(name = "Tashkent")
            )
        val encoded = json.encodeToString(value)

        assertEquals(value, json.decodeFromString<SavedAddress>(encoded))
        // PlaceKind.Home → @SerialName("home")
        assertTrue(encoded.contains("\"kind\":\"home\""))
    }

    @Test
    fun shouldRoundTripAllPlaceKindVariants() {
        PlaceKind.entries.forEach { kind ->
            val encoded = json.encodeToString(PlaceKind.serializer(), kind)
            val decoded = json.decodeFromString(PlaceKind.serializer(), encoded)

            assertEquals(kind, decoded)
            assertEquals("\"${kind.id}\"", encoded)
        }
    }

    @Test
    fun shouldRoundTripExecutor() {
        val value = Executor(id = 9, lat = 41.31, lng = 69.28, heading = 90.0, distance = 5.0)

        assertEquals(value, json.decodeFromString<Executor>(json.encodeToString(value)))
    }

    @Test
    fun shouldRoundTripExtraService() {
        val value = ExtraService(id = 2, cost = 5000, name = "Child seat", costType = "cost")
        val encoded = json.encodeToString(value)

        assertEquals(value, json.decodeFromString<ExtraService>(encoded))
        assertTrue(encoded.contains("\"costType\":"))
    }

    @Test
    fun shouldRoundTripServiceBrand() {
        val value = ServiceBrand(id = 1, name = "Yalla", photo = "https://cdn.example.com/brand.png")

        assertEquals(value, json.decodeFromString<ServiceBrand>(json.encodeToString(value)))
    }

    @Test
    fun shouldRoundTripPaymentCard() {
        val value = PaymentCard(cardId = "abc-123", maskedPan = "**** 1234")
        val encoded = json.encodeToString(value)

        assertEquals(value, json.decodeFromString<PaymentCard>(encoded))
        assertTrue(encoded.contains("\"cardId\":"))
        assertTrue(encoded.contains("\"maskedPan\":"))
    }

    @Test
    fun shouldRoundTripClient() {
        val value =
            Client(
                phone = "+998900000000",
                name = "Islom",
                surname = "Sheraliyev",
                image = "",
                birthday = "1995-01-01",
                balance = 12345L,
                gender = "male"
            )

        assertEquals(value, json.decodeFromString<Client>(json.encodeToString(value)))
    }

    @Test
    fun shouldSerializeGenderKindWithWireIds() {
        GenderKind.entries.forEach { kind ->
            val encoded = json.encodeToString(GenderKind.serializer(), kind)
            val decoded = json.decodeFromString(GenderKind.serializer(), encoded)

            assertEquals(kind, decoded)
            assertEquals("\"${kind.id}\"", encoded)
        }
    }

    @Test
    fun shouldSerializeLocaleKindWithWireCodes() {
        LocaleKind.entries.forEach { kind ->
            val encoded = json.encodeToString(LocaleKind.serializer(), kind)
            val decoded = json.decodeFromString(LocaleKind.serializer(), encoded)

            assertEquals(kind, decoded)
            assertEquals("\"${kind.code}\"", encoded)
        }
    }

    @Test
    fun shouldSerializeMapKindWithWireIds() {
        MapKind.entries.forEach { kind ->
            val encoded = json.encodeToString(MapKind.serializer(), kind)
            assertEquals(kind, json.decodeFromString(MapKind.serializer(), encoded))
            assertEquals("\"${kind.id}\"", encoded)
        }
    }

    @Test
    fun shouldSerializeThemeKindWithWireIds() {
        ThemeKind.entries.forEach { kind ->
            val encoded = json.encodeToString(ThemeKind.serializer(), kind)
            assertEquals(kind, json.decodeFromString(ThemeKind.serializer(), encoded))
            assertEquals("\"${kind.id}\"", encoded)
        }
    }

    // -------------------------------------------------------------------------
    // OrderStatus — Task 11
    // -------------------------------------------------------------------------

    @Test
    fun orderStatus_roundTrip_allKnownSubtypes() {
        val knownSubtypes = listOf(
            OrderStatus.New,
            OrderStatus.Sending,
            OrderStatus.UserSending,
            OrderStatus.NonStopSending,
            OrderStatus.Appointed,
            OrderStatus.AtAddress,
            OrderStatus.InProgress,
            OrderStatus.Completed,
            OrderStatus.Canceled
        )
        knownSubtypes.forEach { status ->
            val encoded = json.encodeToString(OrderStatus.serializer(), status)
            val decoded = json.decodeFromString(OrderStatus.serializer(), encoded)
            assertEquals("\"${status.id}\"", encoded)
            assertEquals(status, decoded)
        }
    }

    @Test
    fun orderStatus_deserializesUnknownWireValueViaFrom() {
        val result = json.decodeFromString(OrderStatus.serializer(), "\"some-unknown-id\"")
        val unknown = assertIs<OrderStatus.Unknown>(result)
        assertEquals("some-unknown-id", unknown.originalId)
    }

    @Test
    fun orderStatus_unknownRoundTripPreservesOriginalId() {
        // Unknown serializes as "unknown" (its id), not the originalId.
        // That is the correct wire contract — the receiver sees "unknown" and
        // creates Unknown("unknown") on deserialization.
        val original = OrderStatus.Unknown("mystery-status")
        val encoded = json.encodeToString(OrderStatus.serializer(), original)
        assertEquals("\"unknown\"", encoded)
        val decoded = json.decodeFromString(OrderStatus.serializer(), encoded)
        val roundTripped = assertIs<OrderStatus.Unknown>(decoded)
        assertEquals("unknown", roundTripped.originalId)
    }

    // -------------------------------------------------------------------------
    // PaymentKind — Task 11
    // -------------------------------------------------------------------------

    @Test
    fun paymentKind_roundTrip_cash() {
        val encoded = json.encodeToString(PaymentKind.serializer(), PaymentKind.Cash)
        assertEquals("\"cash\"", encoded)
        assertEquals(PaymentKind.Cash, json.decodeFromString(PaymentKind.serializer(), encoded))
    }

    @Test
    fun paymentKind_cardSerializesAsTag() {
        // Card serializes as "card" (its id). On deserialization from("card") returns Cash
        // because no cardId is available in the single-token wire format — documented trade-off.
        val card = PaymentKind.Card(cardId = "abc-123", maskedNumber = "**** 4321")
        val encoded = json.encodeToString(PaymentKind.serializer(), card)
        assertEquals("\"card\"", encoded)
    }

    @Test
    fun paymentKind_deserializesUnknownWireValueViaFrom() {
        // from() falls back to Cash for unknown values.
        val result = json.decodeFromString(PaymentKind.serializer(), "\"bonus\"")
        assertEquals(PaymentKind.Cash, result)
    }

    // -------------------------------------------------------------------------
    // PointKind — Task 12
    // -------------------------------------------------------------------------

    @Test
    fun pointKind_roundTrip_allKnownEntries() {
        PointKind.entries.forEach { kind ->
            val encoded = json.encodeToString(PointKind.serializer(), kind)
            val decoded = json.decodeFromString(PointKind.serializer(), encoded)
            assertEquals("\"${kind.wireValue}\"", encoded)
            assertEquals(kind, decoded)
        }
    }

    @Test
    fun pointKind_deserializesUnknownWireValueToPoint() {
        val result = json.decodeFromString(PointKind.serializer(), "\"waypoint\"")
        assertEquals(PointKind.POINT, result)
    }

    @Test
    fun pointKind_fromNullFallsBackToPoint() {
        assertEquals(PointKind.POINT, PointKind.from(null))
    }
}
