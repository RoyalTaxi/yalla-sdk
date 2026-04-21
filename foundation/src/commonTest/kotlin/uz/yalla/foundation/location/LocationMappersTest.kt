package uz.yalla.foundation.location

import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.location.Address
import uz.yalla.core.location.AddressOption
import uz.yalla.core.location.PlaceKind
import uz.yalla.core.location.SavedAddress
import uz.yalla.core.order.Order
import uz.yalla.core.order.OrderStatus
import uz.yalla.core.payment.PaymentKind
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LocationMappersTest {

    @Test
    fun shouldMapAddressOptionToFoundLocation() {
        val option = AddressOption(
            id = 42, title = "Office", address = "123 Main St",
            distance = 1.5, lat = 41.3, lng = 69.2, isFromDatabase = false
        )

        val result = option.toFoundLocation()

        assertEquals(42, result.id)
        assertEquals("Office", result.name)
        assertEquals("123 Main St", result.address)
        assertEquals(GeoPoint(41.3, 69.2), result.point)
        assertNull(result.placeKind)
    }

    @Test
    fun shouldMapSavedAddressToFoundLocation() {
        val saved = SavedAddress(
            distance = 2.0, duration = 10.0, lat = 41.3, lng = 69.2,
            address = "456 Elm St", title = "Home",
            kind = PlaceKind.Home, parent = SavedAddress.Parent(name = null)
        )

        val result = saved.toFoundLocation()

        assertNull(result.id)
        assertEquals("Home", result.name)
        assertEquals("456 Elm St", result.address)
        assertEquals(GeoPoint(41.3, 69.2), result.point)
        assertEquals(PlaceKind.Home, result.placeKind)
    }

    @Test
    fun shouldMapAddressToLocation() {
        val address = Address(id = 7, name = "Tashkent", lat = 41.3, lng = 69.2, isFromDatabase = true)

        val result = address.toLocation()

        assertEquals(7, result.id)
        assertEquals("Tashkent", result.name)
        assertEquals(GeoPoint(41.3, 69.2), result.point)
    }

    @Test
    fun shouldMapAddressToLocationWithCustomPoint() {
        val address = Address(id = 7, name = "Tashkent", lat = 41.3, lng = 69.2, isFromDatabase = true)
        val custom = GeoPoint(lat = 40.0, lng = 70.0)

        val result = address.toLocation(point = custom)

        assertEquals(7, result.id)
        assertEquals("Tashkent", result.name)
        assertEquals(custom, result.point)
    }

    @Test
    fun shouldMapRouteToLocation() {
        val route = Order.Taxi.Route(
            coords = Order.Taxi.Route.Coords(lat = 41.3, lng = 69.2),
            fullAddress = "789 Oak Ave",
            index = 1
        )

        val result = route.toLocation()

        assertEquals(1, result.id)
        assertEquals("789 Oak Ave", result.name)
        assertEquals(GeoPoint(41.3, 69.2), result.point)
    }

    @Test
    fun shouldSortRouteLocationsByIndex() {
        val order = testOrder(
            routes = listOf(
                Order.Taxi.Route(Order.Taxi.Route.Coords(3.0, 3.0), "C", index = 3),
                Order.Taxi.Route(Order.Taxi.Route.Coords(1.0, 1.0), "A", index = 1),
                Order.Taxi.Route(Order.Taxi.Route.Coords(2.0, 2.0), "B", index = 2),
            )
        )

        val result = order.sortedRouteLocations()

        assertEquals(3, result.size)
        assertEquals("A", result[0].name)
        assertEquals("B", result[1].name)
        assertEquals("C", result[2].name)
    }

    @Test
    fun shouldMapFoundLocationToLocation() {
        val found = FoundLocation(
            id = 10, name = "Found Place",
            address = "should be dropped",
            point = GeoPoint(41.3, 69.2),
            placeKind = PlaceKind.Work
        )

        val result = found.toLocation()

        assertEquals(10, result.id)
        assertEquals("Found Place", result.name)
        assertEquals(GeoPoint(41.3, 69.2), result.point)
    }

    // --- Test helpers ---

    private fun testOrder(routes: List<Order.Taxi.Route>) = Order(
        comment = "",
        dateTime = 0L,
        executor = Order.Executor(
            coords = Order.Executor.Coords(0.0, 0.0, 0.0),
            vehicle = Order.Executor.Vehicle(
                callsign = "", color = Order.Executor.Vehicle.Color("", ""),
                id = 0, mark = "", model = "", stateNumber = ""
            ),
            fatherName = "", givenNames = "", id = 0, phone = "", photo = "", rating = 0.0, surName = ""
        ),
        id = 1,
        paymentType = PaymentKind.Cash,
        service = "",
        status = OrderStatus.New,
        statusTime = emptyList(),
        taxi = Order.Taxi(
            bonusAmount = 0, clientTotalPrice = 0.0, distance = 0.0, fixedPrice = false,
            routes = routes, services = emptyList(), startPrice = 0,
            tariff = "", tariffId = 0, totalPrice = 0, waitingTime = 0
        )
    )
}
