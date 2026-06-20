package uz.yalla.network

import io.ktor.client.engine.HttpClientEngine

internal expect fun createHttpEngine(): HttpClientEngine
