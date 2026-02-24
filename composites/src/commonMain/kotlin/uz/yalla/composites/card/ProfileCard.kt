package uz.yalla.composites.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource
import uz.yalla.design.theme.System
import uz.yalla.primitives.transformation.MaskFormatter
import uz.yalla.resources.Res
import uz.yalla.resources.img_no_pfp

/**
 * State for [ProfileCard].
 *
 * @property name User display name.
 * @property phone User phone number.
 * @property imageUrl Optional avatar URL.
 */
data class ProfileCardState(
    val name: String,
    val phone: String,
    val imageUrl: String? = null,
)

/**
 * User profile card with avatar, name, and phone.
 *
 * Displays user information with optional action buttons.
 *
 * ## Usage
 *
 * ```kotlin
 * ProfileCard(
 *     state = ProfileCardState(
 *         name = "${user.firstName} ${user.lastName}",
 *         phone = user.phone,
 *         imageUrl = user.avatar,
 *     ),
 *     leadingAction = { NavigationButton(onClick = onBack) },
 *     trailingAction = { IconButton(onClick = onEdit) { Icon(...) } },
 * )
 * ```
 *
 * @param state Profile data state.
 * @param modifier Applied to component.
 * @param leadingAction Optional left action button.
 * @param trailingAction Optional right action button.
 * @param colors Color configuration, defaults to [ProfileCardDefaults.colors].
 * @param dimens Dimension configuration, defaults to [ProfileCardDefaults.dimens].
 *
 * @see ProfileCardState for state configuration
 * @see ProfileCardDefaults for default values
 */
@Composable
fun ProfileCard(
    state: ProfileCardState,
    modifier: Modifier = Modifier,
    leadingAction: (@Composable () -> Unit)? = null,
    trailingAction: (@Composable () -> Unit)? = null,
    colors: ProfileCardDefaults.ProfileCardColors = ProfileCardDefaults.colors(),
    dimens: ProfileCardDefaults.ProfileCardDimens = ProfileCardDefaults.dimens(),
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth(),
    ) {
        if (leadingAction != null) {
            leadingAction()
        } else {
            Spacer(Modifier.size(dimens.actionButtonSize))
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = state.imageUrl,
                contentDescription = null,
                placeholder = painterResource(Res.drawable.img_no_pfp),
                error = painterResource(Res.drawable.img_no_pfp),
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .clip(CircleShape)
                        .size(dimens.avatarSize),
            )

            Spacer(Modifier.height(dimens.nameTopSpacing))

            Text(
                text = state.name,
                style = System.font.title.large,
                color = colors.name,
            )

            Spacer(Modifier.height(dimens.phoneTopSpacing))

            Text(
                text =
                    MaskFormatter.format(
                        text = state.phone,
                        mask = dimens.phoneMask,
                    ),
                style = System.font.body.small.medium,
                color = colors.phone,
            )

            Spacer(Modifier.height(dimens.bottomSpacing))
        }

        if (trailingAction != null) {
            trailingAction()
        } else {
            Spacer(Modifier.size(dimens.actionButtonSize))
        }
    }
}

/**
 * Default values for [ProfileCard].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 */
object ProfileCardDefaults {
    /**
     * Color configuration for [ProfileCard].
     *
     * @param name Name text color.
     * @param phone Phone text color.
     */
    data class ProfileCardColors(
        val name: Color,
        val phone: Color,
    )

    @Composable
    fun colors(
        name: Color = System.color.textBase,
        phone: Color = System.color.textBase,
    ): ProfileCardColors =
        ProfileCardColors(
            name = name,
            phone = phone,
        )

    /**
     * Dimension configuration for [ProfileCard].
     *
     * @param avatarSize Avatar image size.
     * @param actionButtonSize Action button size.
     * @param nameTopSpacing Spacing above name.
     * @param phoneTopSpacing Spacing above phone.
     * @param bottomSpacing Bottom spacing.
     * @param phoneMask Phone number display mask.
     */
    data class ProfileCardDimens(
        val avatarSize: Dp,
        val actionButtonSize: Dp,
        val nameTopSpacing: Dp,
        val phoneTopSpacing: Dp,
        val bottomSpacing: Dp,
        val phoneMask: String,
    )

    @Composable
    fun dimens(
        avatarSize: Dp = 80.dp,
        actionButtonSize: Dp = 40.dp,
        nameTopSpacing: Dp = 10.dp,
        phoneTopSpacing: Dp = 4.dp,
        bottomSpacing: Dp = 10.dp,
        phoneMask: String = "____ (__) ___-__-__",
    ): ProfileCardDimens =
        ProfileCardDimens(
            avatarSize = avatarSize,
            actionButtonSize = actionButtonSize,
            nameTopSpacing = nameTopSpacing,
            phoneTopSpacing = phoneTopSpacing,
            bottomSpacing = bottomSpacing,
            phoneMask = phoneMask,
        )
}

@Preview
@Composable
private fun ProfileCardPreview() {
    ProfileCard(
        state =
            ProfileCardState(
                name = "John Doe",
                phone = "998901234567",
            ),
    )
}
