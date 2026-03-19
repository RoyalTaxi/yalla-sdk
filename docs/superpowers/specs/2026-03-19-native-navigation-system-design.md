# Native Navigation System — Design Spec

## Goal

Build a cross-platform navigation system for yalla-sdk's platform module that uses **Decompose** for navigation logic/lifecycle and **NativeNavHost** for platform-native rendering. iOS gets UINavigationController with native transitions, large titles, swipe-back. Android gets Material3 TopAppBar with animated transitions. App code stays 100% in commonMain.

## Architecture

```
┌─────────────────────────────────────────────┐
│              commonMain (App)                │
│                                              │
│  RootComponent (Decompose)                   │
│  ├── StackNavigation<Config>                 │
│  ├── childStack(source, childFactory)        │
│  └── Child components with lifecycle         │
│                                              │
│  ScreenProvider (App implements)              │
│  ├── configFor(route) → ScreenConfig         │
│  └── Content(route, navigator, toolbar)      │
│                                              │
│  NativeNavHost(rootComponent, screenProvider) │
│  ├── expect fun (commonMain)                 │
│  └── actual fun per platform                 │
└─────────────────────────────────────────────┘
         │                          │
    ┌────▼────┐              ┌──────▼──────┐
    │  iOS    │              │   Android   │
    │ actual  │              │   actual    │
    ├─────────┤              ├─────────────┤
    │ UINav   │              │ BackStack + │
    │ Controller              │ Animated   │
    │ + native│              │ Content +  │
    │ UINav   │              │ Material3  │
    │ Bar     │              │ TopAppBar  │
    └─────────┘              └─────────────┘
```

## Dependencies

- **Decompose** 3.2.x (latest stable) — navigation logic, lifecycle, state preservation
- **Essenty** (transitive via Decompose) — Lifecycle, StateKeeper, InstanceKeeper, BackHandler
- No dependency on compose-cupertino (we write our own NativeNavHost)

### Dependency Strategy

Decompose is added to the **platform** module as `api` (not `implementation`) because `NativeNavHost` takes `NativeRootComponent` which exposes Decompose types (`Value`, `ChildStack`, `ComponentContext`). This means SDK consumers transitively get Decompose + Essenty.

**Binary size impact:** Decompose is ~300KB, Essenty ~100KB. Acceptable for a navigation library.

**Scope:** Decompose is used ONLY in the `platform` module. No other SDK modules depend on it. Consumer apps use Decompose in their `composeApp` module to create `NativeRootComponent`.

```toml
# gradle/libs.versions.toml
decompose = "3.2.2"

[libraries]
decompose = { module = "com.arkivanov.decompose:decompose", version.ref = "decompose" }
decompose-compose = { module = "com.arkivanov.decompose:extensions-compose", version.ref = "decompose" }
```

```kotlin
// platform/build.gradle.kts
commonMain.dependencies {
    api(libs.decompose)
    api(libs.decompose.compose)
}
```

## Core API (commonMain)

### Route System

```kotlin
package uz.yalla.platform.navigation

// Marker interface — app extends with sealed class
interface Route

// Large title mode (iOS renders natively, Android maps to TopAppBar)
enum class LargeTitleMode {
    Always,  // Static large title (Menu, Settings screens)
    Never,   // Small title (Detail, Form screens)
}

// Per-screen configuration
// Note: title is String (not StringResource) because iOS sets navigationItem.title
// outside @Composable context. App resolves StringResource → String before creating config.
data class ScreenConfig(
    val title: String? = null,
    val largeTitleMode: LargeTitleMode = LargeTitleMode.Never,
    val showsNavigationBar: Boolean = true,
    val transparentNavigationBar: Boolean = false,
)
```

### Dynamic Toolbar

```kotlin
// Compose screens can update toolbar actions at runtime
@Stable
class ToolbarState {
    var actions: List<ToolbarAction> by mutableStateOf(emptyList())
}

// ToolbarAction already exists in primitives module:
// sealed interface ToolbarAction {
//     data class Text(val label: String, val onClick: () -> Unit)
//     data class Icon(val icon: ToolbarIcon, val onClick: () -> Unit)
// }
```

### Screen Provider

```kotlin
// App implements this — generic to preserve type safety (no unchecked casts)
interface ScreenProvider<C : Route> {
    fun configFor(route: C): ScreenConfig

    @Composable
    fun Content(route: C, navigator: Navigator, toolbarState: ToolbarState)
}
```

