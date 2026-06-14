package uz.yalla.capabilities.update

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
public class AppUpdateState {
    public var isUpdateAvailable: Boolean by mutableStateOf(false)
        internal set

    public var storeUrl: String by mutableStateOf("")
        internal set

    public var isChecking: Boolean by mutableStateOf(true)
        internal set
}
