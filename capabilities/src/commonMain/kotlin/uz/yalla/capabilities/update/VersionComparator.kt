package uz.yalla.capabilities.update

internal object VersionComparator {
    /**
     * Returns `true` when [storeVersion] is strictly newer than [installedVersion].
     *
     * Versions are compared as dotted runs of integers; non-numeric segments
     * (e.g. the `-beta` in `1.2.0-beta`) are dropped, so a pre-release compares
     * equal to its release. Missing trailing segments are treated as `0`
     * (`1.0` == `1.0.0`).
     *
     * Fails closed: if [installedVersion] has no parsable numeric segment (blank
     * or all-garbage), the installed version is treated as "unknown" and the
     * function returns `false`, so an undeterminable version can never trigger an
     * update prompt. (Guards the iOS force-update path against a missing
     * `CFBundleShortVersionString`.)
     */
    fun isNewer(
        storeVersion: String,
        installedVersion: String
    ): Boolean {
        val storeParts = storeVersion.split(".").mapNotNull { it.toIntOrNull() }
        val installedParts = installedVersion.split(".").mapNotNull { it.toIntOrNull() }
        // Can't determine the installed version → never report newer (fail closed).
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
