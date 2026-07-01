package uz.yalla.carto.state

import uz.yalla.carto.camera.CameraView

public interface MapSink {
    public fun report(event: MapEvent)

    public fun reportCamera(view: CameraView)

    public fun reportReady(ready: Boolean)

    public fun reportFrame(frameTimeMs: Long)
}
