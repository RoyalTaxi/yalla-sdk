package uz.yalla.components.composites.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import uz.yalla.components.composites.item.SelectableItem
import uz.yalla.components.composites.item.SelectableItemDefaults
import uz.yalla.core.profile.GenderKind
import uz.yalla.design.theme.System
import uz.yalla.resources.Res
import uz.yalla.resources.error_unknown
import uz.yalla.resources.icons.Female
import uz.yalla.resources.icons.Male
import uz.yalla.resources.icons.Unchecked
import uz.yalla.resources.icons.YallaIcons
import uz.yalla.resources.register_gender_female
import uz.yalla.resources.register_gender_male


@Composable
public fun SeatOverviewCard(
    title: String,
    body: String,
    price: String,
    gender: GenderKind?,
    onGender: (GenderKind) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(System.color.background.secondary),
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier =
                Modifier
                    .padding(
                        vertical = 16.dp,
                        horizontal = 12.dp
                    )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier =
                        Modifier
                            .weight(1f)
                ) {
                    Text(
                        text = title,
                        color = System.color.text.subtle,
                        style = System.font.body.small.medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        text = body,
                        color = System.color.background.brand,
                        style = System.font.body.base.bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Text(
                    text = price,
                    color = System.color.text.base,
                    style = System.font.body.base.bold
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                GenderKind.entries
                    .filter { it != GenderKind.NotSelected }
                    .forEach {
                        SelectableItem(
                            leadingPainter = it.painterResource(),
                            text = it.stringResource(),
                            selected = gender == it,
                            modifier = Modifier.weight(1f),
                            onClick = { onGender(it) },
                            dimens = SelectableItemDefaults.dimens(
                                shape = RoundedCornerShape(16.dp),
                                contentSpacing = 6.dp,
                                borderWidth = 0.dp,
                                iconSize = 20.dp,
                                contentPadding =
                                    PaddingValues(
                                        vertical = 8.dp,
                                        horizontal = 10.dp
                                    )
                            ),
                            colors = SelectableItemDefaults.colors(
                                containerColor = System.color.background.base,
                                selectedContainerColor = System.color.background.base,
                                iconColor = System.color.icon.subtle,
                                selectedIconColor = System.color.button.active,
                                textColor = System.color.text.subtle,
                                selectedTextColor = System.color.text.base
                            )
                        )
                    }
            }
        }
    }
}

@Composable
private fun GenderKind.stringResource() =
    when (this) {
        GenderKind.Male -> stringResource(Res.string.register_gender_male)
        GenderKind.Female -> stringResource(Res.string.register_gender_female)
        GenderKind.NotSelected -> stringResource(Res.string.error_unknown)
    }


@Composable
private fun GenderKind.painterResource() =
    when (this) {
        GenderKind.Male -> rememberVectorPainter(YallaIcons.Male)
        GenderKind.Female -> rememberVectorPainter(YallaIcons.Female)
        GenderKind.NotSelected -> rememberVectorPainter(YallaIcons.Unchecked)
    }
