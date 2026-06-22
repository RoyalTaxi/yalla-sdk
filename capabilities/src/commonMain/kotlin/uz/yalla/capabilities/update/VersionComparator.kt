package uz.yalla.capabilities.update

internal object VersionComparator {
    fun isNewer(
        storeVersion: String,
        installedVersion: String
    ): Boolean {
        val storeParts = storeVersion.split(".").mapNotNull { it.toIntOrNull() }
        val installedParts = installedVersion.split(".").mapNotNull { it.toIntOrNull() }
        if (installedParts.isEmpty()) return false
        val maxLen = maxOf(storeParts.size, installedParts.size)
        for (i in 0 until maxLen) {
            val s = storeParts.getOrElse(i) { 0 }
            val c = installedParts.getOrElse(i) { 0 }
            if (s > c) return true
            if (s < c) return false
        }
        return false
    }
}
