package uz.yalla.platform.update

object VersionComparator {
    /**
     * Returns true if [storeVersion] is newer than [installedVersion].
     * Compares semver-style version strings (e.g. "2.1.0" vs "2.0.3").
     */
    fun isNewer(
        storeVersion: String,
        installedVersion: String
    ): Boolean {
        val storeParts = storeVersion.split(".").mapNotNull { it.toIntOrNull() }
        val installedParts = installedVersion.split(".").mapNotNull { it.toIntOrNull() }
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
