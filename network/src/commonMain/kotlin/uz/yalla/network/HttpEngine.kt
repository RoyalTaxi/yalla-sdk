package uz.yalla.network

import io.ktor.client.engine.HttpClientEngine

/**
 * Builds the platform's default Ktor engine, wiring in TLS certificate pinning when [certificatePins]
 * is non-empty. An empty list (the default) builds a plain engine that uses normal CA trust — pinning is
 * opt-in deployment config, see [CertificatePin] and [NetworkConfig.certificatePins].
 *
 * The pinning mechanism differs per engine because their TLS hooks differ (Darwin ships a first-class
 * `CertificatePinner`; the Android `HttpURLConnection` engine exposes an `sslManager` we drive with a
 * SPKI-matching trust manager) — see each `actual`.
 */
internal expect fun createHttpEngine(
    certificatePins: List<CertificatePin> = emptyList()
): HttpClientEngine
