package uz.yalla.design.image

import org.jetbrains.compose.resources.DrawableResource
import uz.yalla.resources.Res
import uz.yalla.resources.img_dark_blurry_logo
import uz.yalla.resources.img_dark_close_circle
import uz.yalla.resources.img_dark_login
import uz.yalla.resources.img_dark_logout
import uz.yalla.resources.img_dark_map_pin
import uz.yalla.resources.img_dark_notification_mute
import uz.yalla.resources.img_dark_order_history
import uz.yalla.resources.img_dark_order_search
import uz.yalla.resources.img_dark_safety
import uz.yalla.resources.img_dark_shield_check
import uz.yalla.resources.img_dark_tariff_card
import uz.yalla.resources.img_dark_trash_can
import uz.yalla.resources.img_light_blurry_logo
import uz.yalla.resources.img_light_close_circle
import uz.yalla.resources.img_light_login
import uz.yalla.resources.img_light_logout
import uz.yalla.resources.img_light_map_pin
import uz.yalla.resources.img_light_notification_mute
import uz.yalla.resources.img_light_order_history
import uz.yalla.resources.img_light_order_search
import uz.yalla.resources.img_light_safety
import uz.yalla.resources.img_light_shield_check
import uz.yalla.resources.img_light_tariff_card
import uz.yalla.resources.img_light_trash_can
import uz.yalla.resources.img_wifi

// Image entries originate from yalla-design; this file is hand-finished afterwards (e.g. the
// `assetName` iOS bridge key and the `Wifi` entry) and is authoritative for this module.

// TODO(quality, needs-decision): `assetName` is a hand-maintained stringly-typed key that
//  duplicates the typed light/dark pair for the iOS bridge and can silently drift from the Xcode
//  asset catalog. Deriving it from the resource (or moving the bridge mapping out of the public
//  enum) is a breaking ABI change and needs owner sign-off / a cross-repo decision.
/**
 * A light/dark image pair. Resolve it to a `Painter` for the current appearance with
 * [themedPainter]; do not read [light]/[dark] directly.
 *
 * @property light the resource shown in the light appearance.
 * @property dark the resource shown in the dark appearance.
 * @property assetName the matching iOS asset-catalog name, used only by the iOS bridge.
 */
public enum class ThemedImage(
    public val light: DrawableResource,
    public val dark: DrawableResource,
    public val assetName: String
) {
    BlurryLogo(Res.drawable.img_light_blurry_logo, Res.drawable.img_dark_blurry_logo, "img_blurry_logo"),
    CloseCircle(Res.drawable.img_light_close_circle, Res.drawable.img_dark_close_circle, "img_close_circle"),
    Login(Res.drawable.img_light_login, Res.drawable.img_dark_login, "img_login"),
    Logout(Res.drawable.img_light_logout, Res.drawable.img_dark_logout, "img_logout"),
    MapPin(Res.drawable.img_light_map_pin, Res.drawable.img_dark_map_pin, "img_map_pin"),
    NotificationMute(
        Res.drawable.img_light_notification_mute,
        Res.drawable.img_dark_notification_mute,
        "img_notification_mute"
    ),
    OrderHistory(Res.drawable.img_light_order_history, Res.drawable.img_dark_order_history, "img_order_history"),
    OrderSearch(Res.drawable.img_light_order_search, Res.drawable.img_dark_order_search, "img_order_search"),
    Safety(Res.drawable.img_light_safety, Res.drawable.img_dark_safety, "img_safety"),
    ShieldCheck(Res.drawable.img_light_shield_check, Res.drawable.img_dark_shield_check, "img_shield_check"),
    TariffCard(Res.drawable.img_light_tariff_card, Res.drawable.img_dark_tariff_card, "img_tariff_card"),
    TrashCan(Res.drawable.img_light_trash_can, Res.drawable.img_dark_trash_can, "img_trash_can"),
    Wifi(Res.drawable.img_wifi, Res.drawable.img_wifi, "img_wifi")
}
