package uz.yalla.design.space

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
public data class SpaceScheme(
    val screenEdge: Dp,
    val sheetEdge: Dp,
    val contentEdge: Dp,
    val itemGap: Dp,
    val sectionGap: Dp,
    val heroGap: Dp,
    val inlineGap: Dp,
    val scale: Scale
) {
    @Immutable
    public data class Scale(
        val xxs: Dp,
        val xs: Dp,
        val s: Dp,
        val m: Dp,
        val l: Dp,
        val xl: Dp,
        val xxl: Dp,
        val huge: Dp,
        val section: Dp,
        val massive: Dp
    )
}

public fun standardSpaceScheme(): SpaceScheme = SpaceScheme(
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
        massive = 56.dp
    )
)

public val LocalSpaceScheme: ProvidableCompositionLocal<SpaceScheme> = staticCompositionLocalOf { standardSpaceScheme() }
