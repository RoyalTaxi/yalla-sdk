package uz.yalla.components.composites.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme

@Composable
fun NotificationCard(
    header: String,
    date: String,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(System.color.background.secondary),
        onClick = onClick,
        modifier = modifier,
        border = BorderStroke(
            width = 1.dp,
            color = System.color.border.disabled
        )
    ) {
        Box {
            Box(modifier = Modifier.matchParentSize()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(vertical = 24.dp)
                        .fillMaxHeight()
                        .width(4.dp)
                        .background(
                            color = System.color.background.brand,
                            shape = RoundedCornerShape(
                                topEnd = 100.dp,
                                bottomEnd = 100.dp
                            )
                        )
                )
            }

            Column(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .padding(start = 16.dp, end = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = header,
                        color = System.color.text.base,
                        style = System.font.body.base.bold
                    )

                    Text(
                        text = date,
                        color = System.color.text.base,
                        style = System.font.body.caption
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                HorizontalDivider(
                    thickness = 1.dp,
                    color = System.color.border.disabled
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = title,
                    color = System.color.text.base,
                    style = System.font.body.base.bold
                )

                Spacer(modifier = Modifier.padding(4.dp))

                Text(
                    text = description,
                    color = System.color.text.base,
                    style = System.font.body.caption
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() = YallaTheme {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        NotificationCard(
            header = "Ошибка",
            date = "30 Апреля, 2022",
            title = "Ваш заказ отменён водителем",
            description = "К сожалению, водитель отменил заказ. Попробуйте вызвать снова",
            onClick = {}
        )

        NotificationCard(
            header = "Информация",
            date = "12 Мая, 2026",
            title = "Скидка 20% на ваш следующий заказ",
            description = "Используйте промокод SPRING20 при оформлении поездки",
            onClick = {}
        )
    }
}
