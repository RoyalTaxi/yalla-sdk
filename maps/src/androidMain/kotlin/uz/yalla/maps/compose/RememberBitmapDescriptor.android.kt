package uz.yalla.maps.compose

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.google.android.gms.maps.model.BitmapDescriptorFactory as GoogleBitmapDescriptorFactory

internal actual fun ImageBitmap.toBitmapDescriptor(): BitmapDescriptor =
    BitmapDescriptor(GoogleBitmapDescriptorFactory.fromBitmap(asAndroidBitmap()))
