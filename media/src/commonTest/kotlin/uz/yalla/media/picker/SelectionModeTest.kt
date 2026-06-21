package uz.yalla.media.picker

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Pins the single [SelectionMode.toSelectionLimit] mapping both platform actuals now share, so the
 * picker's selection-limit contract can't drift between Android and iOS again (review media.md
 * #18): `1` for a single pick, `0` for unlimited ([SelectionMode.INFINITY]), the cap otherwise.
 */
class SelectionModeTest {
    @Test
    fun singleMapsToOne() {
        assertEquals(1, SelectionMode.Single.toSelectionLimit())
    }

    @Test
    fun infinityMapsToZero() {
        assertEquals(0, SelectionMode.Multiple(SelectionMode.INFINITY).toSelectionLimit())
        // INFINITY is the default for Multiple.
        assertEquals(0, SelectionMode.Multiple().toSelectionLimit())
    }

    @Test
    fun explicitCapPassesThrough() {
        assertEquals(1, SelectionMode.Multiple(1).toSelectionLimit())
        assertEquals(5, SelectionMode.Multiple(5).toSelectionLimit())
    }
}
