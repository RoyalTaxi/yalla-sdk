package uz.yalla.platform.navigation

/**
 * Marker interface for navigation routes.
 *
 * App extends this with a `@Serializable sealed class` to define its route hierarchy.
 * Serialization enables Decompose's state preservation across process death.
 *
 * ## Usage
 * ```kotlin
 * @Serializable
 * sealed class AppRoute : Route {
 *     @Serializable data object Home : AppRoute()
 *     @Serializable data class Detail(val id: Int) : AppRoute()
 * }
 * ```
 *
 * @since 0.0.5
 */
interface Route

/**
 * Controls the navigation bar title display mode.
 *
 * On iOS, this maps to `UINavigationItem.largeTitleDisplayMode`.
 * On Android, [Always] uses Material3 `LargeTopAppBar`, [Never] uses `TopAppBar`.
 *
 * Large titles are static — no scroll-driven collapse. This is a conscious
 * trade-off: Compose's scroll system does not interoperate with UINavigationBar's
 * scroll tracking (industry-wide unsolved).
 *
 * @since 0.0.5
 */
enum class LargeTitleMode {
    /** Large title always visible. Use for primary screens (Menu, Settings). */
    Always,
    /** Small title only. Use for detail and form screens. */
    Never,
}

/**
 * Per-screen navigation configuration.
 *
 * Returned by [ScreenProvider.configFor] for each route. The platform's native
 * navigation bar uses this to configure title, visibility, and appearance.
 *
 * @property title Screen title displayed in the navigation bar. `null` hides the title.
 *   Use plain [String] (not StringResource) because iOS sets `navigationItem.title`
 *   outside a `@Composable` context.
 * @property largeTitleMode Whether to use large or small title style.
 * @property showsNavigationBar Whether the navigation bar is visible for this screen.
 * @property transparentNavigationBar Whether the navigation bar background is transparent.
 * @since 0.0.5
 */
data class ScreenConfig(
    val title: String? = null,
    val largeTitleMode: LargeTitleMode = LargeTitleMode.Never,
    val showsNavigationBar: Boolean = true,
    val transparentNavigationBar: Boolean = false,
)
