package uz.yalla.data.network

/**
 * Configuration for HTTP client initialization.
 *
 * @property baseUrl root URL for API requests
 * @property brandId brand identifier sent in request headers
 * @property secretKey API secret key for authentication headers
 * @property deviceType device category, defaults to `"client"`
 * @property deviceMode device form factor, defaults to `"mobile"`
 * @since 0.0.1
 */
data class NetworkConfig(
    val baseUrl: String,
    val brandId: String,
    val secretKey: String,
    val deviceType: String = "client",
    val deviceMode: String = "mobile",
)
