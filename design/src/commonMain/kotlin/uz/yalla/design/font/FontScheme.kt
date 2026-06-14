package uz.yalla.design.font

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle

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
        val large: Weighty,
        val base: Weighty,
        val small: Weighty
    ) {
        @Immutable
        public data class Weighty(
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

public val LocalFontScheme: ProvidableCompositionLocal<FontScheme> =
    staticCompositionLocalOf<FontScheme> {
        error("No FontScheme provided. Wrap your content with YallaTheme or provide a FontScheme via LocalFontScheme.")
    }
