package uz.yalla.data.api

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ApiResponseTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun shouldDeserializeApiResponse() {
        val input = """{"result": "hello"}"""

        val response = json.decodeFromString<ApiResponse<String>>(input)

        assertEquals("hello", response.result)
    }

    @Test
    fun shouldDeserializeApiResponseWithNullResult() {
        val input = """{}"""

        val response = json.decodeFromString<ApiResponse<String>>(input)

        assertNull(response.result)
    }

    @Test
    fun shouldDeserializeApiListResponse() {
        val input = """{"list": ["a", "b", "c"]}"""

        val response = json.decodeFromString<ApiListResponse<String>>(input)

        assertEquals(listOf("a", "b", "c"), response.list)
    }

    @Test
    fun shouldDeserializeApiListResponseWithNullList() {
        val input = """{}"""

        val response = json.decodeFromString<ApiListResponse<String>>(input)

        assertNull(response.list)
    }

    @Test
    fun shouldDeserializeApiErrorResponse() {
        val input = """{"message": "Not found"}"""

        val response = json.decodeFromString<ApiErrorResponse>(input)

        assertEquals("Not found", response.message)
    }

    @Test
    fun shouldDeserializeApiErrorResponseWithNullMessage() {
        val input = """{}"""

        val response = json.decodeFromString<ApiErrorResponse>(input)

        assertNull(response.message)
    }
}
