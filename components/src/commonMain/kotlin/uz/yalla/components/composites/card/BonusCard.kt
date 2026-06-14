package uz.yalla.components.composites.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.Res
import uz.yalla.resources.img_coin

@Immutable
public data class BonusCardDimens(
    val shape: Shape,
    val contentPadding: PaddingValues
)

public object BonusCardDefaults {
    @Composable
    public fun dimens(
        shape: Shape = CircleShape,
        contentPadding: PaddingValues =
            PaddingValues(
                start = 4.dp,
                top = 4.dp,
                end = 8.dp,
                bottom = 4.dp
            )
    ): BonusCardDimens =
        BonusCardDimens(
            shape = shape,
            contentPadding = contentPadding
        )
}

@Composable
public fun BonusCard(
    bonus: String,
    enabled: Boolean = true,
    leadingPainter: Painter? = painterResource(Res.drawable.img_coin),
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    dimens: BonusCardDimens = BonusCardDefaults.dimens()
) {
    Surface(
        enabled = enabled && onClick != null,
        shape = dimens.shape,
        modifier = modifier,
        onClick = onClick ?: {}
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier =
                Modifier
                    .background(System.color.gradient.sunsetNight)
                    .padding(dimens.contentPadding)
        ) {
            leadingPainter?.let { painter ->
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = bonus,
                color = System.color.text.white,
                style = System.font.body.base.bold
            )
        }
    }
}

@Preview
@Composable
private fun Preview() =
    YallaTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BonusCard(
                bonus = "0",
                enabled = false,
                leadingPainter = null,
                onClick = { },
                dimens =
                    BonusCardDefaults.dimens(
                        contentPadding =
                            PaddingValues(
                                vertical = 6.dp,
                                horizontal = 8.dp
                            )
                    )
            )

            BonusCard(
                bonus = "30000",
                onClick = { }
            )
        }
    }
