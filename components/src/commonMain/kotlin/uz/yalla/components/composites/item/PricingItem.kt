package uz.yalla.components.composites.item

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

@Composable
fun PricingItem(
    name: @Composable () -> Unit,
    price: @Composable () -> Unit,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    image: (@Composable ColumnScope.() -> Unit)? = null
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .height(120.dp)
            .widthIn(min = 140.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) System.color.background.base else System.color.background.secondary
        ),
        border = if (selected) BorderStroke(width = 2.dp, brush = System.color.gradient.sunsetNight) else null
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            ProvideTextStyle(System.font.body.base.bold.copy(color = System.color.text.base)) {
                name()
            }

            Spacer(Modifier.height(6.dp))

            ProvideTextStyle(System.font.body.base.bold.copy(color = System.color.text.base)) {
                price()
            }

            if (image != null) {
                Spacer(Modifier.height(10.dp))
                image(this)
            }
        }
    }
}
