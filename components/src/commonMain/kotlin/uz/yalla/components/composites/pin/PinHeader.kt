package uz.yalla.components.composites.pin

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import uz.yalla.resources.Res
import uz.yalla.resources.common_status_loading

private val HeaderMaxWidth = 280.dp

@Composable
internal fun PinHeader(
    address: String,
    headerStyle: TextStyle,
    colors: LocationPinColors,
    dimens: LocationPinDimens,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = dimens.headerShape,
        color = colors.header,
        modifier = modifier.widthIn(max = HeaderMaxWidth)
    ) {
        Text(
            text = address.takeIf { it.isNotBlank() } ?: stringResource(Res.string.common_status_loading),
            color = colors.headerText,
            style = headerStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .animateContentSize(
                    alignment = Alignment.Center,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
                .padding(
                    vertical = dimens.headerVerticalPadding,
                    horizontal = dimens.headerHorizontalPadding
                )
        )
    }
}
