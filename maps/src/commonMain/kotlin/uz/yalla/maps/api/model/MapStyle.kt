package uz.yalla.maps.api.model

import androidx.compose.runtime.Immutable

/**
 * The map style to render. Style content (URLs and inline JSON) is handed verbatim to the native
 * map engine, which will fetch whatever tile/sprite/glyph endpoints the style names — so style
 * content is trusted-caller input and must come from a trusted origin. [Url] enforces an
 * `https://` scheme as a baseline guardrail; [InlineJson] is not validated.
 */
@Immutable
public sealed class MapStyle {
    /**
     * A remote style document, light and dark variants. Both URLs must be `https://` — a non-https
     * URL is rejected at construction to avoid pointing the engine at a cleartext/attacker host.
     */
    public data class Url(
        val lightUrl: String,
        val darkUrl: String
    ) : MapStyle() {
        init {
            require(lightUrl.startsWith("https://")) { "MapStyle.Url.lightUrl must be https://" }
            require(darkUrl.startsWith("https://")) { "MapStyle.Url.darkUrl must be https://" }
        }
    }

    /** Inline style JSON, light and dark variants. Loaded verbatim by the engine; trusted input. */
    public data class InlineJson(
        val lightJson: String,
        val darkJson: String
    ) : MapStyle()

    /** Defer to the platform engine's built-in default style. */
    public data object PlatformDefault : MapStyle()

    public companion object {
        /** The bundled default: CARTO positron (light) / dark-matter (dark) over HTTPS, no API key. */
        public val CARTO: Url =
            Url(
                lightUrl = "https://basemaps.cartocdn.com/gl/positron-gl-style/style.json",
                darkUrl = "https://basemaps.cartocdn.com/gl/dark-matter-gl-style/style.json"
            )
    }
}
