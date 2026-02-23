package uz.yalla.components.composite.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import org.jetbrains.compose.resources.painterResource
import uz.yalla.design.theme.System
import uz.yalla.resources.Res
import uz.yalla.resources.ic_car
import uz.yalla.resources.ic_warning

/**
 * UI state for [BrandServiceItem].
 *
 * @param title Service title.
 * @param selected Whether service is selected.
 */
data class BrandServiceItemState(
    val title: String,
    val selected: Boolean,
)

/**
 * Icon model for [BrandServiceItem].
 *
 * Supports local painter icons and async models (e.g., URL string).
 */
sealed interface BrandServiceItemIconModel {
    /**
     * Local static icon.
     *
     * @param painter Painter source for icon.
     */
    data class Local(
        val painter: Painter,
    ) : BrandServiceItemIconModel

    /**
     * Async icon source passed to Coil model.
     *
     * @param model Coil model (e.g., URL string, ImageRequest).
     */
    data class Async(
        val model: Any?,
    ) : BrandServiceItemIconModel
}

/**
 * Selectable pill item used for brand-service filtering.
 *
 * ## Usage
 *
 * ```kotlin
 * BrandServiceItem(
 *     state = BrandServiceItemState(
 *         title = "Такси",
 *         selected = selectedServiceId == service.id,
 *     ),
 *     iconModel = BrandServiceItemIconModel.Async(service.iconUrl),
 *     asyncImage = BrandServiceItemDefaults.asyncImage(
 *         placeholder = painterResource(Res.drawable.ic_car),
 *         error = painterResource(Res.drawable.ic_warning),
 *     ),
 *     onClick = { onServiceClick(service.id) },
 * )
 * ```
 *
 * @param state Current item state.
 * @param onClick Invoked when item is clicked.
 * @param modifier Applied to item.
 * @param enabled Whether item is clickable.
 * @param iconModel Optional icon source, local painter or async model.
 * @param iconLoading Optional loading content for async icon.
 * @param iconError Optional error content for async icon.
 * @param iconPlaceholder Optional placeholder content for async icon.
 * @param asyncImage Async image painter configuration.
 * @param iconTint Icon tint. Use [Color.Unspecified] to keep original icon colors.
 * @param colors Color configuration, defaults to [BrandServiceItemDefaults.colors].
 * @param style Text style configuration, defaults to [BrandServiceItemDefaults.style].
 * @param dimens Dimension configuration, defaults to [BrandServiceItemDefaults.dimens].
 *
 * @see BrandServiceItemDefaults for default values
 */
@Composable
fun BrandServiceItem(
    state: BrandServiceItemState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    iconModel: BrandServiceItemIconModel? = null,
    iconLoading: (@Composable () -> Unit)? = null,
    iconError: (@Composable () -> Unit)? = null,
    iconPlaceholder: (@Composable () -> Unit)? = null,
    asyncImage: BrandServiceItemDefaults.BrandServiceItemAsyncImage = BrandServiceItemDefaults.asyncImage(),
    iconTint: Color = Color.Unspecified,
    colors: BrandServiceItemDefaults.BrandServiceItemColors = BrandServiceItemDefaults.colors(),
    style: BrandServiceItemDefaults.BrandServiceItemStyle = BrandServiceItemDefaults.style(),
    dimens: BrandServiceItemDefaults.BrandServiceItemDimens = BrandServiceItemDefaults.dimens(),
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = dimens.shape,
        color = if (state.selected) colors.selectedContainer else colors.container,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.iconTextSpacing),
            modifier = Modifier.padding(dimens.contentPadding),
        ) {
            if (iconModel != null) {
                BrandServiceIcon(
                    iconModel = iconModel,
                    dimens = dimens,
                    asyncImage = asyncImage,
                    iconLoading = iconLoading,
                    iconError = iconError,
                    iconPlaceholder = iconPlaceholder,
                    iconTint = iconTint,
                )
            }

            Text(
                text = state.title,
                color = if (state.selected) colors.selectedTitle else colors.title,
                style = style.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun BrandServiceIcon(
    iconModel: BrandServiceItemIconModel,
    dimens: BrandServiceItemDefaults.BrandServiceItemDimens,
    asyncImage: BrandServiceItemDefaults.BrandServiceItemAsyncImage,
    iconLoading: (@Composable () -> Unit)?,
    iconError: (@Composable () -> Unit)?,
    iconPlaceholder: (@Composable () -> Unit)?,
    iconTint: Color,
) {
    when (iconModel) {
        is BrandServiceItemIconModel.Local -> {
            Icon(
                painter = iconModel.painter,
                contentDescription = null,
                modifier = Modifier.size(dimens.iconSize),
                tint = iconTint,
            )
        }

        is BrandServiceItemIconModel.Async -> {
            val painter = rememberAsyncImagePainter(model = iconModel.model)
            val state by painter.state.collectAsState()

            when (state) {
                is AsyncImagePainter.State.Success -> {
                    Icon(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier.size(dimens.iconSize),
                        tint = iconTint,
                    )
                }

                is AsyncImagePainter.State.Loading -> {
                    BrandServiceIconFallback(
                        size = dimens.iconSize,
                        content = iconLoading ?: iconPlaceholder,
                        painter = asyncImage.placeholder ?: asyncImage.fallback,
                        iconTint = iconTint,
                    )
                }

                is AsyncImagePainter.State.Error -> {
                    BrandServiceIconFallback(
                        size = dimens.iconSize,
                        content = iconError ?: iconPlaceholder,
                        painter = asyncImage.error ?: asyncImage.fallback ?: asyncImage.placeholder,
                        iconTint = iconTint,
                    )
                }

                is AsyncImagePainter.State.Empty -> {
                    BrandServiceIconFallback(
                        size = dimens.iconSize,
                        content = iconPlaceholder,
                        painter = asyncImage.fallback ?: asyncImage.placeholder,
                        iconTint = iconTint,
                    )
                }
            }
        }
    }
}

@Composable
private fun BrandServiceIconFallback(
    size: Dp,
    content: (@Composable () -> Unit)?,
    painter: Painter?,
    iconTint: Color,
) {
    when {
        content != null -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(size),
            ) {
                content()
            }
        }

        painter != null -> {
            Icon(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(size),
                tint = iconTint,
            )
        }
    }
}

