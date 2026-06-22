package uz.yalla.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.CertificatePinner

internal actual fun createHttpEngine(certificatePins: List<CertificatePin>): HttpClientEngine =
    OkHttp.create {
        if (certificatePins.isNotEmpty()) {
            val pinner =
                CertificatePinner
                    .Builder()
                    .apply {
                        certificatePins.forEach { pin -> pin.pins.forEach { value -> add(pin.host, value) } }
                    }.build()
            config {
                certificatePinner(pinner)
            }
        }
    }
