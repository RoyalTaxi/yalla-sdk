package uz.yalla.components.primitives.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ContextualFlowRow
import androidx.compose.foundation.layout.ContextualFlowRowOverflow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.icons.ArrowRight
import uz.yalla.resources.icons.ArrowRightInCircle
import uz.yalla.resources.icons.YallaIcons

@Immutable
public data class LocationButtonColors(
    val dotColor: Color,
    val titleColor: Color,
    val descriptionColor: Color,
    val iconColor: Color,
    val containerColor: Color
)

@Immutable
public data class LocationButtonDimens(
    val shape: Shape,
    val contentSpacing: Dp,
    val contentPadding: PaddingValues
)

@Immutable
public data class LocationButtonStyles(
    val titleStyle: TextStyle,
    val descriptionStyle: TextStyle
)

public object LocationButtonDefaults {
    @Composable
    public fun colors(
        dotColor: Color = System.color.background.brand,
        titleColor: Color = System.color.text.base,
        descriptionColor: Color = System.color.text.base,
        iconColor: Color = System.color.icon.base,
        containerColor: Color = System.color.background.secondary
    ): LocationButtonColors =
        LocationButtonColors(
            dotColor = dotColor,
            titleColor = titleColor,
            descriptionColor = descriptionColor,
            iconColor = iconColor,
            containerColor = containerColor
        )

    @Composable
    public fun dimens(
        shape: Shape = RectangleShape,
        contentSpacing: Dp = 12.dp,
        contentPadding: PaddingValues =
            PaddingValues(
                vertical = 12.dp,
                horizontal = 16.dp
            )
    ): LocationButtonDimens =
        LocationButtonDimens(
            shape = shape,
            contentSpacing = contentSpacing,
            contentPadding = contentPadding
        )

    @Composable
    public fun styles(
        titleStyle: TextStyle = System.font.body.base.bold,
        descriptionStyle: TextStyle = System.font.body.small.medium
    ): LocationButtonStyles =
        LocationButtonStyles(
            titleStyle = titleStyle,
            descriptionStyle = descriptionStyle
        )

    @Composable
    public fun LeadingView(color: Color) {
        Box(
            modifier =
                Modifier
                    .size(14.dp)
                    .background(
                        color = color,
                        shape = CircleShape
                    ).padding(4.dp)
                    .background(
                        color = System.color.icon.white,
                        shape = CircleShape
                    )
        )
    }

