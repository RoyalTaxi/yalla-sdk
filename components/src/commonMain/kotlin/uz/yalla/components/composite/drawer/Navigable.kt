package uz.yalla.components.composite.drawer

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
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Default configuration values for [Navigable].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object NavigableDefaults {
    /**
     * Color configuration for [Navigable].
     *
     * @param container Surface background color.
     * @param title Title text color.
     * @param description Description text color.
     * @param chevron Chevron icon color.
     */
    data class NavigableColors(
        val container: Color,
        val title: Color,
        val description: Color,
        val chevron: Color
    )

    @Composable
    fun colors(
        container: Color = System.color.backgroundSecondary,
        title: Color = System.color.textBase,
        description: Color = System.color.textBase,
        chevron: Color = System.color.iconBase
    ) = NavigableColors(
        container = container,
        title = title,
        description = description,
        chevron = chevron
    )

    /**
     * Text style configuration for [Navigable].
     *
     * @param title Style applied to the title text.
     * @param description Style applied to the description text.
     */
    data class NavigableStyle(
        val title: TextStyle,
        val description: TextStyle
    )

    @Composable
    fun style(
        title: TextStyle = System.font.body.large.medium,
        description: TextStyle = System.font.body.caption
    ) = NavigableStyle(
        title = title,
        description = description
    )

    /**
     * Dimension configuration for [Navigable].
     *
     * @param contentPadding Padding inside the surface.
     * @param iconSpacing Spacing between icon and text.
     * @param descriptionSpacing Spacing between title and description.
     * @param trailingSpacing Spacing before chevron.
     * @param chevronSize Size of the chevron icon.
     */
    data class NavigableDimens(
        val contentPadding: PaddingValues,
        val iconSpacing: Dp,
        val descriptionSpacing: Dp,
        val trailingSpacing: Dp,
        val chevronSize: Dp
    )

    @Composable
    fun dimens(
        contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 9.dp),
        iconSpacing: Dp = 6.dp,
        descriptionSpacing: Dp = 4.dp,
        trailingSpacing: Dp = 8.dp,
        chevronSize: Dp = 24.dp
    ) = NavigableDimens(
        contentPadding = contentPadding,
        iconSpacing = iconSpacing,
        descriptionSpacing = descriptionSpacing,
        trailingSpacing = trailingSpacing,
        chevronSize = chevronSize
    )
}

/**
 * Navigable list item with title, optional description, and chevron.
 *
 * ## Usage
 *
 * ```kotlin
 * Navigable(
 *     title = "Settings",
 *     description = "Configure app preferences",
 *     leadingIcon = { DrawerItemIcon(painterResource(Res.drawable.ic_settings)) },
 *     onClick = { navigateToSettings() }
 * )
 * ```
 *
 * @param title Primary text
 * @param onClick Invoked when item is clicked
 * @param modifier Applied to surface
 * @param description Optional secondary text
 * @param leadingIcon Optional leading icon slot
 * @param trailingView Optional trailing content before chevron
 * @param colors Color configuration, defaults to [NavigableDefaults.colors]
 * @param style Text style configuration, defaults to [NavigableDefaults.style]
 * @param dimens Dimension configuration, defaults to [NavigableDefaults.dimens]
 */
@Composable
fun Navigable(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingView: (@Composable () -> Unit)? = null,
    colors: NavigableDefaults.NavigableColors = NavigableDefaults.colors(),
    style: NavigableDefaults.NavigableStyle = NavigableDefaults.style(),
    dimens: NavigableDefaults.NavigableDimens = NavigableDefaults.dimens()
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        color = colors.container
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(dimens.contentPadding)
        ) {
            leadingIcon?.let { it() }

            Spacer(modifier = Modifier.width(dimens.iconSpacing))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(dimens.descriptionSpacing)
            ) {
                Text(
                    text = title,
                    style = style.title,
                    color = colors.title
                )

                description?.let {
                    Text(
                        text = it,
                        style = style.description,
                        color = colors.description
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
                modifier = Modifier.size(dimens.chevronSize)
            )
        }
    }
}