/**
 * Default configuration values for [BrandServiceItem].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object BrandServiceItemDefaults {
    /**
     * Color configuration for [BrandServiceItem].
     *
     * @param container Background color when unselected.
     * @param selectedContainer Background color when selected.
     * @param title Title text color when unselected.
     * @param selectedTitle Title text color when selected.
     */
    data class BrandServiceItemColors(
        val container: Color,
        val selectedContainer: Color,
        val title: Color,
        val selectedTitle: Color,
    )

    @Composable
    fun colors(
        container: Color = System.color.backgroundTertiary,
        selectedContainer: Color = System.color.backgroundBrandBase,
        title: Color = System.color.textBase,
        selectedTitle: Color = System.color.textWhite,
    ) = BrandServiceItemColors(
        container = container,
        selectedContainer = selectedContainer,
        title = title,
        selectedTitle = selectedTitle,
    )

    /**
     * Text style configuration for [BrandServiceItem].
     *
     * @param title Title text style.
     */
    data class BrandServiceItemStyle(
        val title: TextStyle,
    )

    @Composable
    fun style(title: TextStyle = System.font.body.base.medium) =
        BrandServiceItemStyle(
            title = title,
        )

    /**
     * Async icon painter configuration for [BrandServiceItem].
     *
     * @param placeholder Painter shown while async icon is loading.
     * @param error Painter shown when async icon loading fails.
     * @param fallback Painter shown when async model is empty.
     */
    data class BrandServiceItemAsyncImage(
        val placeholder: Painter?,
        val error: Painter?,
        val fallback: Painter?,
    )

    fun asyncImage(
        placeholder: Painter? = null,
        error: Painter? = placeholder,
        fallback: Painter? = placeholder,
    ) = BrandServiceItemAsyncImage(
        placeholder = placeholder,
        error = error,
        fallback = fallback,
    )

    /**
     * Dimension configuration for [BrandServiceItem].
     *
     * @param shape Pill shape of the item.
     * @param contentPadding Padding around the title.
     * @param itemSpacing Recommended spacing between multiple chips.
     * @param iconSize Icon size.
     * @param iconTextSpacing Spacing between icon and title.
     */
    data class BrandServiceItemDimens(
        val shape: Shape,
        val contentPadding: PaddingValues,
        val itemSpacing: Dp,
        val iconSize: Dp,
        val iconTextSpacing: Dp,
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(50.dp),
        contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        itemSpacing: Dp = 8.dp,
        iconSize: Dp = 16.dp,
        iconTextSpacing: Dp = 4.dp,
    ) = BrandServiceItemDimens(
        shape = shape,
        contentPadding = contentPadding,
        itemSpacing = itemSpacing,
        iconSize = iconSize,
        iconTextSpacing = iconTextSpacing,
    )
}

@Preview
@Composable
private fun BrandServiceItemPreview() {
    val dimens = BrandServiceItemDefaults.dimens()

    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(dimens.itemSpacing),
        ) {
            BrandServiceItem(
                state =
                    BrandServiceItemState(
                        title = "Все",
                        selected = true,
                    ),
                iconModel =
                    BrandServiceItemIconModel.Local(
                        painter = painterResource(Res.drawable.ic_car),
                    ),
                onClick = {},
            )

            BrandServiceItem(
                state =
                    BrandServiceItemState(
                        title = "Такси",
                        selected = false,
                    ),
                iconModel =
                    BrandServiceItemIconModel.Async(
                        model = "https://example.com/taxi.png",
                    ),
                asyncImage =
                    BrandServiceItemDefaults.asyncImage(
                        placeholder = painterResource(Res.drawable.ic_car),
                        error = painterResource(Res.drawable.ic_warning),
                    ),
                onClick = {},
            )

            BrandServiceItem(
                state =
                    BrandServiceItemState(
                        title = "Грузовой",
                        selected = false,
                    ),
                onClick = {},
                style =
                    BrandServiceItemDefaults.style(
                        title = System.font.body.small.medium,
                    ),
            )

            BrandServiceItem(
                state =
                    BrandServiceItemState(
                        title = "📍 Межгород",
                        selected = false,
                    ),
                onClick = {},
            )

            BrandServiceItem(
                state =
                    BrandServiceItemState(
                        title = "Межгород",
                        selected = false,
                    ),
                onClick = {},
            )
        }
    }
}
