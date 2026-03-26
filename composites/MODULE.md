# Module composites

Pre-built composite UI components for the Yalla SDK.

This module assembles lower-level design tokens, primitives, and platform controls into
ready-to-use, fully themed UI components. Each component follows the
**State + Defaults (colors/style/dimens)** pattern for consistent customization.

## Architecture

Components are organised by purpose:
- **Cards** — content containers for addresses, history, payments, profiles, and promotions
- **Items** — list and selection items for actions, locations, services, and tariffs
- **Sheets** — modal and expandable bottom sheets for actions, confirmations, and date picking
- **Drawers** — navigation drawer building blocks with icon and section styling
- **Snackbar** — transient success/error feedback with global controller
- **Views** — display-only composables for routes, vehicle plates, empty states, and car numbers

# Package uz.yalla.composites.card

Content cards: [ContentCard] (building block), [SelectionCard], [ToggleCard], [BannerCard],
[SummaryCard], [FeedCard], [InfoCard], [AvatarCard], [AddressCard], [NavigableCard],
[ProfileCard], [PromotionCard], [SelectableCard], and [SwitchCard].

# Package uz.yalla.composites.drawer

Drawer building blocks: [DrawerItemIcon] for menu icons, [SectionBackground] for grouped
sections, and [Navigable] for clickable menu items with chevron.

# Package uz.yalla.composites.item

List and selection items: [ListItem] (building block), [IconItem] (icon + text + trailing),
[NavigableItem] (IconItem + arrow), [SelectableItem], [PricingItem], [AddressItem],
[PlaceButton], and [ValueItem].

# Package uz.yalla.composites.sheet

Bottom sheets: [Sheet] (modal wrapper), [BottomSheetCard] (animated card), [ExpandableSheet]
(collapsed/expanded), [HeaderableSheet] (header/body/footer), [ActionSheet], [ActionPickerSheet],
[ConfirmationSheet], [DatePickerSheet], and [SheetSnackbarHost].

# Package uz.yalla.composites.snackbar

Snackbar system: [Snackbar] composable, [SnackbarHost] for hosting, and
[SnackbarController] for global event-driven display.

# Package uz.yalla.composites.view

Display views: [CarNumber] (Uzbekistan license plate), [RouteView] (origin/destination list),
[LocationPoint], and [EmptyState].

# Package uz.yalla.composites.util

Internal utilities: [PaymentKind.toPainter][uz.yalla.composites.util.toPainter] and
[PaymentKind.getStringResource][uz.yalla.composites.util.getStringResource] for payment type
resource resolution.
