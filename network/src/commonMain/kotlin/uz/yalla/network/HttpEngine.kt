package uz.yalla.network

import io.ktor.client.engine.HttpClientEngine

expect fun createHttpEngine(): HttpClientEngine
