package uz.yalla.platform.update

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class AppUpdateState {
    var isUpdateAvailable by mutableStateOf(false)
        internal set
    var storeUrl by mutableStateOf("")
        internal set
    var isChecking by mutableStateOf(true)
        internal set
}
