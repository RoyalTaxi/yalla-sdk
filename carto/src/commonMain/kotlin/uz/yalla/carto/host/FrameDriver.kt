package uz.yalla.carto.host

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import kotlinx.coroutines.isActive
import uz.yalla.carto.state.MapSink

@Composable
internal fun FrameDriver(sink: MapSink) {
    LaunchedEffect(sink) {
        while (isActive) {
            withFrameNanos { nanos -> sink.reportFrame(nanos / 1_000_000L) }
        }
    }
}
