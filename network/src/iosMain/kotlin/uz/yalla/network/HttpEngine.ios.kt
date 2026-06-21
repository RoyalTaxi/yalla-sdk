package uz.yalla.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.engine.darwin.certificates.CertificatePinner

/**
 * Darwin engine with TLS certificate pinning wired through Ktor's first-class [CertificatePinner],
 * which installs an `NSURLSession` challenge handler that checks each pinned host's SPKI hash against
 * the presented chain. When [certificatePins] is empty (the default) no handler is installed and the
 * client uses the system's normal CA trust — see [CertificatePin] for how the host supplies real pins
 * and why this is deployment config, not a baked-in constant.
 */
internal actual fun createHttpEngine(certificatePins: List<CertificatePin>): HttpClientEngine =
    Darwin.create {
        if (certificatePins.isNotEmpty()) {
            val builder = CertificatePinner.Builder()
            certificatePins.forEach { pin -> builder.add(pin.host, *pin.pins.toTypedArray()) }
            handleChallenge(builder.build())
        }
    }
