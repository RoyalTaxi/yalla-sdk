package uz.yalla.design.font

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.FontResource
import uz.yalla.resources.Res
import uz.yalla.resources.nummernschild

/**
 * Platform-specific bold (700) font resource.
 *
 * Each platform (Android / iOS) provides the actual font file.
 * Used by [FontScheme.Title] styles and [FontScheme.Body.Weighty.bold].
 *
 * @since 0.0.1
 */
expect val boldFont: FontResource

/**
 * Platform-specific medium (500) font resource.
 *
 * Each platform provides the actual font file.
 * Used by [FontScheme.Body.caption] and [FontScheme.Body.Weighty.medium].
 *
 * @since 0.0.1
 */
expect val mediumFont: FontResource

/**
 * Platform-specific normal/regular (400) font resource.
 *
 * Each platform provides the actual font file.
 * Used by [FontScheme.Body.Weighty.regular].
 *
 * @since 0.0.1
 */
expect val normalFont: FontResource

/**
 * Creates and remembers the complete [FontScheme] for the Yalla design system.
 *
 * Builds all title, body, and custom text styles using the platform-specific font resources
 * ([boldFont], [mediumFont], [normalFont]) and the bundled Nummernschild font for license plates.
 *
 * This composable is called by [YallaTheme][uz.yalla.design.theme.YallaTheme] and its result
 * is provided via [LocalFontScheme]. Callers generally do not need to invoke this directly.
 *
 * @return Fully configured [FontScheme] instance.
 * @since 0.0.1
 */
@Composable
fun rememberFontScheme(): FontScheme {
    return FontScheme(
        title =
            FontScheme.Title(
                xLarge =
                    TextStyle(
                        fontSize = 30.sp,
                        lineHeight = 30.sp,
                        fontFamily = FontFamily(Font(boldFont))
                    ),
                large =
                    TextStyle(
                        fontSize = 22.sp,
                        lineHeight = 22.sp,
                        fontFamily = FontFamily(Font(boldFont))
                    ),
                base =
                    TextStyle(
                        fontSize = 20.sp,
                        lineHeight = 20.sp,
                        fontFamily = FontFamily(Font(boldFont))
                    )
            ),
        body =
            FontScheme.Body(
                caption =
                    TextStyle(
                        fontSize = 13.sp,
                        lineHeight = 15.6.sp,
                        fontFamily = FontFamily(Font(mediumFont))
                    ),
                large =
                    FontScheme.Body.Weighty(
                        regular =
                            TextStyle(
                                fontSize = 18.sp,
                                lineHeight = 21.6.sp,
                                fontFamily = FontFamily(Font(normalFont))
                            ),
                        medium =
                            TextStyle(
                                fontSize = 18.sp,
                                lineHeight = 21.6.sp,
                                fontFamily = FontFamily(Font(mediumFont))
                            ),
                        bold =
                            TextStyle(
                                fontSize = 18.sp,
                                lineHeight = 21.6.sp,
                                fontFamily = FontFamily(Font(boldFont))
                            )
                    ),
                base =
                    FontScheme.Body.Weighty(
                        regular =
                            TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 20.8.sp,
                                fontFamily = FontFamily(Font(normalFont))
                            ),
                        medium =
                            TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 20.8.sp,
                                fontFamily = FontFamily(Font(mediumFont))
                            ),
                        bold =
                            TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 20.8.sp,
                                fontFamily = FontFamily(Font(boldFont))
                            )
                    ),
                small =
                    FontScheme.Body.Weighty(
                        regular =
                            TextStyle(
                                fontSize = 14.sp,
                                lineHeight = 15.4.sp,
                                fontFamily = FontFamily(Font(normalFont))
                            ),
                        medium =
                            TextStyle(
                                fontSize = 14.sp,
                                lineHeight = 15.4.sp,
                                fontFamily = FontFamily(Font(mediumFont))
                            ),
                        bold =
                            TextStyle(
                                fontSize = 14.sp,
                                lineHeight = 15.4.sp,
                                fontFamily = FontFamily(Font(boldFont))
                            )
                    )
            ),
        custom =
            FontScheme.Custom(
                carNumber =
                    TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.nummernschild))
                    )
            )
    )
}
