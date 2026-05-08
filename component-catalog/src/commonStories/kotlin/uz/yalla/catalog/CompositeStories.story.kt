package uz.yalla.catalog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.storytale.story
import uz.yalla.composites.card.AvatarCard
import uz.yalla.composites.card.BannerCard
import uz.yalla.composites.card.ContentCard
import uz.yalla.composites.card.FeedCard
import uz.yalla.composites.card.InfoCard
import uz.yalla.composites.card.NavigableCard
import uz.yalla.composites.card.SelectionCard
import uz.yalla.composites.card.SummaryCard
import uz.yalla.composites.card.ToggleCard
import uz.yalla.composites.drawer.DrawerItemIcon
import uz.yalla.composites.drawer.Navigable as DrawerNavigable
import uz.yalla.composites.drawer.SectionBackground
import uz.yalla.composites.item.AddressDot
import uz.yalla.composites.item.AddressItem
import uz.yalla.composites.item.ClickableValueItem
import uz.yalla.composites.item.IconItem
import uz.yalla.composites.item.ListItem
import uz.yalla.composites.item.NavigableItem
import uz.yalla.composites.item.PlaceButton
import uz.yalla.composites.item.PricingItem
import uz.yalla.composites.item.SelectableItem
import uz.yalla.composites.item.ValueItemView
import uz.yalla.composites.sheet.ActionPickerItem
import uz.yalla.composites.sheet.ActionPickerSheet
import uz.yalla.composites.sheet.ActionSheet
import uz.yalla.composites.sheet.BottomSheetCard
import uz.yalla.composites.sheet.ConfirmationSheet
import uz.yalla.composites.sheet.FormSheet
import uz.yalla.composites.sheet.OtpSheet
import uz.yalla.composites.sheet.SelectionSheet
import uz.yalla.composites.sheet.SheetDragHandle
import uz.yalla.composites.sheet.SheetHeader
import uz.yalla.composites.snackbar.Snackbar
import uz.yalla.composites.snackbar.SnackbarData
import uz.yalla.composites.snackbar.SnackbarHost
import uz.yalla.composites.snackbar.SnackbarState
import uz.yalla.composites.snackbar.SnackbarVariant
import uz.yalla.composites.view.CarNumber
import uz.yalla.composites.view.CarNumberDefaults
import uz.yalla.composites.view.CarNumberState
import uz.yalla.composites.view.EmptyState
import uz.yalla.composites.view.LocationPoint
import uz.yalla.composites.view.RouteLocation
import uz.yalla.composites.view.RouteView
import uz.yalla.design.theme.System
import uz.yalla.primitives.button.IconButton
import uz.yalla.primitives.button.PrimaryButton
import uz.yalla.primitives.button.SecondaryButton
import uz.yalla.primitives.button.TextButton
import uz.yalla.primitives.field.PrimaryField
import uz.yalla.resources.Res
import uz.yalla.resources.icons.Add
import uz.yalla.resources.icons.CardBonus
import uz.yalla.resources.icons.CheckCircle
import uz.yalla.resources.icons.Destination
import uz.yalla.resources.icons.Home
import uz.yalla.resources.icons.Language
import uz.yalla.resources.icons.Location
import uz.yalla.resources.icons.Origin
import uz.yalla.resources.icons.Places
import uz.yalla.resources.icons.Setting
import uz.yalla.resources.icons.TicketDiscount
import uz.yalla.resources.icons.Warning
import uz.yalla.resources.icons.X
import uz.yalla.resources.icons.YallaIcons
import uz.yalla.resources.img_banner_bonus
import uz.yalla.resources.img_banner_ride
import uz.yalla.resources.img_car_comfort
import uz.yalla.resources.img_car_standard
import uz.yalla.resources.img_light_order_history
import uz.yalla.resources.img_notification

