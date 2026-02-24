package uz.yalla.data.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import uz.yalla.core.contract.AppPreferences
import uz.yalla.core.contract.StaticPreferences

expect fun provideNetworkClient(
    config: NetworkConfig,
    appPrefs: AppPreferences,
    staticPrefs: StaticPreferences,
    inspektifySetup: (HttpClientConfig<*>.() -> Unit)? = null,
): HttpClient
