package uz.yalla.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

// TODO(quality, needs-decision): no TLS certificate / public-key pinning on the Darwin engine —
//  blocked on real SPKI pin material + a rotation procedure. Do not invent pins. Wire a Darwin
//  handleChallenge / URLSession delegate (or a pinning hook on NetworkConfig) once the pins are provided.
internal actual fun createHttpEngine(): HttpClientEngine = Darwin.create()