val `Composites / Cards / product states` by story {
    CatalogFrame(width = 640) {
        var selectedPayment by remember { mutableStateOf("cash") }
        var enabled by remember { mutableStateOf(true) }
        var cardClicks by remember { mutableIntStateOf(0) }

        CatalogStack(spacing = 16) {
            CatalogTitle("Cards", "Clickable, selectable, toggle, avatar, banner, feed, info, and summary surfaces.")
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
                SelectionCard(
                    selected = selectedPayment == "cash",
                    onClick = { selectedPayment = "cash" },
                    modifier = Modifier.weight(1f),
                    leadingIcon = { CatalogIcon(YallaIcons.CardBonus) },
                ) {
                    Text("Cash")
                }
                ToggleCard(
                    checked = enabled,
                    onCheckedChange = { enabled = it },
                    modifier = Modifier.weight(1f),
                    leadingIcon = { CatalogIcon(YallaIcons.TicketDiscount) },
                ) {
                    Column {
                        Text("Bonus", style = System.font.body.base.bold)
                        Text("Use balance", style = System.font.body.caption, color = System.color.text.subtle)
                    }
                }
            }

            NavigableCard(
                onClick = { cardClicks++ },
                leadingIcon = { CatalogIcon(YallaIcons.Setting) },
                modifier = Modifier.fillMaxWidth(),
            ) { contentModifier ->
                Text("Open ride preferences", modifier = contentModifier, style = System.font.body.base.bold)
            }

            SummaryCard(
                onClick = { cardClicks++ },
                modifier = Modifier.fillMaxWidth(),
                header = {
                    RouteView(
                        locations =
                            listOf(
                                RouteLocation("Bobur Square", isOrigin = true),
                                RouteLocation("Tashkent City"),
                            ),
                        originIcon = rememberVectorPainter(YallaIcons.Origin),
                        destinationIcon = rememberVectorPainter(YallaIcons.Destination),
                    )
                },
                trailing = {
                    Text("42,000", style = System.font.title.base)
                    Image(
                        painter = painterResource(Res.drawable.img_car_standard),
                        contentDescription = null,
                        modifier = Modifier.width(96.dp),
                    )
                },
            ) {
                Text("12 min - Comfort", style = System.font.body.caption, color = System.color.text.subtle)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
                InfoCard(
                    onClick = { cardClicks++ },
                    modifier = Modifier.weight(1f),
                    trailingIcon = { IconTile(YallaIcons.Home) },
                    description = { Text("Saved pickup point", style = System.font.body.caption) },
                ) {
                    Text("Home", style = System.font.body.large.bold)
                }
                AvatarCard(
                    modifier = Modifier.weight(1f),
                    avatar = { AvatarCircle("AK") },
                    badge = { Text("VIP", style = System.font.body.caption, color = Color.White) },
                    name = { Text("Akmal Karimov", style = System.font.body.base.bold) },
                    content = { Text("4.96 rating", style = System.font.body.caption, color = System.color.text.subtle) },
                )
            }

            BannerCard(
                background = painterResource(Res.drawable.img_banner_bonus),
                onClick = { cardClicks++ },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column {
                    Text("Bonus balance", style = System.font.body.base.bold)
                    Spacer(Modifier.weight(1f))
                    Text("50,000", style = System.font.title.xLarge)
                }
            }

            FeedCard(
                onClick = { cardClicks++ },
                isHighlighted = enabled,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Driver arrived", style = System.font.body.base.bold)
                    Text("Your car is waiting near the entrance.", style = System.font.body.caption)
                }
            }
            InteractionCounter("Card click events", cardClicks)
        }
    }
}

