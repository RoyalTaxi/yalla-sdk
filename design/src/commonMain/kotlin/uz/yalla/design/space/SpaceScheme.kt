package uz.yalla.design.space

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Semantic spacing scheme for the Yalla design system.
 *
 * Organizes spacing tokens into two layers: a small set of top-level **semantic**
 * aliases consumers should prefer (`screenEdge`, `sheetEdge`, `itemGap`, …) and a
 * nested [Scale] of t-shirt-sized primitives that act as an escape hatch when no
 * semantic name fits. The two-layer shape intentionally makes the semantic call
 * shorter to type so correct usage wins on ergonomics.
 *
 * Access via `System.space` inside a
 * [YallaTheme][uz.yalla.design.theme.YallaTheme].
 *
 * ## Usage
 *
 * ```kotlin
 * // Prefer semantic:
 * Column(modifier = Modifier.padding(horizontal = System.space.screenEdge)) { … }
 *
 * Column(verticalArrangement = Arrangement.spacedBy(System.space.itemGap)) { … }
 *
 * // Escape hatch when semantic doesn't fit:
 * Spacer(modifier = Modifier.height(System.space.scale.xl))
 * ```
 *
 * Custom schemes can be constructed directly for previews or white-labeling;
 * [standardSpaceScheme] returns the Yalla defaults.
 *
 * @property screenEdge Horizontal padding between screen content and the screen edge.
 * @property sheetEdge Horizontal padding inside a bottom-sheet surface.
 * @property contentEdge Horizontal padding for content-dense screens (lists, settings).
 * @property itemGap Vertical gap between adjacent items in a list or column.
 * @property sectionGap Gap between major content sections on a screen.
 * @property heroGap Gap between a hero title/image and surrounding content.
 * @property inlineGap Tight gap between inline elements (icon+text, chip+chip).
 * @property scale Primitive t-shirt scale; used when a semantic alias doesn't fit.
 * @since 0.0.13
 */
data class SpaceScheme(
    val screenEdge: Dp,
    val sheetEdge: Dp,
    val contentEdge: Dp,
    val itemGap: Dp,
    val sectionGap: Dp,
    val heroGap: Dp,
    val inlineGap: Dp,
    val scale: Scale,
) {
    /**
     * Primitive t-shirt spacing scale.
     *
     * Raw values with no inherent meaning — prefer the semantic aliases on
     * [SpaceScheme] when one fits. Reach for the scale when a one-off spacing
     * is genuinely context-specific and doesn't belong as a shared semantic token.
     *
     * @property xxs 2.dp — hairline gaps, dividers.
     * @property xs 4.dp — extremely tight spacing.
     * @property s 8.dp — tight inline spacing.
     * @property m 12.dp — list item gap.
     * @property l 16.dp — content-dense edge padding.
     * @property xl 20.dp — default screen/sheet edge padding.
     * @property xxl 24.dp — comfortable content padding.
     * @property huge 32.dp — inter-section gap.
     * @property section 40.dp — bottom-sheet top corner, large sections.
     * @property massive 56.dp — hero titles, splash gaps.
     * @since 0.0.13
     */
    data class Scale(
        val xxs: Dp,
        val xs: Dp,
        val s: Dp,
        val m: Dp,
        val l: Dp,
        val xl: Dp,
        val xxl: Dp,
        val huge: Dp,
        val section: Dp,
        val massive: Dp,
    )
}

/**
 * Standard Yalla [SpaceScheme] with semantic aliases mapped to conventional values.
 *
 * - `screenEdge`, `sheetEdge` = 20.dp (the de-facto default)
 * - `contentEdge` = 16.dp (dense lists, settings)
 * - `itemGap` = 12.dp, `sectionGap` = 32.dp, `heroGap` = 56.dp, `inlineGap` = 8.dp
 *
 * Override at the theme level when white-labeling.
 *
 * @since 0.0.13
 */
fun standardSpaceScheme(): SpaceScheme = SpaceScheme(
    screenEdge = 20.dp,
    sheetEdge = 20.dp,
    contentEdge = 16.dp,
    itemGap = 12.dp,
    sectionGap = 32.dp,
    heroGap = 56.dp,
    inlineGap = 8.dp,
    scale = SpaceScheme.Scale(
        xxs = 2.dp,
        xs = 4.dp,
        s = 8.dp,
        m = 12.dp,
        l = 16.dp,
        xl = 20.dp,
        xxl = 24.dp,
        huge = 32.dp,
        section = 40.dp,
        massive = 56.dp,
    ),
)

/**
 * [CompositionLocal][androidx.compose.runtime.CompositionLocal] for providing
 * a [SpaceScheme] down the composable tree.
 *
 * Defaults to [standardSpaceScheme] so tokens are accessible even outside a
 * [YallaTheme][uz.yalla.design.theme.YallaTheme] — previews and isolated
 * composables still resolve sensible values.
 *
 * @since 0.0.13
 */
val LocalSpaceScheme = staticCompositionLocalOf { standardSpaceScheme() }
