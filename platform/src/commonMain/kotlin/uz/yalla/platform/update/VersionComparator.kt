package uz.yalla.platform.update

/**
 * Utility for comparing semantic version strings.
 *
 * Splits version strings on `.` and compares each numeric segment left-to-right.
 * Missing segments are treated as `0` (e.g., `"2.1"` equals `"2.1.0"`).
 *
 * @since 0.0.1
 */
object VersionComparator {
    /**
     * Returns `true` if [storeVersion] is strictly newer than [installedVersion].
     *
     * Compares semver-style version strings segment by segment
     * (e.g., `"2.1.0"` vs `"2.0.3"`).
     *
     * @param storeVersion Version string from the app store.
     * @param installedVersion Currently installed version string.
     * @return `true` when the store version is higher.
     * @since 0.0.1
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
