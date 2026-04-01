package uz.yalla.composites.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Color configuration for [AvatarCard].
 *
 * @param name Name text color.
 * @param badgeBackground Gradient brush for the badge background.
 * @param badgeText Text color inside the badge.
 * @since 0.0.1
 */
@Immutable
data class AvatarCardColors(
    val name: Color,
    val badgeBackground: Brush,
    val badgeText: Color,
)

/**
 * Dimension configuration for [AvatarCard].
 *
 * @param avatarSize Size of the circular avatar.
 * @param badgeShape Shape of the badge overlay.
 * @param badgePadding Padding inside the badge.
 * @param nameTopSpacing Spacing between avatar and name.
 * @param contentSpacing Spacing between name and additional content.
 * @since 0.0.1
 */
@Immutable
data class AvatarCardDimens(
    val avatarSize: Dp,
    val badgeShape: Shape,
    val badgePadding: PaddingValues,
    val nameTopSpacing: Dp,
    val contentSpacing: Dp,
)

/**
 * Default configuration values for [AvatarCard].
 *
 * @since 0.0.1
 */
object AvatarCardDefaults {

    /**
     * Creates theme-aware default colors.
     */
    @Composable
    fun colors(
        name: Color = System.color.text.base,
        badgeBackground: Brush = System.color.gradient.sunsetNight,
        badgeText: Color = System.color.text.white,
    ): AvatarCardColors = AvatarCardColors(
        name = name,
        badgeBackground = badgeBackground,
        badgeText = badgeText,
    )

    /**
     * Creates default dimensions.
     */
    fun dimens(
        avatarSize: Dp = 80.dp,
        badgeShape: Shape = RoundedCornerShape(8.dp),
        badgePadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
        nameTopSpacing: Dp = 12.dp,
        contentSpacing: Dp = 8.dp,
    ): AvatarCardDimens = AvatarCardDimens(
        avatarSize = avatarSize,
        badgeShape = badgeShape,
        badgePadding = badgePadding,
        nameTopSpacing = nameTopSpacing,
        contentSpacing = contentSpacing,
    )
}

/**
 * Avatar card displaying a circular image with optional badge, name, and extra content.
 *
 * Lays out a centered column: avatar (clipped to [CircleShape]) with an optional bottom-end
 * badge overlay, followed by the name and optional content below.
 *
 * ## Usage
 *
 * ```kotlin
 * AvatarCard(
 *     avatar = {
 *         AsyncImage(model = user.photoUrl, contentDescription = null)
 *     },
 *     badge = {
 *         Text("VIP", style = System.font.body.caption, color = colors.badgeText)
 *     },
 *     name = {
 *         Text(user.name, style = System.font.title.base)
 *     },
 *     content = {
 *         Text(user.phone, style = System.font.body.small.medium)
 *     },
 * )
 * ```
 *
 * @param modifier Applied to the root column.
 * @param colors Color configuration, defaults to [AvatarCardDefaults.colors].
 * @param dimens Dimension configuration, defaults to [AvatarCardDefaults.dimens].
 * @param avatar Composable rendered inside a circular clip (e.g., [AsyncImage][coil3.compose.AsyncImage]).
 * @param badge Optional composable overlaid at the bottom-end of the avatar.
 * @param content Optional composable rendered below the name.
 * @param name Composable for the user's name, rendered below the avatar.
 *
 * @see AvatarCardDefaults
 * @since 0.0.1
 */
@Composable
fun AvatarCard(
    modifier: Modifier = Modifier,
    colors: AvatarCardColors = AvatarCardDefaults.colors(),
    dimens: AvatarCardDimens = AvatarCardDefaults.dimens(),
    avatar: @Composable () -> Unit,
    badge: @Composable (() -> Unit)? = null,
    content: @Composable (() -> Unit)? = null,
    name: @Composable () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            Box(
                modifier = Modifier
                    .size(dimens.avatarSize)
                    .clip(CircleShape),
            ) {
                avatar()
            }

            badge?.let {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .clip(dimens.badgeShape)
                        .background(colors.badgeBackground)
                        .padding(dimens.badgePadding),
                    contentAlignment = Alignment.Center,
                ) {
                    it()
                }
            }
        }

        Spacer(modifier = Modifier.height(dimens.nameTopSpacing))

        name()

        content?.let {
            Spacer(modifier = Modifier.height(dimens.contentSpacing))
            it()
        }
    }
}
