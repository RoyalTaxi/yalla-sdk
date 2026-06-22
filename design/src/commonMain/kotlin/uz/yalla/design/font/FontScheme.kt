package uz.yalla.design.font

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import uz.yalla.resources.Res
import uz.yalla.resources.nummernschild

@Immutable
public data class FontScheme(
    val title: Title,
    val body: Body,
    val custom: Custom
) {
    @Immutable
    public data class Title(
        val xLarge: TextStyle,
        val large: TextStyle,
        val base: TextStyle
    )

    @Immutable
    public data class Body(
        val caption: TextStyle,
        val large: Weights,
        val base: Weights,
        val small: Weights
    ) {
        @Immutable
        public data class Weights(
            val regular: TextStyle,
            val medium: TextStyle,
            val bold: TextStyle
        )
    }

    @Immutable
    public data class Custom(
        val carNumber: TextStyle
    )
}

@Composable
internal fun rememberFontScheme(): FontScheme {
    val bold = FontFamily(Font(boldFont))
    val medium = FontFamily(Font(mediumFont))
    val normal = FontFamily(Font(normalFont))
    val carNumber = FontFamily(Font(Res.font.nummernschild))
    return remember(bold, medium, normal, carNumber) {
        FontScheme(
            title =
                FontScheme.Title(
                    xLarge = TextStyle(fontSize = 30.sp, lineHeight = 30.sp, fontFamily = bold),
                    large = TextStyle(fontSize = 22.sp, lineHeight = 22.sp, fontFamily = bold),
                    base = TextStyle(fontSize = 20.sp, lineHeight = 20.sp, fontFamily = bold)
                ),
            body =
                FontScheme.Body(
                    caption = TextStyle(fontSize = 13.sp, lineHeight = 15.6.sp, fontFamily = medium),
                    large =
                        FontScheme.Body.Weights(
                            regular = TextStyle(fontSize = 18.sp, lineHeight = 21.6.sp, fontFamily = normal),
                            medium = TextStyle(fontSize = 18.sp, lineHeight = 21.6.sp, fontFamily = medium),
                            bold = TextStyle(fontSize = 18.sp, lineHeight = 21.6.sp, fontFamily = bold)
                        ),
                    base =
                        FontScheme.Body.Weights(
                            regular = TextStyle(fontSize = 16.sp, lineHeight = 20.8.sp, fontFamily = normal),
                            medium = TextStyle(fontSize = 16.sp, lineHeight = 20.8.sp, fontFamily = medium),
                            bold = TextStyle(fontSize = 16.sp, lineHeight = 20.8.sp, fontFamily = bold)
                        ),
                    small =
                        FontScheme.Body.Weights(
                            regular = TextStyle(fontSize = 14.sp, lineHeight = 15.4.sp, fontFamily = normal),
                            medium = TextStyle(fontSize = 14.sp, lineHeight = 15.4.sp, fontFamily = medium),
                            bold = TextStyle(fontSize = 14.sp, lineHeight = 15.4.sp, fontFamily = bold)
                        )
                ),
            custom =
                FontScheme.Custom(
                    carNumber = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, fontFamily = carNumber)
                )
        )
    }
}

internal val LocalFontScheme: ProvidableCompositionLocal<FontScheme> =
    staticCompositionLocalOf<FontScheme> {
        error("No FontScheme provided. Wrap your content with YallaTheme or provide a FontScheme via LocalFontScheme.")
    }
