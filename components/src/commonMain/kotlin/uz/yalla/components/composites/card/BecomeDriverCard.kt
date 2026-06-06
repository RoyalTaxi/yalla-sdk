package uz.yalla.components.composites.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.Res
import uz.yalla.resources.driver_become
import uz.yalla.resources.icons.ArrowRight
import uz.yalla.resources.icons.YallaIcons
import uz.yalla.resources.img_banner_gradient
import uz.yalla.resources.img_car_cropped

@Composable
fun BecomeDriverCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Box {
            Image(
                painter = painterResource(Res.drawable.img_banner_gradient),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Image(
                    painter = painterResource(Res.drawable.img_car_cropped),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.height(64.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(Res.string.driver_become),
                    style = System.font.title.base,
                    color = System.color.text.white,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    painter = rememberVectorPainter(YallaIcons.ArrowRight),
                    contentDescription = null,
                    tint = System.color.text.white,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() = YallaTheme {
    BecomeDriverCard(
        onClick = {},
        modifier = Modifier.padding(16.dp)
    )
}
