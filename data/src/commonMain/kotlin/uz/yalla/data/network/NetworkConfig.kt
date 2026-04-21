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
 * @property guestAllowedSegments last-path-segment tokens permitted by
 *   [createGuestModeGuardPlugin] when guest mode is active. Defaults to the six
 *   legacy endpoints (`client`, `valid`, `register`, `location-name`, `cost`,
 *   `lists`) so existing consumers keep working without opting in. Override for
 *   custom deploys that expose a different whitelist. Checked via last-segment
 *   membership — e.g. `"cost"` matches any URL whose final path segment is
 *   `cost`. See [createGuestModeGuardPlugin] for matching semantics.
 * @see createHttpClient
 * @see createGuestModeGuardPlugin
 * @since 0.0.1
 */
data class NetworkConfig(
    val baseUrl: String,
    val brandId: String,
    val secretKey: String,
    val deviceType: String = "client",
    val deviceMode: String = "mobile",
    val guestAllowedSegments: List<String> = DEFAULT_GUEST_ALLOWED_SEGMENTS,
)

/**
 * Default whitelist preserved from the original hardcoded
 * [GuestModeGuard][createGuestModeGuardPlugin] implementation — the six
 * last-path-segment tokens that public-facing guest-mode flows required before
 * this whitelist became configurable. Kept as an ordered list so the default
 * is deterministic and diff-friendly; [createGuestModeGuardPlugin] converts to
 * a `Set` internally for O(1) membership checks.
 *
 * @since 0.0.9
 */
val DEFAULT_GUEST_ALLOWED_SEGMENTS: List<String> = listOf(
    "client",
    "valid",
    "register",
    "location-name",
    "cost",
    "lists",
)
