package uz.yalla.data.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import uz.yalla.core.contract.preferences.InterfacePreferences
import uz.yalla.core.contract.preferences.PositionPreferences
import uz.yalla.core.contract.preferences.SessionPreferences

expect fun provideNetworkClient(
    config: NetworkConfig,
    sessionPrefs: SessionPreferences,
    interfacePrefs: InterfacePreferences,
    positionPrefs: PositionPreferences,
    inspektifySetup: (HttpClientConfig<*>.() -> Unit)? = null,
): HttpClient
