package uz.yalla.data.network

/**
 * Configuration for HTTP client initialization.
 *
 * Encapsulates all environment-specific values required by
 * [createHttpClient] to construct request headers and set the base URL.
 * Typically provided via build flavors (dev / staging / prod).
 *
 * @property baseUrl root URL for API requests (e.g. `"https://api.yalla.uz/"`)
 * @property brandId brand identifier sent in the `brand-id` request header
 * @property secretKey API secret key sent in the `secret-key` request header
 * @property deviceType device category sent in the `Device` header, defaults to `"client"`
 * @property deviceMode device form factor sent in the `Device-Mode` header, defaults to `"mobile"`
 * @see createHttpClient
 * @since 0.0.1
 */
data class NetworkConfig(
    val baseUrl: String,
    val brandId: String,
    val secretKey: String,
    val deviceType: String = "client",
    val deviceMode: String = "mobile",
)
