package uz.yalla.foundation.input

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager

/**
 * Clears the current focus (and dismisses the soft keyboard) when the user taps anywhere on the
 * modified element. Apply it to a screen's root container to dismiss text-field focus on outside taps.
 */
@Composable
public fun Modifier.clearFocusOnTap(): Modifier {
    val focusManager = LocalFocusManager.current
    return this.pointerInput(Unit) {
        detectTapGestures(onTap = { focusManager.clearFocus() })
    }
}
