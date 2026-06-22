package uz.yalla.telemetry.sink

import uz.yalla.telemetry.event.event
import kotlin.test.Test
import kotlin.test.assertEquals

class DebugLogSinkTest {
    @Test
    fun rendersNameOnlyWhenNoParams() {
        assertEquals("[Telemetry] login_view", renderLine(event("login_view")))
    }

    @Test
    fun rendersNameAndParamsWhenPresent() {
        val line = renderLine(event("rating_submitted") { put("rating", 5) })
        assertEquals("[Telemetry] rating_submitted {rating=Count(value=5)}", line)
    }
}
