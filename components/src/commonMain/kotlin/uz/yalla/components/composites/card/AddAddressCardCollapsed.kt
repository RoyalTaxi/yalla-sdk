package uz.yalla.components.composites.card

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.yalla.components.primitives.button.AddAddressButton
import uz.yalla.design.theme.System

@Composable
public fun AddAddressCardCollapsed(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(System.color.background.secondary),
        modifier = modifier,
        onClick = onClick
    ) {
        AddAddressButton(
            onClick = onClick,
            layersCount = 4,
            paddingBetween = 16.dp,
            modifier = Modifier.padding(40.dp)
        )
    }
}
