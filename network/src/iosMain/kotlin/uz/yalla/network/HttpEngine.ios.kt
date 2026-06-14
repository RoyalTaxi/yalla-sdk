package uz.yalla.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

public actual fun createHttpEngine(): HttpClientEngine = Darwin.create()
