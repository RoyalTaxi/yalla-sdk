package uz.yalla.core.error

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class DataErrorTest {
    @Test
    fun connectionIsSingletonAndDataErrorAndNetwork() {
        assertEquals(DataError.Network.Connection, DataError.Network.Connection)
        assertTrue(DataError.Network.Connection is DataError)
        assertTrue(DataError.Network.Connection is DataError.Network)
    }

    @Test
    fun timeoutIsSingleton() {
        assertEquals(DataError.Network.Timeout, DataError.Network.Timeout)
        assertTrue(DataError.Network.Timeout is DataError.Network)
    }

    @Test
    fun serverIsSingleton() {
        assertEquals(DataError.Network.Server, DataError.Network.Server)
        assertTrue(DataError.Network.Server is DataError.Network)
    }

    @Test
    fun clientIsSingleton() {
        assertEquals(DataError.Network.Client, DataError.Network.Client)
        assertTrue(DataError.Network.Client is DataError.Network)
    }

    @Test
    fun serializationIsSingleton() {
        assertEquals(DataError.Network.Serialization, DataError.Network.Serialization)
        assertTrue(DataError.Network.Serialization is DataError.Network)
    }

    @Test
    fun guestIsSingleton() {
        assertEquals(DataError.Network.Guest, DataError.Network.Guest)
        assertTrue(DataError.Network.Guest is DataError.Network)
    }

    @Test
    fun unknownIsSingleton() {
        assertEquals(DataError.Network.Unknown, DataError.Network.Unknown)
        assertTrue(DataError.Network.Unknown is DataError.Network)
    }

    @Test
    fun networkVariantsAreDistinct() {
        // Sanity: data objects within the same sealed class compare by identity-as-equality;
        // distinct objects must not collide.
        val variants =
            listOf(
                DataError.Network.Connection,
                DataError.Network.Timeout,
                DataError.Network.Server,
                DataError.Network.Client,
                DataError.Network.Serialization,
                DataError.Network.Guest,
                DataError.Network.Unknown
            )
        for (i in variants.indices) {
            for (j in variants.indices) {
                if (i != j) assertNotEquals(variants[i], variants[j])
            }
        }
    }

    @Test
    fun clientWithMessageEqualsByCodeAndMessage() {
        assertEquals(
            DataError.Network.ClientWithMessage(code = 400, message = "bad request"),
            DataError.Network.ClientWithMessage(code = 400, message = "bad request")
        )
    }

    @Test
    fun clientWithMessageDiffersByCode() {
        assertNotEquals(
            DataError.Network.ClientWithMessage(code = 400, message = "x"),
            DataError.Network.ClientWithMessage(code = 422, message = "x")
        )
    }

    @Test
    fun clientWithMessageDiffersByMessage() {
        assertNotEquals(
            DataError.Network.ClientWithMessage(code = 400, message = "a"),
            DataError.Network.ClientWithMessage(code = 400, message = "b")
        )
    }

    @Test
    fun clientWithMessageIsNetworkAndDataError() {
        val err: DataError = DataError.Network.ClientWithMessage(code = 500, message = "boom")
        assertTrue(err is DataError.Network)
        assertTrue(err is DataError.Network.ClientWithMessage)
    }
}
