package uz.yalla.capabilities.update

internal data class StoreInfo(
    val version: String,
    val storeUrl: String
)

internal fun parseStoreInfo(payload: Map<Any?, *>?): StoreInfo? {
    val results = payload?.get("results") as? List<*>
    val first = results?.firstOrNull() as? Map<*, *> ?: return null
    val version = first["version"] as? String ?: return null
    val storeUrl = first["trackViewUrl"] as? String ?: return null
    return if (isTrustedAppleStoreUrl(storeUrl)) StoreInfo(version, storeUrl) else null
}

internal fun isTrustedAppleStoreUrl(url: String?): Boolean {
    val trimmed = url?.trim()?.lowercase() ?: return false
    if (!trimmed.startsWith("https://")) return false
    val host = trimmed.removePrefix("https://").substringBefore('/').substringBefore(':')
    return host == "apple.com" || host.endsWith(".apple.com")
}
