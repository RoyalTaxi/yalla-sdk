package uz.yalla.components.composites.sheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

public data class SheetTransition<T : Any>(
    val child: T?,
    val isVisible: Boolean
)

@Composable
public fun <T : Any> rememberSheetTransition(
    current: T?,
    hideDurationMillis: Long = SHEET_HIDE_DURATION_MILLIS
): SheetTransition<T> {
    var rendered by remember { mutableStateOf(current) }
    var visible by remember { mutableStateOf(current != null) }

    LaunchedEffect(current) {
        when {
            current == rendered -> visible = current != null

            rendered == null || !visible -> {
                rendered = current
                visible = current != null
            }

            else -> {
                visible = false
                delay(hideDurationMillis)
                rendered = current
                visible = current != null
            }
        }
    }

    return SheetTransition(child = rendered, isVisible = visible)
}

private const val SHEET_HIDE_DURATION_MILLIS = 350L
