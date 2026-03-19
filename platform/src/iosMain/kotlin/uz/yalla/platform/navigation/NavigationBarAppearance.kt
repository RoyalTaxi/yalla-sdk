package uz.yalla.platform.navigation

/**
 * Global appearance configuration for the iOS navigation bar.
 * Colors use ARGB Long encoding.
 * @since 0.0.5
 */
data class NavigationBarAppearance(
    val tintColor: Long = 0,
    val titleColor: Long = 0,
    val backgroundColor: Long = 0,
    val isTranslucent: Boolean = true,
    val showsSeparator: Boolean = true,
    val largeTitleFontName: String? = null,
    val titleFontName: String? = null,
)
