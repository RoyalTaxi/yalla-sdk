package uz.yalla.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.engine.darwin.certificates.CertificatePinner

internal actual fun createHttpEngine(certificatePins: List<CertificatePin>): HttpClientEngine =
    Darwin.create {
        if (certificatePins.isNotEmpty()) {
            val builder = CertificatePinner.Builder()
            certificatePins.forEach { pin -> builder.add(pin.host, *pin.pins.toTypedArray()) }
            handleChallenge(builder.build())
        }
    }
