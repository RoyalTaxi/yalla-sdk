package uz.yalla.components.composites.footer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import uz.yalla.core.util.formatArgs
import uz.yalla.design.theme.System
import uz.yalla.resources.Res
import uz.yalla.resources.icons.CardBonus
import uz.yalla.resources.icons.CashBonus
import uz.yalla.resources.icons.YallaIcons
import uz.yalla.resources.img_card
import uz.yalla.resources.img_cash
import uz.yalla.resources.payment_title
import uz.yalla.resources.payment_type_card_dot

@Composable
public fun PaymentButton(
    isCard: Boolean,
    cardLast4: String?,
    bonusActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val paymentLabel = stringResource(Res.string.payment_title)
    Button(
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(System.color.background.secondary),
        contentPadding = PaddingValues(0.dp),
        modifier =
            modifier
                .size(60.dp)
                .semantics { contentDescription = paymentLabel },
        onClick = onClick
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter =
                    when {
                        isCard && bonusActive -> rememberVectorPainter(YallaIcons.CardBonus)
                        isCard -> painterResource(Res.drawable.img_card)
                        bonusActive -> rememberVectorPainter(YallaIcons.CashBonus)
                        else -> painterResource(Res.drawable.img_cash)
                    },
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )

            if (isCard && cardLast4 != null) {
                Text(
                    text =
                        stringResource(Res.string.payment_type_card_dot)
                            .formatArgs(cardLast4.takeLast(4)),
                    color = System.color.text.base,
                    style = System.font.body.caption
                )
            }
        }
    }
}
