package uz.yalla.maps.compose

expect class BitmapDescriptor

expect object BitmapDescriptorFactory {
    fun fromBytes(
        bytes: ByteArray,
        width: Int,
        height: Int
    ): BitmapDescriptor

    fun fromEncodedImage(data: ByteArray): BitmapDescriptor
}
