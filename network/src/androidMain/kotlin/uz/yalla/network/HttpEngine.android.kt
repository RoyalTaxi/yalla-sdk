package uz.yalla.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android

public actual fun createHttpEngine(): HttpClientEngine = Android.create()
