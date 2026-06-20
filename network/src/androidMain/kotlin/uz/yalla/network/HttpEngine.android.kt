package uz.yalla.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.CertificatePinner

/**
 * Android engine (Ktor OkHttp) with TLS certificate pinning.
 *
 * The OkHttp engine ships OkHttp's battle-tested [CertificatePinner], so pinning is configured directly on
 * the `OkHttpClient.Builder` instead of a hand-rolled trust manager. Each [CertificatePin] host is
 * registered with its `sha256/<base64-SPKI>` pins; OkHttp applies the same wildcard host semantics
 * (`*.host` single-label, `**.host` any-depth) that [CertificatePin] documents, so the Android and Darwin
 * engines agree.
 *
 * When [certificatePins] is empty (the default) no pinner is installed and the engine uses the platform's
 * normal CA trust — see [CertificatePin] for how the host supplies real pins and why this is deployment
 * configuration the SDK cannot ship.
 */
internal actual fun createHttpEngine(
    certificatePins: List<CertificatePin>
): HttpClientEngine = OkHttp.create {
    if (certificatePins.isNotEmpty()) {
        val pinner = CertificatePinner.Builder().apply {
            certificatePins.forEach { pin -> add(pin.host, *pin.pins.toTypedArray()) }
        }.build()
        config {
            certificatePinner(pinner)
        }
    }
}
