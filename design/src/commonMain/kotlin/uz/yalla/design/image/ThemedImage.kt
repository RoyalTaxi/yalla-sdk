package uz.yalla.design.image

import org.jetbrains.compose.resources.DrawableResource
import uz.yalla.resources.Res
import uz.yalla.resources.img_dark_blurry_logo
import uz.yalla.resources.img_dark_login
import uz.yalla.resources.img_dark_logout
import uz.yalla.resources.img_dark_close_circle
import uz.yalla.resources.img_dark_map_pin
import uz.yalla.resources.img_dark_notification_mute
import uz.yalla.resources.img_dark_order_history
import uz.yalla.resources.img_dark_order_search
import uz.yalla.resources.img_dark_safety
import uz.yalla.resources.img_dark_shield_check
import uz.yalla.resources.img_dark_tariff_card
import uz.yalla.resources.img_dark_trash_can
import uz.yalla.resources.img_light_blurry_logo
import uz.yalla.resources.img_light_login
import uz.yalla.resources.img_light_logout
import uz.yalla.resources.img_light_close_circle
import uz.yalla.resources.img_light_map_pin
import uz.yalla.resources.img_light_notification_mute
import uz.yalla.resources.img_light_order_history
import uz.yalla.resources.img_light_order_search
import uz.yalla.resources.img_light_safety
import uz.yalla.resources.img_light_shield_check
import uz.yalla.resources.img_light_tariff_card
import uz.yalla.resources.img_light_trash_can

/**
 * Theme-aware image registry mapping each logical image to its light and dark drawable variants.
 *
 * Each entry pairs a [light] and [dark] [DrawableResource], allowing the UI layer to
 * select the correct variant based on the current theme. Use with [themedPainter] for
 * seamless theme-aware image rendering.
 *
 * ## Usage
 *
 * ```kotlin
 * Image(
 *     painter = themedPainter(ThemedImage.Login),
 *     contentDescription = "Login illustration",
 * )
 * ```
 *
 * @property light Drawable resource for light mode.
 * @property dark Drawable resource for dark mode.
 * @since 0.0.1
 */
enum class ThemedImage(
    val light: DrawableResource,
    val dark: DrawableResource,
) {
    /** Blurred Yalla logo background decoration. */
    BlurryLogo(Res.drawable.img_light_blurry_logo, Res.drawable.img_dark_blurry_logo),

    /** Circle close/dismiss icon. */
    CloseCircle(Res.drawable.img_light_close_circle, Res.drawable.img_dark_close_circle),

    /** Login screen illustration. */
    Login(Res.drawable.img_light_login, Res.drawable.img_dark_login),

    /** Logout confirmation illustration. */
    Logout(Res.drawable.img_light_logout, Res.drawable.img_dark_logout),

    /** Map pin / location marker illustration. */
    MapPin(Res.drawable.img_light_map_pin, Res.drawable.img_dark_map_pin),

    /** Notification muted state illustration. */
    NotificationMute(Res.drawable.img_light_notification_mute, Res.drawable.img_dark_notification_mute),

    /** Order history empty/placeholder illustration. */
    OrderHistory(Res.drawable.img_light_order_history, Res.drawable.img_dark_order_history),

    /** Order search empty-state illustration. */
    OrderSearch(Res.drawable.img_light_order_search, Res.drawable.img_dark_order_search),

    /** Safety feature illustration. */
    Safety(Res.drawable.img_light_safety, Res.drawable.img_dark_safety),

    /** Shield/verification check illustration. */
    ShieldCheck(Res.drawable.img_light_shield_check, Res.drawable.img_dark_shield_check),

    /** Tariff/pricing card illustration. */
    TariffCard(Res.drawable.img_light_tariff_card, Res.drawable.img_dark_tariff_card),

    /** Delete/trash confirmation illustration. */
    TrashCan(Res.drawable.img_light_trash_can, Res.drawable.img_dark_trash_can),
}
