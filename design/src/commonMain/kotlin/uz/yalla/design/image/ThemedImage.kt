package uz.yalla.design.image

import org.jetbrains.compose.resources.DrawableResource
import uz.yalla.resources.Res
import uz.yalla.resources.img_dark_blurry_logo
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
import uz.yalla.resources.img_light_close_circle
import uz.yalla.resources.img_light_map_pin
import uz.yalla.resources.img_light_notification_mute
import uz.yalla.resources.img_light_order_history
import uz.yalla.resources.img_light_order_search
import uz.yalla.resources.img_light_safety
import uz.yalla.resources.img_light_shield_check
import uz.yalla.resources.img_light_tariff_card
import uz.yalla.resources.img_light_trash_can

enum class ThemedImage(
    val light: DrawableResource,
    val dark: DrawableResource,
) {
    BlurryLogo(Res.drawable.img_light_blurry_logo, Res.drawable.img_dark_blurry_logo),
    CloseCircle(Res.drawable.img_light_close_circle, Res.drawable.img_dark_close_circle),
    MapPin(Res.drawable.img_light_map_pin, Res.drawable.img_dark_map_pin),
    NotificationMute(Res.drawable.img_light_notification_mute, Res.drawable.img_dark_notification_mute),
    OrderHistory(Res.drawable.img_light_order_history, Res.drawable.img_dark_order_history),
    OrderSearch(Res.drawable.img_light_order_search, Res.drawable.img_dark_order_search),
    Safety(Res.drawable.img_light_safety, Res.drawable.img_dark_safety),
    ShieldCheck(Res.drawable.img_light_shield_check, Res.drawable.img_dark_shield_check),
    TariffCard(Res.drawable.img_light_tariff_card, Res.drawable.img_dark_tariff_card),
    TrashCan(Res.drawable.img_light_trash_can, Res.drawable.img_dark_trash_can),
}
