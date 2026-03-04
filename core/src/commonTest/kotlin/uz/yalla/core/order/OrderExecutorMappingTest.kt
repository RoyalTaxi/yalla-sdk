package uz.yalla.core.order

import kotlin.test.Test
import kotlin.test.assertEquals

class OrderExecutorMappingTest {
    @Test
    fun shouldMapOrderExecutorToDomainExecutorWithZeroDistance() {
        val orderExecutor =
            Order.Executor(
                coords =
                    Order.Executor.Coords(
                        heading = 90.0,
                        lat = 41.3111,
                        lng = 69.2797
                    ),
                vehicle =
                    Order.Executor.Vehicle(
                        callsign = "A1",
                        color =
                            Order.Executor.Vehicle.Color(
                                color = "#FFFFFF",
                                name = "White"
                            ),
                        id = 100,
                        mark = "Chevrolet",
                        model = "Cobalt",
                        stateNumber = "01A123BC"
                    ),
                fatherName = "Father",
                givenNames = "Driver",
                id = 10,
                phone = "+998900000000",
                photo = "photo.png",
                rating = 5.0,
                surName = "Surname"
            )

        val mapped = orderExecutor.toExecutor()

        assertEquals(10, mapped.id)
        assertEquals(41.3111, mapped.lat)
        assertEquals(69.2797, mapped.lng)
        assertEquals(90.0, mapped.heading)
        assertEquals(0.0, mapped.distance)
    }
}