### Navigator

```kotlin
// Thin wrapper over Decompose's StackNavigation
// Exposed to screens via LocalNavigator
interface Navigator {
    fun push(route: Route)
    fun pop()
    fun popWhile(predicate: (Route) -> Boolean)  // matches Decompose API
    fun setRoot(route: Route)
    fun replaceCurrent(route: Route)  // replaces top of stack only
    val canGoBack: StateFlow<Boolean>
    val currentRoute: StateFlow<Route>  // non-nullable: Decompose stack always has ≥1 item
}

val LocalNavigator: ProvidableCompositionLocal<Navigator>
```

**Note:** `popTo(route)` was removed — Decompose has `popWhile(predicate)` which is unambiguous. `replace(from, to)` was replaced with `replaceCurrent(route)` matching Decompose's `replaceCurrent` semantics (replaces stack top only).

### Entry Point

```kotlin
@Composable
expect fun <C : Route> NativeNavHost(
    rootComponent: NativeRootComponent<C>,
    screenProvider: ScreenProvider<C>,
    modifier: Modifier = Modifier,
)
```

### RootComponent (Decompose)

The SDK provides a base `RootComponent` or the app defines its own using Decompose's `childStack`:

```kotlin
// Option A: SDK provides generic RootComponent
class NativeRootComponent<C : Route>(
    componentContext: ComponentContext,
    initialRoute: C,
    private val serializer: KSerializer<C>?,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<C>()

    val childStack: Value<ChildStack<C, ComponentContext>> = childStack(
        source = navigation,
        serializer = serializer,
        initialConfiguration = initialRoute,
        handleBackButton = true,
        childFactory = { _, childContext -> childContext },
    )

    val navigator: Navigator = NavigatorImpl(navigation, childStack)
}
```

## iOS Implementation

### UINavigationController Integration

```
NativeNavHost (UIKitViewController) {
    UINavigationController
    ├── ComposeUIViewController[0]  ← startRoute
    ├── ComposeUIViewController[1]  ← pushed route
    └── ComposeUIViewController[N]  ← current active
}
```

### Key Behaviors

1. **Stack observation**: `childStack.subscribe()` — when Decompose stack changes, map to UINavigationController push/pop
2. **Native nav bar**: Each ComposeUIViewController gets `navigationItem.title`, `largeTitleDisplayMode`, `rightBarButtonItems` from ScreenConfig
3. **Swipe-back sync**: `UINavigationControllerDelegate.didShow()` detects when user completes swipe-back → sync Decompose stack via `navigation.pop()`
4. **Gesture cancellation**: If swipe-back is cancelled (finger returns), no sync needed — UINavigationController didn't change
5. **Toolbar updates**: Observe `ToolbarState.actions` changes → update `navigationItem.rightBarButtonItems`

### IosPlatformConfig Extension

```kotlin
class IosPlatformConfig.Builder {
    // ... existing factories (sheet, buttons)
    var navigationBarAppearance: NavigationBarAppearance? = null
}

data class NavigationBarAppearance(
    val tintColor: Long = 0,              // UIBarButtonItem tint (ARGB)
    val titleColor: Long = 0,             // title text color (ARGB)
    val backgroundColor: Long = 0,         // nav bar background (ARGB)
    val isTranslucent: Boolean = true,     // blur effect behind bar
    val showsSeparator: Boolean = true,    // bottom hairline separator
    val largeTitleFontName: String? = null, // custom font for large title
    val titleFontName: String? = null,     // custom font for small title
)
```

### Bidirectional Sync Algorithm

**Critical: Re-entrancy guard required.** When iOS triggers Decompose pop, the stack subscription fires back. Without a guard, this creates an infinite loop.

```
// State
var isSyncingFromNative = false  // re-entrancy guard

DECOMPOSE → iOS:
  childStack.subscribe { newStack ->
    if (isSyncingFromNative) return  // ← GUARD: skip if sync came from native

    let currentVCs = navController.viewControllers
    let newCount = newStack.items.count

    if newCount > currentVCs.count → push new VC (animated: true)
    if newCount < currentVCs.count → pop to index (animated: true)
    if newCount == currentVCs.count && top changed → replace top VC (animated: false)
  }

iOS → DECOMPOSE:
  navigationController(didShow:animated:) {
    let vcCount = navController.viewControllers.count
    let stackCount = currentDecomposeStack.count

    if vcCount < stackCount {
      // User swiped back — sync Decompose
      isSyncingFromNative = true
      navigation.pop()  // Decompose dispatches synchronously by default
      isSyncingFromNative = false
    }
  }
```

