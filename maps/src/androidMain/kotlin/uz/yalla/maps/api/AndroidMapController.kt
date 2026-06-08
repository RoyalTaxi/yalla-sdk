package uz.yalla.maps.api

import android.content.Context
import android.view.View
import androidx.lifecycle.Lifecycle

interface AndroidMapController {
    fun createView(context: Context, lifecycle: Lifecycle): View

    fun detach()
}
