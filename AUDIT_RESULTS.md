# Yalla SDK — Component Usage Audit Results

> Audit date: 2026-03-25
> Scope: All YallaClient feature modules + composeApp
> Purpose: Map every SDK component usage before gold standard refactoring

---

## Usage Summary

### Buttons (Primitives)

| Component | Usages | Files | Notes |
|-----------|--------|-------|-------|
| PrimaryButton | 24 | 24 | Most used component in entire SDK |
| SecondaryButton | 4 | 4 | ConnectivitySheet, ForceUpdate, Outside, Comment |
| TextButton | 3 | 3 | OTP resend (auth + billing), skip onboarding |
| BottomSheetButton | 3 | 3 | OrderSheet, SearchSheet, DetailsSheet |
| GenderButton | 2 | 2 | Register + Account (male/female selection) |
| NavigationButton | 1 | 1 | InfoScreen |
| **GradientButton** | **0** | **0** | **NOT USED — removal candidate** |
| **LocationEnableButton** | **0** | **0** | **NOT USED — removal candidate** |
| **CountdownActionButton** | **0** | **0** | **NOT USED — removal candidate** |
| **SupportButton** | **0** | **0** | **NOT USED — removal candidate** |

**Key pattern:** All buttons use `*ButtonState(text, enabled, loading)` — content-state bundling anti-pattern.

### Fields (Primitives)

| Component | Usages | Files | Notes |
|-----------|--------|-------|-------|
| PrimaryField | 13 | 8 | Name, surname, comment, promo code, place details |
| NumberField | 3 | 2 | Phone input (login + driver signup) |
| DateField | 4 | 2 | Birth date (register + account) |
| PinRow | 2 | 2 | OTP: 5-digit login + 6-digit payment |
| **PinView** | **0** | **0** | **NOT USED — removal candidate** |

**Key pattern:** All fields use `rememberTextFieldState()` + `LaunchedEffect` for bidirectional sync.

### Cards (Composites)

| Component | Usages | Files | Notes |
|-----------|--------|-------|-------|
| NavigableCard | 6 | 5 | Add card, order more, trip info, comment, delete |
| PaymentTypeCard | 6 | 3 | Card/cash selection in payment flows |
| EnableBonusCard | 2 | 2 | Bonus toggle in payment/bonus sheets |
| BonusCard | 2 | 2 | Bonus balance display |
| HistoryCard | 1 | 1 | Past order in history list |
| NotificationCard | 1 | 1 | Notification item |
| PlaceCard | 1 | 1 | Home/Work saved places |
| **ContentCard** | **0** | **0** | **NOT USED — removal candidate** |
| **AddressCard** | **0** | **0** | **NOT USED — removal candidate** |
| **ProfileCard** | **0** | **0** | **NOT USED — removal candidate** |
| **PromotionCard** | **0** | **0** | **NOT USED — removal candidate** |
| **SelectableCard** | **0** | **0** | **NOT USED — removal candidate** |
| **SwitchCard** | **0** | **0** | **NOT USED — removal candidate** |

### Sheets (Composites)

| Component | Usages | Files | Notes |
|-----------|--------|-------|-------|
| Sheet | 13 | 13 | Base modal sheet — most used composite |
| ConfirmationSheet | 5 | 3 | Delete/cancel confirmations |
| ActionPickerSheet | 3 | 2 | Edit/delete action selection |
| DatePickerSheet | 2 | 2 | Birth date in register/account |
| BottomSheetCard | 2 | 2 | Start + Outside floating cards |
| ExpandableSheet | 1 | 1 | TaxiSheet (main order creation) |
| HeaderableSheet | 1 | 1 | SearchSheet (driver search) |
| **ActionSheet** | **?** | **?** | **Needs verification** |

### Items (Composites)

| Component | Usages | Files | Notes |
|-----------|--------|-------|-------|
| SelectableItem | 2 | 2 | Language/theme/map settings |
| LocationItem | 2 | 1 | Origin/destination in TaxiSheet |
| BrandServiceItem | 2 | 1 | Brand display in history |
| BonusAmountItem | 2 | 2 | Menu + Home bonus display |
| TariffItem | 1 | 1 | Car tariff selection in LazyRow |
| ServiceItem | 1 | 1 | Service type selection |
| LocationButton | 2 | 2 | Map + Start sheets |
| **ListItem** | **?** | **?** | **Needs verification** |
| **RadioItem** | **?** | **?** | **Needs verification** |
| **ActionItem** | **?** | **?** | **Needs verification** |

### Indicators (Primitives)

| Component | Usages | Files | Notes |
|-----------|--------|-------|-------|
| DotsIndicator | 1 | 1 | Onboarding page indicator |
| **StripedProgressbar** | **?** | **?** | **Needs verification** |
| **SplashOverlay** | **?** | **?** | **Needs verification** |
| **LoadingIndicator (SDK)** | **?** | **?** | **Needs verification** |

