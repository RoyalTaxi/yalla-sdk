package uz.yalla.composites.drawer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

@Immutable
data class NavigableColors(
    val container: Color,
    val title: Color,
    val description: Color,
    val chevron: Color,
)

@Immutable
data class NavigableDimens(
    val contentPadding: PaddingValues,
    val iconSpacing: Dp,
    val descriptionSpacing: Dp,
    val trailingSpacing: Dp,
    val chevronSize: Dp,
)

/**
 * Navigable list item with title, optional description, and chevron.
 *
 * @param title Primary text
 * @param onClick Invoked when item is clicked
 * @param modifier Applied to surface
 * @param description Optional secondary text
 * @param leadingIcon Optional leading icon slot
 * @param trailingView Optional trailing content before chevron
 * @param titleStyle Text style for the title.
 * @param descriptionStyle Text style for the description.
 * @param colors Color configuration, defaults to [NavigableDefaults.colors]
 * @param dimens Dimension configuration, defaults to [NavigableDefaults.dimens]
 * @since 0.0.1
 */
@Composable
fun Navigable(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingView: (@Composable () -> Unit)? = null,
    titleStyle: TextStyle = System.font.body.large.medium,
    descriptionStyle: TextStyle = System.font.body.caption,
    colors: NavigableColors = NavigableDefaults.colors(),
    dimens: NavigableDimens = NavigableDefaults.dimens(),
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        color = colors.container,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(dimens.contentPadding),
        ) {
            leadingIcon?.let { it() }

            Spacer(modifier = Modifier.width(dimens.iconSpacing))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(dimens.descriptionSpacing),
            ) {
                Text(
                    text = title,
                    style = titleStyle,
                    color = colors.title,
                )

                description?.let {
                    Text(
                        text = it,
                        style = descriptionStyle,
                        color = colors.description,
                    )
                }
            }

            trailingView?.let { view ->
                view()
                Spacer(modifier = Modifier.width(dimens.trailingSpacing))
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = colors.chevron,
                modifier = Modifier.size(dimens.chevronSize),
            )
        }
    }
}

object NavigableDefaults {
    @Composable
    fun colors(
        container: Color = System.color.background.secondary,
        title: Color = System.color.text.base,
        description: Color = System.color.text.base,
        chevron: Color = System.color.icon.base,
    ): NavigableColors = NavigableColors(
        container = container,
        title = title,
        description = description,
        chevron = chevron,
    )

    fun dimens(
        contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 9.dp),
        iconSpacing: Dp = 6.dp,
        descriptionSpacing: Dp = 4.dp,
        trailingSpacing: Dp = 8.dp,
        chevronSize: Dp = 24.dp,
    ): NavigableDimens = NavigableDimens(
        contentPadding = contentPadding,
        iconSpacing = iconSpacing,
        descriptionSpacing = descriptionSpacing,
        trailingSpacing = trailingSpacing,
        chevronSize = chevronSize,
    )
}
