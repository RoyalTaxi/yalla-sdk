package uz.yalla.maps.api

import android.content.Context
import android.view.View
import androidx.lifecycle.Lifecycle

public interface AndroidMapController {
    public fun createView(
        context: Context,
        lifecycle: Lifecycle
    ): View

    public fun detach()
}
