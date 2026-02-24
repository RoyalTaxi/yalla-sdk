package uz.yalla.data.network

data class NetworkConfig(
    val baseUrl: String,
    val brandId: String,
    val secretKey: String,
    val userAgentOS: String = "",
    val deviceType: String = "client",
    val deviceMode: String = "mobile",
)
