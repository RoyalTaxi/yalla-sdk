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
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Color configuration for [Navigable].
 *
 * @param container Surface background color.
 * @param title Title text color.
 * @param description Description text color.
 * @param chevron Trailing chevron icon tint.
 * @since 0.0.1
 */
@Immutable
data class NavigableColors(
    val container: Color,
    val title: Color,
    val description: Color,
    val chevron: Color,
)

/**
 * Dimension configuration for [Navigable].
 *
 * @param contentPadding Padding around the entire row.
 * @param iconSpacing Spacing between leading icon and text column.
 * @param descriptionSpacing Vertical spacing between title and description.
 * @param trailingSpacing Spacing between trailing view and chevron.
 * @param chevronSize Size of the trailing chevron icon.
 * @since 0.0.1
 */
@Immutable
data class NavigableDimens(
    val contentPadding: PaddingValues,
    val iconSpacing: Dp,
    val descriptionSpacing: Dp,
    val trailingSpacing: Dp,
    val chevronSize: Dp,
)

/**
 * Navigable drawer/menu item with title, optional description, leading icon, and trailing chevron.
 *
 * Used inside [SectionBackground] for building grouped navigation menus in drawer screens.
 *
 * The default text style and color for [title] and [description] are injected via
 * [ProvideTextStyle][androidx.compose.material3.ProvideTextStyle]; a plain
 * [Text][androidx.compose.material3.Text] in the slot inherits them automatically.
 * Callers that need a custom style should pass a styled [Text] inside the slot.
 *
 * ## Usage
 *
 * ```kotlin
 * SectionBackground {
 *     Navigable(
 *         title = { Text("Settings") },
 *         description = { Text("Language, theme, notifications") },
 *         onClick = { navigateToSettings() },
 *         leadingIcon = {
 *             DrawerItemIcon(painter = painterResource(Res.drawable.ic_settings))
 *         },
 *     )
 *     Navigable(
 *         title = { Text("About") },
 *         onClick = { navigateToAbout() },
 *         leadingIcon = {
 *             DrawerItemIcon(painter = painterResource(Res.drawable.ic_info))
 *         },
 *     )
 * }
 * ```
 *
 * @param title Primary content; receives [System.font.body.large.medium] style by default.
 * @param onClick Invoked when the item is clicked.
 * @param modifier Applied to the root surface.
 * @param description Optional secondary content below the title; receives [System.font.body.caption] style.
 * @param leadingIcon Optional leading icon slot (e.g., [DrawerItemIcon]).
 * @param trailingView Optional trailing content rendered before the chevron.
 * @param colors Color configuration, defaults to [NavigableDefaults.colors].
 * @param dimens Dimension configuration, defaults to [NavigableDefaults.dimens].
 *
 * @see SectionBackground
 * @see DrawerItemIcon
 * @see NavigableDefaults
 * @since 0.0.1
 */
@Composable
fun Navigable(
    title: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingView: (@Composable () -> Unit)? = null,
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
                ProvideTextStyle(
                    System.font.body.large.medium
                        .copy(color = colors.title)
                ) {
                    title()
                }

                if (description != null) {
                    ProvideTextStyle(
                        System.font.body.caption
                            .copy(color = colors.description)
                    ) {
                        description()
                    }
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

/**
 * Default configuration values for [Navigable].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 * @since 0.0.1
 */
object NavigableDefaults {
    /**
     * Creates theme-aware default colors.
     */
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

    /**
     * Creates default dimensions.
     */
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