val `Composites / Items / settings and ride selection` by story {
    CatalogFrame(width = 620) {
        var selectedTariff by remember { mutableStateOf("comfort") }
        var selectedLanguage by remember { mutableStateOf("uz") }
        var clicks by remember { mutableIntStateOf(0) }

        CatalogStack(spacing = 16) {
            CatalogTitle("Items", "List, icon, navigation, address, selectable, pricing, place, and value item patterns.")
            SectionBackground {
                ListItem(
                    title = { Text("Notifications") },
                    subtitle = { Text("Ride status and payment alerts") },
                    leadingContent = { IconTile(YallaIcons.Warning) },
                    trailingContent = { ValueItemView(12) },
                    onClick = { clicks++ },
                )
                IconItem(
                    title = { Text("Language") },
                    subtitle = { Text("Uzbek") },
                    icon = { CatalogIcon(YallaIcons.Language) },
                    trailingContent = { Text("UZ", style = System.font.body.caption) },
                    onClick = { clicks++ },
                )
                NavigableItem(
                    title = { Text("Payment methods") },
                    subtitle = { Text("Cards, cash, and bonuses") },
                    icon = { CatalogIcon(YallaIcons.CardBonus) },
                    onClick = { clicks++ },
                )
            }

            AddressItem(
                locations = listOf("Home", "Airport", "Tashkent City"),
                placeholder = { Text("Where to?") },
                leadingContent = { AddressDot(System.color.background.brand) },
                trailingContent = { CatalogIcon(YallaIcons.Places) },
                onClick = { clicks++ },
                modifier = Modifier.fillMaxWidth(),
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                SelectableItem(
                    title = { Text("Uzbek") },
                    isSelected = selectedLanguage == "uz",
                    onSelect = { selectedLanguage = "uz" },
                    icon = { CatalogIcon(YallaIcons.Language) },
                    modifier = Modifier.weight(1f),
                )
                SelectableItem(
                    title = { Text("English") },
                    isSelected = selectedLanguage == "en",
                    onSelect = { selectedLanguage = "en" },
                    icon = { CatalogIcon(YallaIcons.Language) },
                    modifier = Modifier.weight(1f),
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                PricingItem(
                    name = { Text("Standard") },
                    price = { Text("28,000") },
                    selected = selectedTariff == "standard",
                    onClick = { selectedTariff = "standard" },
                    modifier = Modifier.weight(1f),
                    image = {
                        Image(
                            painter = painterResource(Res.drawable.img_car_standard),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                )
                PricingItem(
                    name = { Text("Comfort") },
                    price = { Text("42,000") },
                    selected = selectedTariff == "comfort",
                    onClick = { selectedTariff = "comfort" },
                    modifier = Modifier.weight(1f),
                    image = {
                        Image(
                            painter = painterResource(Res.drawable.img_car_comfort),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                PlaceButton(
                    text = "Home",
                    onClick = { clicks++ },
                    leadingIcon = { CatalogIcon(YallaIcons.Home) },
                    trailingIcon = { CatalogIcon(YallaIcons.Add) },
                    modifier = Modifier.weight(1f),
                )
                ClickableValueItem(
                    bonus = 50000,
                    onClick = { clicks++ },
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
            }
            InteractionCounter("Item click events", clicks)
        }
    }
}

val `Composites / Views / empty, route, location, car number` by story {
    CatalogFrame(width = 620) {
        CatalogStack(spacing = 18) {
            CatalogTitle("Views", "Reusable screen-level fragments and domain display components.")
            Row(horizontalArrangement = Arrangement.spacedBy(18.dp), modifier = Modifier.fillMaxWidth()) {
                ContentCard(modifier = Modifier.weight(1f)) {
                    EmptyState(
                        image = {
                            Image(
                                painter = painterResource(Res.drawable.img_light_order_history),
                                contentDescription = null,
                                modifier = Modifier.height(104.dp),
                                contentScale = ContentScale.Fit,
                            )
                        },
                        title = { Text("No rides yet") },
                        description = { Text("Completed rides will appear here.") },
                        action = {
                            PrimaryButton(onClick = {}) { Text("Book") }
                        },
                    )
                }
                ContentCard(modifier = Modifier.weight(1f)) {
                    CatalogStack {
                        RouteView(
                            locations =
                                listOf(
                                    RouteLocation("Current location", isOrigin = true),
                                    RouteLocation("Tashkent City Mall"),
                                    RouteLocation("Airport Terminal 2"),
                                ),
                            originIcon = rememberVectorPainter(YallaIcons.Origin),
                            destinationIcon = rememberVectorPainter(YallaIcons.Destination),
                        )
                        Separator()
                        LocationPoint(
                            icon = rememberVectorPainter(YallaIcons.Location),
                            label = "Pinned pickup point",
                        )
                        CarNumber(
                            state = CarNumberState(code = "01", number = listOf("A", "777", "AA")),
                            dimens = CarNumberDefaults.dimens(height = 42.dp),
                        )
                    }
                }
            }
        }
    }
}

val `Composites / Sheets / action sheet` by story {
    CatalogFrame(width = 520, contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
        var actions by remember { mutableIntStateOf(0) }

        ActionSheet(
            isVisible = true,
            onDismissRequest = {},
            title = "Cancel ride?",
            message = "The driver is already on the way. Cancellation may affect your priority.",
            primaryAction = "Keep ride",
            onPrimaryAction = { actions++ },
            secondaryAction = "Cancel ride",
            onSecondaryAction = { actions++ },
        )
        InteractionCounter("Action events", actions, modifier = Modifier.padding(16.dp))
    }
}

val `Composites / Sheets / action picker` by story {
    CatalogFrame(width = 520, contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
        var actions by remember { mutableIntStateOf(0) }

        ActionPickerSheet(
            isVisible = true,
            onDismissRequest = {},
            title = "Change pickup",
            items =
                listOf(
                    ActionPickerItem(
                        title = "Focus my location",
                        icon = rememberVectorPainter(YallaIcons.Location),
                        onClick = { actions++ },
                    ),
                    ActionPickerItem(
                        title = "Choose saved place",
                        icon = rememberVectorPainter(YallaIcons.Home),
                        onClick = { actions++ },
                    ),
                    ActionPickerItem(
                        title = "Clear route",
                        icon = rememberVectorPainter(YallaIcons.X),
                        onClick = { actions++ },
                        isDestructive = true,
                    ),
                ),
        )
        InteractionCounter("Picker events", actions, modifier = Modifier.padding(16.dp))
    }
}

val `Composites / Sheets / confirmation` by story {
    CatalogFrame(width = 520, contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
        var confirmed by remember { mutableIntStateOf(0) }

        ConfirmationSheet(
            isVisible = true,
            onDismissRequest = {},
            image = painterResource(Res.drawable.img_notification),
            title = "Notifications enabled",
            description = "You will receive ride status, driver arrival, and payment updates.",
            actionText = "Done",
            onAction = { confirmed++ },
            sheetName = "Notifications",
        )
        InteractionCounter("Confirm events", confirmed, modifier = Modifier.padding(16.dp))
    }
}

val `Composites / Sheets / form and otp` by story {
    CatalogFrame(width = 520, contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
        var mode by remember { mutableStateOf("form") }
        var code by remember { mutableStateOf("12") }
        val cardNumber = androidx.compose.foundation.text.input.rememberTextFieldState("8600 12")

        if (mode == "form") {
            FormSheet(
                isVisible = true,
                onDismissRequest = {},
                title = "Add card",
                action = {
                    PrimaryButton(onClick = { mode = "otp" }, modifier = Modifier.fillMaxWidth()) {
                        Text("Continue")
                    }
                },
            ) {
                PrimaryField(
                    state = cardNumber,
                    placeholder = { Text("Card number") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(12.dp))
                TextButton(onClick = { mode = "otp" }, modifier = Modifier.fillMaxWidth()) {
                    Text("Show OTP sheet")
                }
            }
        } else {
            OtpSheet(
                isVisible = true,
                onDismissRequest = {},
                code = code,
                onCodeChange = { code = it },
                codeLength = 5,
                onCodeComplete = { code = it },
                headline = "Verify card",
                description = "Enter the 5-digit code sent by your bank.",
                title = "Verification",
                resendButton = {
                    TextButton(onClick = { code = "" }) { Text("Resend code") }
                },
                confirmButton = {
                    PrimaryButton(onClick = { mode = "form" }, modifier = Modifier.fillMaxWidth()) {
                        Text("Back to form")
                    }
                },
            )
        }
    }
}

val `Composites / Sheets / selection` by story {
    CatalogFrame(width = 520, contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
        val languages = listOf("Uzbek", "English", "Russian")
        var selected by remember { mutableStateOf(languages.first()) }

        SelectionSheet(
            isVisible = true,
            onDismissRequest = {},
            title = "Language",
            items = languages,
            selectedItem = selected,
            onSelect = { selected = it },
            itemKey = { it },
        ) { item, isSelected ->
            SelectableItem(
                title = { Text(item) },
                icon = { CatalogIcon(YallaIcons.Language) },
                isSelected = isSelected,
                onSelect = { selected = item },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

val `Composites / Drawers, snackbars, sheets` by story {
    CatalogFrame(width = 620) {
        val hostState = remember { SnackbarHostState() }
        var clicks by remember { mutableIntStateOf(0) }

        CatalogStack(spacing = 18) {
            CatalogTitle("System composites", "Drawer rows, snackbar variants, and bottom-sheet anatomy.")
            SectionBackground {
                DrawerNavigable(
                    title = { Text("Settings") },
                    description = { Text("Language, theme, notifications") },
                    leadingIcon = {
                        DrawerItemIcon(painter = rememberVectorPainter(YallaIcons.Setting))
                    },
                    onClick = { clicks++ },
                )
                DrawerNavigable(
                    title = { Text("Saved places") },
                    description = { Text("Home, work, frequent stops") },
                    leadingIcon = {
                        DrawerItemIcon(painter = rememberVectorPainter(YallaIcons.Places))
                    },
                    trailingView = { ValueItemView(3) },
                    onClick = { clicks++ },
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Snackbar(
                    state =
                        SnackbarState(
                            message = "Card added",
                            variant = SnackbarVariant.Success,
                            icon = rememberVectorPainter(YallaIcons.CheckCircle),
                            dismissIcon = rememberVectorPainter(YallaIcons.X),
                        ),
                    onDismiss = {},
                    modifier = Modifier.weight(1f),
                )
                Snackbar(
                    state =
                        SnackbarState(
                            message = "Payment failed",
                            variant = SnackbarVariant.Error,
                            icon = rememberVectorPainter(YallaIcons.Warning),
                            dismissIcon = rememberVectorPainter(YallaIcons.X),
                        ),
                    onDismiss = {},
                    modifier = Modifier.weight(1f),
                )
            }

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(76.dp),
            ) {
                SnackbarHost(
                    data = SnackbarData("Host renders custom snackbar", isSuccess = true),
                    successIcon = rememberVectorPainter(YallaIcons.CheckCircle),
                    errorIcon = rememberVectorPainter(YallaIcons.Warning),
                    dismissIcon = rememberVectorPainter(YallaIcons.X),
                    hostState = hostState,
                    onDismiss = {},
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            }

            BottomSheetCard(
                offset = 0f,
                onHeightChanged = {},
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 18.dp),
                ) {
                    SheetDragHandle()
                    SheetHeader(
                        title = "Confirm ride",
                        onClose = {},
                        actions = {
                            IconButton(onClick = {}) { CatalogIcon(YallaIcons.CheckCircle) }
                        },
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(horizontal = 18.dp),
                    ) {
                        PrimaryField(
                            state = androidx.compose.foundation.text.input.rememberTextFieldState("Bobur Square"),
                            placeholder = { Text("Pickup") },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            SecondaryButton(onClick = {}, modifier = Modifier.weight(1f)) { Text("Cancel") }
                            PrimaryButton(onClick = { clicks++ }, modifier = Modifier.weight(1f)) { Text("Confirm") }
                        }
                    }
                }
            }
            InteractionCounter("System click events", clicks)
        }
    }
}