    @Composable
    public fun Content(
        title: String,
        description: String?,
        modifier: Modifier = Modifier,
        colors: LocationButtonColors,
        styles: LocationButtonStyles
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = modifier
        ) {
            description?.let { desc ->
                Text(
                    text = desc,
                    color = colors.descriptionColor,
                    style = styles.descriptionStyle,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }

            Text(
                text = title,
                color = colors.titleColor,
                style = styles.titleStyle,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }

    @Suppress("DEPRECATION")
    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    public fun Content(
        vararg titles: String,
        modifier: Modifier = Modifier,
        colors: LocationButtonColors,
        styles: LocationButtonStyles
    ) {
        val itemCount = (titles.size * 2 - 1).coerceAtLeast(0)

        ContextualFlowRow(
            itemCount = itemCount,
            modifier = modifier,
            maxLines = 2,
            overflow =
                ContextualFlowRowOverflow.expandIndicator {
                    val visibleTitles = (shownItemCount + 1) / 2
                    val hiddenTitles = titles.size - visibleTitles

                    Text(
                        text = "+$hiddenTitles",
                        color = colors.titleColor,
                        style = styles.titleStyle
                    )
                },
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            itemVerticalAlignment = Alignment.CenterVertically
        ) { index ->
            if (index % 2 == 0) {
                val title = titles.getOrNull(index / 2) ?: return@ContextualFlowRow
                Text(
                    text = title,
                    color = colors.titleColor,
                    style = styles.titleStyle,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            } else {
                Icon(
                    imageVector = YallaIcons.ArrowRight,
                    contentDescription = null,
                    tint = System.color.icon.subtle,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }

    @Composable
    public fun TrailingView(
        color: Color,
        onClick: () -> Unit
    ) {
        Icon(
            painter = rememberVectorPainter(YallaIcons.ArrowRightInCircle),
            contentDescription = null,
            tint = color,
            modifier =
                Modifier
                    .size(24.dp)
                    .clickable(
                        onClick = onClick,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
        )
    }
}

@Composable
private fun LocationButtonContainer(
    onClick: () -> Unit,
    onTrailingViewClick: (() -> Unit)?,
    modifier: Modifier,
    colors: LocationButtonColors,
    dimens: LocationButtonDimens,
    leadingView: @Composable (() -> Unit)?,
    trailingView: @Composable (onClick: () -> Unit) -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        shape = dimens.shape,
        color = colors.containerColor,
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.contentSpacing),
            modifier = Modifier.padding(dimens.contentPadding)
        ) {
            leadingView?.invoke()
            content()
            onTrailingViewClick?.let { trailingView(it) }
        }
    }
}

@Composable
public fun LocationButton(
    title: String,
    description: String? = null,
    onClick: () -> Unit,
    onTrailingViewClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    colors: LocationButtonColors = LocationButtonDefaults.colors(),
    dimens: LocationButtonDimens = LocationButtonDefaults.dimens(),
    styles: LocationButtonStyles = LocationButtonDefaults.styles(),
    leadingView: @Composable (() -> Unit)? = {
        LocationButtonDefaults.LeadingView(
            color = colors.dotColor
        )
    },
    trailingView: @Composable (onClick: () -> Unit) -> Unit = {
        LocationButtonDefaults.TrailingView(
            color = colors.iconColor,
            onClick = it
        )
    }
): Unit =
    LocationButtonContainer(
        onClick = onClick,
        onTrailingViewClick = onTrailingViewClick,
        modifier = modifier,
        colors = colors,
        dimens = dimens,
        leadingView = leadingView,
        trailingView = trailingView
    ) {
        LocationButtonDefaults.Content(
            title = title,
            description = description,
            modifier = Modifier.weight(1f),
            colors = colors,
            styles = styles
        )
    }

@Composable
public fun LocationButton(
    vararg titles: String,
    onClick: () -> Unit,
    onTrailingViewClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    colors: LocationButtonColors = LocationButtonDefaults.colors(),
    dimens: LocationButtonDimens = LocationButtonDefaults.dimens(),
    styles: LocationButtonStyles = LocationButtonDefaults.styles(),
    leadingView: @Composable (() -> Unit)? = {
        LocationButtonDefaults.LeadingView(
            color = colors.dotColor
        )
    },
    trailingView: @Composable (onClick: () -> Unit) -> Unit = {
        LocationButtonDefaults.TrailingView(
            color = colors.iconColor,
            onClick = it
        )
    }
): Unit =
    LocationButtonContainer(
        onClick = onClick,
        onTrailingViewClick = onTrailingViewClick,
        modifier = modifier,
        colors = colors,
        dimens = dimens,
        leadingView = leadingView,
        trailingView = trailingView
    ) {
        LocationButtonDefaults.Content(
            titles = titles,
            modifier = Modifier.weight(1f),
            colors = colors,
            styles = styles
        )
    }

@Preview
@Composable
private fun Preview() =
    YallaTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LocationButton(
                title = "Amir Temur 1",
                onClick = {},
                dimens = LocationButtonDefaults.dimens(RoundedCornerShape(16.dp)),
                modifier = Modifier.fillMaxWidth(),
                onTrailingViewClick = {}
            )

            LocationButton(
                title = "Amir Temur 1",
                description = "Tashkent",
                onClick = {},
                colors = LocationButtonDefaults.colors(descriptionColor = System.color.text.subtle),
                dimens = LocationButtonDefaults.dimens(RoundedCornerShape(16.dp)),
                modifier = Modifier.fillMaxWidth(),
                leadingView = { LocationButtonDefaults.LeadingView(System.color.icon.red) }
            )

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = System.color.background.secondary
            ) {
                LocationButton(
                    title = "Amir Temur 1",
                    onClick = {}
                )
            }

            LocationButton(
                "Amir Temur 1",
                "Tashkent",
                "Samarkand",
                onClick = {},
                dimens = LocationButtonDefaults.dimens(RoundedCornerShape(16.dp)),
                modifier = Modifier.fillMaxWidth(),
                leadingView = { LocationButtonDefaults.LeadingView(System.color.icon.red) }
            )

            LocationButton(
                "Amir Temur 1",
                "Tashkent",
                "Samarkand Region",
                "Bukhara",
                "Khiva",
                "Nukus",
                onClick = {},
                dimens = LocationButtonDefaults.dimens(RoundedCornerShape(16.dp)),
                modifier = Modifier.fillMaxWidth(),
                leadingView = { LocationButtonDefaults.LeadingView(System.color.icon.red) }
            )
        }
    }