**Threading assumption:** Decompose's `StackNavigation.pop()` dispatches synchronously on the calling thread. Since `didShow` is called on the main thread and Decompose's subscription also runs on the main thread, the guard flag works correctly. If Decompose ever dispatches async, this must be replaced with a `Mutex` or similar mechanism.

**Gesture cancellation:** If the user starts swipe-back but cancels (finger returns), `didShow` is NOT called — UINavigationController did not change its stack. No sync needed.

## Android Implementation

### Compose-Based Rendering

**Key fix:** TopAppBar is rendered INSIDE `Children` block so each screen animates with its own bar config. This prevents visual flicker when navigating between screens with different bar configs.

**Back handling:** Decompose's `handleBackButton = true` handles back presses. No separate `BackHandler` needed (avoids double-handling).

```kotlin
@Composable
actual fun <C : Route> NativeNavHost(
    rootComponent: NativeRootComponent<C>,
    screenProvider: ScreenProvider<C>,
    modifier: Modifier,
) {
    val childStack by rootComponent.childStack.subscribeAsState()

    Children(
        stack = childStack,
        modifier = modifier,
        animation = stackAnimation(slide()),
    ) { child ->
        val route = child.configuration
        val config = screenProvider.configFor(route)
        val toolbarState = remember(route) { ToolbarState() }
        val canGoBack by rootComponent.navigator.canGoBack.collectAsState()

        Scaffold(
            topBar = {
                if (config.showsNavigationBar) {
                    val title = config.title?.let { stringResource(it) } ?: ""
                    if (config.largeTitleMode == LargeTitleMode.Always) {
                        LargeTopAppBar(
                            title = { Text(title) },
                            navigationIcon = {
                                if (canGoBack) BackButton(rootComponent.navigator)
                            },
                            actions = { ToolbarActions(toolbarState) },
                        )
                    } else {
                        TopAppBar(
                            title = { Text(title) },
                            navigationIcon = {
                                if (canGoBack) BackButton(rootComponent.navigator)
                            },
                            actions = { ToolbarActions(toolbarState) },
                        )
                    }
                }
            },
        ) { padding ->
            CompositionLocalProvider(LocalNavigator provides rootComponent.navigator) {
                Box(Modifier.padding(padding)) {
                    screenProvider.Content(route, rootComponent.navigator, toolbarState)
                }
            }
        }
    }
    // No BackHandler needed — Decompose handles back via handleBackButton = true
}
```

## App Usage Example

### Route Definition (commonMain)

```kotlin
@Serializable
sealed class AppRoute : Route {
    @Serializable data object Home : AppRoute()
    @Serializable data object Menu : AppRoute()
    @Serializable data class OrderHistory(val orderId: Int) : AppRoute()
    @Serializable data object Settings : AppRoute()
    // ... 15+ routes
}
```

### Screen Provider (commonMain)

```kotlin
class AppScreenProvider(
    private val strings: AppStrings,  // resolved strings (from Koin or manual DI)
) : ScreenProvider<AppRoute> {
    override fun configFor(route: AppRoute) = when (route) {  // type-safe, no cast needed!
        AppRoute.Home -> ScreenConfig(showsNavigationBar = false)
        AppRoute.Menu -> ScreenConfig(
            title = strings.menu,
            largeTitleMode = LargeTitleMode.Always,
        )
        is AppRoute.OrderHistory -> ScreenConfig(
            title = strings.orderHistory,
        )
        AppRoute.Settings -> ScreenConfig(
            title = strings.settings,
            largeTitleMode = LargeTitleMode.Always,
        )
    }

    @Composable
    override fun Content(route: AppRoute, navigator: Navigator, toolbarState: ToolbarState) {
        when (route) {  // type-safe, no cast needed!
            AppRoute.Home -> HomeScreen()
            AppRoute.Menu -> MenuScreen(toolbarState)
            is AppRoute.OrderHistory -> OrderHistoryScreen(route.orderId)
            AppRoute.Settings -> SettingsScreen()
        }
    }
}
```

