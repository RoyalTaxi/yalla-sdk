package uz.yalla.carto.camera

public class CameraArbiter internal constructor(
    private val apply: suspend (CameraIntent) -> Unit
) {
    public suspend fun submit(intent: CameraIntent) {
        apply(intent)
    }
}
