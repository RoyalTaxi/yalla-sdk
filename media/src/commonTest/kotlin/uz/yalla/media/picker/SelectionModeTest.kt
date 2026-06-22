package uz.yalla.media.picker

import kotlin.test.Test
import kotlin.test.assertEquals

class SelectionModeTest {
    @Test
    fun singleMapsToOne() {
        assertEquals(1, SelectionMode.Single.toSelectionLimit())
    }

    @Test
    fun infinityMapsToZero() {
        assertEquals(0, SelectionMode.Multiple(SelectionMode.INFINITY).toSelectionLimit())
        assertEquals(0, SelectionMode.Multiple().toSelectionLimit())
    }

    @Test
    fun explicitCapPassesThrough() {
        assertEquals(1, SelectionMode.Multiple(1).toSelectionLimit())
        assertEquals(5, SelectionMode.Multiple(5).toSelectionLimit())
    }
}
