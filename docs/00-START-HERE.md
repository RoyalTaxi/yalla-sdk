# Yalla SDK — Start Here

> You're new to the SDK. This page tells you everything you need to know to start contributing.
> Read it once, then use it as a reference.

## What is yalla-sdk?

A Kotlin Multiplatform SDK powering the Yalla ride-hailing super-app (Android + iOS).
It provides: domain models, networking, UI components, maps, camera/media, Firebase integration,
and platform-native wrappers — all shared across both platforms.

## Quick Links

| What you need | Where to find it |
|---------------|------------------|
| What modules exist and their status | [`SDK_STATUS.md`](../SDK_STATUS.md) |
| How to build a component | [`02-COMPONENT-GUIDE.md`](02-COMPONENT-GUIDE.md) |
| Architecture & module map | [`01-ARCHITECTURE.md`](01-ARCHITECTURE.md) |
| Common recipes ("how do I...") | [`03-PATTERNS.md`](03-PATTERNS.md) |
| Publishing a new SDK version | [`04-PUBLISHING.md`](04-PUBLISHING.md) |
| Writing tests | [`05-TESTING.md`](05-TESTING.md) |
| Why decisions were made | [`06-DECISIONS.md`](06-DECISIONS.md) |
| Component gold standard rules | [`COMPONENT_STANDARD.md`](../COMPONENT_STANDARD.md) |

## First-Day Setup

### Prerequisites
- **JDK 21** (set as `JAVA_HOME`)
- **Android Studio** (latest stable) or **Fleet**
- **Xcode 16+** (for iOS builds)
- **CocoaPods** (`gem install cocoapods`)

### Clone & Build
```bash
git clone git@github.com:RoyalTaxi/yalla-sdk.git
cd yalla-sdk

# Verify everything compiles
./gradlew compileCommonMainKotlinMetadata   # Fast — shared code only
./gradlew build                              # Full — all platforms
```

### Project Structure
```
yalla-sdk/
├── bom/            # Bill of Materials (version alignment)
├── core/           # Domain models, Either, DataError, contracts
├── data/           # Networking, preferences, DataStore
├── design/         # Color tokens, font tokens, YallaTheme
├── foundation/     # BaseViewModel, location, locale, settings
├── primitives/     # Building-block UI components (buttons, fields, etc.)
├── composites/     # Composed UI components (cards, sheets, items, etc.)
├── platform/       # Native wrappers (sheets, navigation, switches)
├── maps/           # Google Maps + MapLibre abstraction
├── media/          # Camera, image picker, compression
├── firebase/       # Analytics, crashlytics, messaging
├── resources/      # Icons, strings, drawables
├── docs/           # ← You are here
└── SDK_STATUS.md   # Living dashboard of current status
```

## Your First Contribution

1. **Read** [`01-ARCHITECTURE.md`](01-ARCHITECTURE.md) — understand where code goes
2. **Read** [`SDK_STATUS.md`](../SDK_STATUS.md) — see what's done and what's open
3. **Pick a task** from the Known Issues section
4. **Follow** the patterns in [`03-PATTERNS.md`](03-PATTERNS.md)
5. **Write tests** per [`05-TESTING.md`](05-TESTING.md)
6. **Submit PR** — it will be reviewed against [`COMPONENT_STANDARD.md`](../COMPONENT_STANDARD.md)

## Conventions

- **Kotlin style**: Official Kotlin coding conventions, 120 char max
- **Formatting**: ktlint enforced (runs on commit hook)
- **Commits**: Conventional commits — `feat(module):`, `fix(module):`, `docs(module):`
- **Branches**: `feature/`, `fix/`, `chore/`, `docs/`
- **KDoc**: Every public API must have KDoc with `@param`, `@return`, `@since`
- **Immutability**: `val` over `var`, `@Immutable` on Colors/Dimens classes
- **Error handling**: `Either<D, E>` — never raw try-catch for business logic
