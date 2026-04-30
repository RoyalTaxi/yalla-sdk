package uz.yalla.composites.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.resources.icons.Checked
import uz.yalla.resources.icons.Unchecked
import uz.yalla.resources.icons.YallaIcons

/**
 * Color configuration for [SelectionCard].
 */
@Immutable
data class SelectionCardColors(
    val container: Color,
    val iconBackground: Color,
)

/**
 * Dimension configuration for [SelectionCard].
 */
@Immutable
data class SelectionCardDimens(
    val contentPadding: PaddingValues,
    val iconSize: Dp,
    val iconShape: Shape,
    val iconPadding: Dp,
    val iconSpacing: Dp,
    val trailingSpacing: Dp,
)

/**
 * Default configuration values for [SelectionCard].
 */
object SelectionCardDefaults {

    /**
     * Creates theme-aware default colors.
     */
    @Composable
    fun colors(
        container: Color = Color.Transparent,
        iconBackground: Color = System.color.background.secondary,
    ): SelectionCardColors = SelectionCardColors(
        container = container,
        iconBackground = iconBackground,
    )

    /**
     * Creates default dimensions.
     */
    fun dimens(
        contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        iconSize: Dp = 44.dp,
        iconShape: Shape = RoundedCornerShape(10.dp),
        iconPadding: Dp = 10.dp,
        iconSpacing: Dp = 16.dp,
        trailingSpacing: Dp = 28.dp,
    ): SelectionCardDimens = SelectionCardDimens(
        contentPadding = contentPadding,
        iconSize = iconSize,
        iconShape = iconShape,
        iconPadding = iconPadding,
        iconSpacing = iconSpacing,
        trailingSpacing = trailingSpacing,
    )
}

/**
 * Card with selection indicator (checked/unchecked) for single-choice lists.
 *
 * Built on [ContentCard] with a leading icon container and trailing check indicator.
 *
 * ## Usage
 *
 * ```kotlin
 * SelectionCard(
 *     selected = isSelected,
 *     onClick = { select() },
 *     leadingIcon = { Icon(YallaIcons.Cash, null) },
 * ) {
 *     Text("Cash Payment")
 * }
 * ```
 *
 * @param colors Color configuration, defaults to [SelectionCardDefaults.colors].
 * @param dimens Dimension configuration, defaults to [SelectionCardDefaults.dimens].
 * @param content Card content, typically a [Text][androidx.compose.material3.Text].
 *
 * @see ContentCard
 * @see SelectionCardDefaults
 */
@Composable
fun SelectionCard(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: SelectionCardColors = SelectionCardDefaults.colors(),
    dimens: SelectionCardDimens = SelectionCardDefaults.dimens(),
    leadingIcon: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    ContentCard(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        colors = ContentCardDefaults.colors(
            container = colors.container,
            disabledContainer = colors.container,
        ),
        dimens = ContentCardDefaults.dimens(
            contentPadding = dimens.contentPadding,
        ),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(dimens.iconSize)
                        .clip(dimens.iconShape)
                        .background(colors.iconBackground)
                        .padding(dimens.iconPadding),
                ) {
                    leadingIcon()
                }

                Spacer(Modifier.width(dimens.iconSpacing))
            }

            Box(Modifier.weight(1f)) {
                content()
            }

            Spacer(Modifier.width(dimens.trailingSpacing))

            Icon(
                painter = rememberVectorPainter(
                    if (selected) YallaIcons.Checked else YallaIcons.Unchecked,
                ),
                contentDescription = null,
                tint = Color.Unspecified,
            )
        }
    }
}
