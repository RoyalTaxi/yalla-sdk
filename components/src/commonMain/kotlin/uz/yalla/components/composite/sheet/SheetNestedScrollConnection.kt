package uz.yalla.components.composite.sheet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
class SheetNestedScrollConnection(
    private val sheetState: ExpandableSheetState,
    private val canScrollUp: () -> Boolean
) : NestedScrollConnection {
    private val draggableState get() = sheetState.anchoredDraggableState

    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        val delta = available.y
        if (delta == 0f) return Offset.Zero

        if (delta < 0f) {
            if (sheetState.fraction < 1f) {
                val consumed = draggableState.dispatchRawDelta(delta)
                return Offset(0f, consumed)
            }
        }

        if (delta > 0f) {
            if (!canScrollUp() && sheetState.fraction > 0f) {
                val consumed = draggableState.dispatchRawDelta(delta)
                return Offset(0f, consumed)
            }
        }

        return Offset.Zero
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        val delta = available.y
        if (delta == 0f) return Offset.Zero

        val consumedBySheet = draggableState.dispatchRawDelta(delta)
        return Offset(0f, consumedBySheet)
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        val velocity = available.y
        val fraction = sheetState.fraction

        if (abs(velocity) < 0.1f) {
            if (fraction > 0.01f && fraction < 0.99f) {
                sheetState.settle(0f)
            }
            return Velocity.Zero
        }

        if (velocity < 0f && fraction < 1f) {
            sheetState.settle(velocity)
            return available
        }

        if (velocity > 0f && !canScrollUp() && fraction > 0f) {
            sheetState.settle(velocity)
            return available
        }

        return Velocity.Zero
    }

    override suspend fun onPostFling(
        consumed: Velocity,
        available: Velocity
    ): Velocity {
        val velocity = available.y
        val fraction = sheetState.fraction

        if (fraction > 0.01f && fraction < 0.99f) {
            sheetState.settle(velocity)
        } else if (abs(velocity) > 0.1f) {
            sheetState.settle(velocity)
        }
        return available
    }
}
