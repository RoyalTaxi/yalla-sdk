package uz.yalla.components.composites.item

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.resources.icons.ArrowRight
import uz.yalla.resources.icons.YallaIcons

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddressItem(
    locations: List<String>,
    placeholder: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = System.color.background.secondary)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .heightIn(min = 60.dp)
                .padding(start = 16.dp, end = 8.dp)
        ) {
            leadingContent?.invoke()

            if (locations.isEmpty()) {
                ProvideTextStyle(System.font.body.base.bold.copy(color = System.color.text.subtle)) {
                    Box(modifier = Modifier.weight(1f)) {
                        placeholder()
                    }
                }
            } else {
                FlowRow(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    itemVerticalAlignment = Alignment.CenterVertically
                ) {
                    locations.forEachIndexed { index, location ->
                        Text(
                            text = location,
                            color = System.color.text.base,
                            style = System.font.body.base.bold,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )

                        if (index != locations.lastIndex) {
                            Icon(
                                imageVector = YallaIcons.ArrowRight,
                                contentDescription = null,
                                tint = System.color.icon.subtle
                            )
                        }
                    }
                }
            }

            trailingContent?.invoke()
        }
    }
}

@Composable
fun AddressDot(
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(14.dp)
            .background(color = System.color.icon.white, shape = CircleShape)
            .border(width = 4.dp, color = color, shape = CircleShape)
    )
}
