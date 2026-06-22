package uz.yalla.network

import io.ktor.client.engine.HttpClientEngine

internal expect fun createHttpEngine(certificatePins: List<CertificatePin> = emptyList()): HttpClientEngine
