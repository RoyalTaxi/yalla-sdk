package uz.yalla.platform.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ToolbarStateTest {
    @Test
    fun shouldStartWithEmptyActions() {
        val state = ToolbarState()
        assertTrue(state.actions.isEmpty())
    }

    @Test
    fun shouldUpdateActionsWhenSet() {
        val state = ToolbarState()
        val actions = listOf(
            ToolbarAction.Icon(ToolbarIcon.Edit) {},
            ToolbarAction.Text("Save") {},
        )
        state.actions = actions

        assertEquals(2, state.actions.size)
    }

    @Test
    fun shouldReplaceActionsOnSubsequentSets() {
        val state = ToolbarState()
        state.actions = listOf(ToolbarAction.Text("A") {})
        state.actions = listOf(ToolbarAction.Text("B") {}, ToolbarAction.Text("C") {})

        assertEquals(2, state.actions.size)
        assertEquals("B", (state.actions[0] as ToolbarAction.Text).label)
    }

    @Test
    fun shouldClearActionsWhenSetToEmpty() {
        val state = ToolbarState()
        state.actions = listOf(ToolbarAction.Text("A") {})
        state.actions = emptyList()

        assertTrue(state.actions.isEmpty())
    }
}
