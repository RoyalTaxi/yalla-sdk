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

@Immutable
data class AvatarCardColors(
    val name: Color,
    val badgeBackground: Brush,
    val badgeText: Color,
)

@Immutable
data class AvatarCardDimens(
    val avatarSize: Dp,
    val badgeShape: Shape,
    val badgePadding: PaddingValues,
    val nameTopSpacing: Dp,
    val contentSpacing: Dp,
)

object AvatarCardDefaults {

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