### App Entry Point (commonMain)

```kotlin
@Composable
fun App(rootComponent: NativeRootComponent<AppRoute>) {
    YallaTheme {
        NativeNavHost(
            rootComponent = rootComponent,
            screenProvider = AppScreenProvider(),
        )
    }
}
```

### iOS Entry (Swift — minimal)

```swift
@main
struct iOSApp: App {
    init() {
        YallaPlatform.shared.install(config: IosPlatformConfig.Builder()
            .sheetPresenter(MySheetPresenter())
            .circleButton(MyCircleButtonFactory())
            .squircleButton(MySquircleButtonFactory())
            .build()
        )
    }

    var body: some Scene {
        WindowGroup {
            ComposeView(rootComponent: createRootComponent())
        }
    }
}
```

### Android Entry (Kotlin — minimal)

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = NativeRootComponent<AppRoute>(
            componentContext = defaultComponentContext(),
            initialRoute = AppRoute.Home,
            serializer = AppRoute.serializer(),
        )
        setContent { App(root) }
    }
}
```

## Migration Plan (YallaClient)

### Files to DELETE from iosApp:
- `AppNavigator.swift` (479 lines) → replaced by NativeNavHost
- `ScreenConfig.swift` (60 lines) → replaced by ScreenConfig.kt in commonMain
- `Route.swift` (50 lines) → replaced by AppRoute sealed class
- `NavigatorView.swift` (40 lines) → replaced by NativeNavHost
- `ScreenViewController` inner class → replaced by NativeNavHost's VC management

### Files to MOVE to commonMain:
- Route definitions → `AppRoute.kt` sealed class
- Screen configs → `AppScreenProvider.configFor()`
- Screen content → `AppScreenProvider.Content()`

### Net result:
- **Before**: 630+ lines Swift navigation code + Kotlin ScreenFactory
- **After**: ~0 lines Swift navigation code. Everything in commonMain Kotlin.

## Design Decisions Log

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Navigation logic | Decompose | Battle-tested, lifecycle-aware, state preservation |
| iOS nav rendering | UINavigationController | Native transitions, swipe-back, nav bar |
| Android nav rendering | Compose + Material3 TopAppBar | Platform-idiomatic |
| Route type safety | Sealed class | Compile-time checked, IDE autocomplete |
| Large title | Static (no scroll collapse) | Compose LazyColumn ↔ UINavigationBar scroll interop unsolved industry-wide |
| Toolbar | Dynamic ToolbarState | Screens update actions at runtime |
| Nav bar rendering | Platform (not Compose) | iOS: UINavigationBar. Android: Material3 TopAppBar |
| cupertino-decompose | NOT used (reference only) | Alpha, memory leak, unmaintained 1+ year |
| compose-cupertino widgets | NOT used | We use real native components, not imitations |

## Top Bar Behavior (Important)

**iOS:** `UINavigationBar` with `prefersLargeTitles = true`. Large title mode is **static** — screens with `LargeTitleMode.Always` show large title permanently, screens with `LargeTitleMode.Never` show small title. No scroll-driven collapse. This is a conscious trade-off: Compose's scroll system does not interoperate with UINavigationBar's scroll tracking.

**Android:** `Material3 TopAppBar`. `LargeTitleMode.Always` maps to `LargeTopAppBar`, `LargeTitleMode.Never` maps to `TopAppBar`. Android's `TopAppBarScrollBehavior` CAN collapse on scroll since it's all Compose.

## Testing Strategy

- **Navigation logic**: Unit test Decompose components (push/pop/replace) — commonTest
- **Bidirectional sync**: iOS integration test — verify swipe-back syncs Decompose stack
- **ScreenConfig mapping**: Unit test configFor() — commonTest
- **ToolbarState**: Unit test action updates — commonTest
- **Android UI**: compose-ui-test for TopAppBar rendering
- **iOS UI**: Manual testing for native nav bar, transitions, swipe-back feel

## Future Extensions (not in scope)

- Tab navigation (UITabBarController + nested UINavigationController per tab)
- Navigation in sheets (nested Decompose ChildStack inside sheet)
- Deep linking (setRoot with pre-built stack)
- Shared element transitions
- Custom transition animations