### TopBars (Primitives)

| Component | Usages | Files | Notes |
|-----------|--------|-------|-------|
| TopBar | 5 | 1 | AppNavHost (Menu, Account, HistoryItem, PlacesItem, Info) |
| LargeTopBar | 8 | 1 | AppNavHost (Payment, PromoCode, History, Places, Notifications, Contact, Settings, Driver) |

### Views (Composites)

| Component | Usages | Files | Notes |
|-----------|--------|-------|-------|
| EmptyState | 3 | 3 | Search, History, Notifications |
| CarNumber | 3 | 3 | Orders, Details, History |
| **VehiclePlate** | **0** | **0** | **NOT USED — removal candidate** |
| **RouteView** | **0** | **0** | **NOT USED — removal candidate** |

### Drawer (Composites)

| Component | Usages | Files | Notes |
|-----------|--------|-------|-------|
| Navigable | 16+ | 4 | Menu, Settings, Info, Details |
| DrawerItemIcon | 14+ | 2 | Menu, Settings |
| SectionBackground | 5+ | 3 | Menu, Settings, Info |

### Platform Components

| Component | Usages | Files | Notes |
|-----------|--------|-------|-------|
| SheetIconButton | 40 | 20+ | Close button in EVERY sheet — most used platform component |
| NativeSheet | 15 | 13 | Platform-native sheets |
| NativeCircleIconButton | 6 | 2 | Home + Map overlay buttons |
| ToolbarAction | 17+ | 6 | Navigation toolbar actions |
| SystemBarColors | 1 | 1 | MainActivity |
| ObserveAsEvents | 2 | 2 | Android + iOS entry points |
| MaskFormatter | 1 | 1 | Phone formatting |
| NativeLoadingIndicator | 1 | 1 | History locations card |
| **NativeSwitch** | **0** | **0** | **NOT USED in client (SDK uses internally)** |
| **NativeSquircleIconButton** | **0** | **0** | **NOT USED — removal candidate** |
| **NativeWheelDatePicker** | **0** | **0** | **NOT USED directly (via DatePickerSheet)** |

### Foundation

| Component | Usages | Notes |
|-----------|--------|-------|
| BaseViewModel | 32 | Every ViewModel extends it |

---

## Unused Components — Removal Candidates

**Total: 15 components with zero usage**

### Primitives (5)
- `GradientButton`
- `LocationEnableButton`
- `CountdownActionButton`
- `SupportButton`
- `PinView`

### Composites (8)
- `ContentCard`
- `AddressCard`
- `ProfileCard`
- `PromotionCard`
- `SelectableCard`
- `SwitchCard`
- `VehiclePlate`
- `RouteView`

### Platform (1)
- `NativeSquircleIconButton`

### Decision needed:
- Keep for future features?
- Remove to reduce maintenance surface?
- Mark as `@Deprecated` with migration note?

---

## Top 10 Most Used Components

| # | Component | Usages | Module |
|---|-----------|--------|--------|
| 1 | **SheetIconButton** | 40 | platform |
| 2 | **BaseViewModel** | 32 | foundation |
| 3 | **PrimaryButton** | 24 | primitives |
| 4 | **ToolbarAction** | 17+ | primitives |
| 5 | **Navigable** | 16+ | composites |
| 6 | **NativeSheet** | 15 | platform |
| 7 | **DrawerItemIcon** | 14+ | composites |
| 8 | **PrimaryField** | 13 | primitives |
| 9 | **Sheet** | 13 | composites |
| 10 | **LargeTopBar** | 8 | primitives |

---

## Hidden Components in Features (Extract to SDK)

### Critical (Duplicate code)
- **ValidationSheet** — `feature/auth` + `feature/billing` (OTP validation, nearly identical)

### High Priority
- DriverCard, DetailsCard, DetailRow (feature/history)
- LocationsCard, Route, Location (feature/history)
- SearchLocationField, FoundLocationView (feature/location)
- SavedAddressCard (feature/main)
- CommunicationOptionCard (feature/account)
- DeleteCardItem (feature/billing)

### Medium Priority
- ClientCard, FlavorItem, OrdersCard, EnterLocationCard, BecomeDriverCard

---

## Key Patterns Observed

1. **State bundling** — All buttons use `*State(text, enabled, loading)` — BANNED by gold standard
2. **Field sync** — All fields use `rememberTextFieldState()` + `LaunchedEffect` — consider simplifying
3. **Sheet close** — `SheetIconButton` (40 usages!) used in every sheet for close action
4. **Intent dispatch** — All onClick handlers dispatch MVI intents
5. **Lazy rendering** — Lists use LazyColumn/LazyRow consistently
6. **Design tokens** — `System.color.*` and `System.font.*` used consistently
7. **No size override** — No call site overrides `ButtonSize` — default Medium is always used
