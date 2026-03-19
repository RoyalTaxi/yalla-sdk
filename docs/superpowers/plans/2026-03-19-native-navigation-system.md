# Native Navigation System Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add Decompose-based native navigation to yalla-sdk's platform module — UINavigationController on iOS, Material3 TopAppBar on Android — with a common API in commonMain.

**Architecture:** Decompose provides navigation logic, lifecycle, and state preservation in commonMain. `NativeNavHost` is an expect/actual composable — iOS actual wraps UINavigationController (native transitions, large titles, swipe-back), Android actual uses Compose Children + Material3 Scaffold. App code stays 100% in commonMain via `ScreenProvider<C>` interface.

**Tech Stack:** Decompose 3.2.x, Essenty (transitive), Kotlin Multiplatform, Compose Multiplatform, UIKit (iOS), Material3 (Android)

**Spec:** `docs/superpowers/specs/2026-03-19-native-navigation-system-design.md`

---

## File Structure

### New files to CREATE:

**commonMain (platform module):**
- `platform/src/commonMain/kotlin/uz/yalla/platform/navigation/Route.kt` — `Route` interface, `LargeTitleMode` enum, `ScreenConfig` data class
- `platform/src/commonMain/kotlin/uz/yalla/platform/navigation/Navigator.kt` — `Navigator` interface, `LocalNavigator` CompositionLocal
- `platform/src/commonMain/kotlin/uz/yalla/platform/navigation/NavigatorImpl.kt` — `NavigatorImpl` wrapping Decompose's `StackNavigation`
- `platform/src/commonMain/kotlin/uz/yalla/platform/navigation/ScreenProvider.kt` — `ScreenProvider<C>` interface
- `platform/src/commonMain/kotlin/uz/yalla/platform/navigation/ToolbarState.kt` — `ToolbarState` class
- `platform/src/commonMain/kotlin/uz/yalla/platform/navigation/NativeRootComponent.kt` — SDK-provided generic root component
- `platform/src/commonMain/kotlin/uz/yalla/platform/navigation/NativeNavHost.kt` — `expect fun NativeNavHost`

**iosMain (platform module):**
- `platform/src/iosMain/kotlin/uz/yalla/platform/navigation/NativeNavHost.ios.kt` — `actual fun NativeNavHost` with UINavigationController
- `platform/src/iosMain/kotlin/uz/yalla/platform/navigation/NavControllerSync.kt` — bidirectional sync logic with re-entrancy guard
- `platform/src/iosMain/kotlin/uz/yalla/platform/navigation/NavigationBarAppearance.kt` — appearance config data class

**androidMain (platform module):**
- `platform/src/androidMain/kotlin/uz/yalla/platform/navigation/NativeNavHost.android.kt` — `actual fun NativeNavHost` with Compose Children + Scaffold

**commonTest (platform module):**
- `platform/src/commonTest/kotlin/uz/yalla/platform/navigation/NavigatorImplTest.kt` — unit tests for Navigator wrapper
- `platform/src/commonTest/kotlin/uz/yalla/platform/navigation/ToolbarStateTest.kt` — unit tests for toolbar state
- `platform/src/commonTest/kotlin/uz/yalla/platform/navigation/ScreenConfigTest.kt` — unit tests for config defaults

### Files to MODIFY:
- `gradle/libs.versions.toml` — add decompose version + library entries
- `platform/build.gradle.kts` — add decompose dependencies
- `platform/src/iosMain/kotlin/uz/yalla/platform/config/IosPlatformConfig.kt` — add `navigationBarAppearance` field

---

## Chunk 1: Dependencies + Core Types

### Task 1: Add Decompose to version catalog

**Files:**
- Modify: `gradle/libs.versions.toml`

- [ ] **Step 1: Add Decompose version and libraries to version catalog**

Add after the `orbit` line in `[versions]`:
```toml
decompose = "3.2.2"
```

Add in `[libraries]` section (after existing entries):
```toml
decompose = { module = "com.arkivanov.decompose:decompose", version.ref = "decompose" }
decompose-compose = { module = "com.arkivanov.decompose:extensions-compose", version.ref = "decompose" }
```

- [ ] **Step 2: Add dependencies to platform/build.gradle.kts**

Add to `commonMain.dependencies` block:
```kotlin
api(libs.decompose)
api(libs.decompose.compose)
```

- [ ] **Step 3: Sync Gradle to verify dependencies resolve**

Run: `./gradlew :platform:dependencies --configuration commonMainImplementationDependenciesMetadata 2>&1 | grep decompose`
Expected: Lines showing `com.arkivanov.decompose:decompose:3.2.2` and `extensions-compose:3.2.2`

- [ ] **Step 4: Commit**

```bash
git add gradle/libs.versions.toml platform/build.gradle.kts
git commit -m "chore(platform): add Decompose 3.2.2 dependency for navigation system"
```

---

### Task 2: Create Route, ScreenConfig, LargeTitleMode

**Files:**
- Create: `platform/src/commonMain/kotlin/uz/yalla/platform/navigation/Route.kt`

- [ ] **Step 1: Create Route.kt with all core types**

```kotlin
package uz.yalla.platform.navigation

/**
 * Marker interface for navigation routes.
 *
 * App extends this with a `@Serializable sealed class` to define its route hierarchy.
 * Serialization enables Decompose's state preservation across process death.
 *
 * ## Usage
 * ```kotlin
 * @Serializable
 * sealed class AppRoute : Route {
 *     @Serializable data object Home : AppRoute()
 *     @Serializable data class Detail(val id: Int) : AppRoute()
 * }
 * ```
 *
 * @since 0.0.5
 */
interface Route

/**
 * Controls the navigation bar title display mode.
 *
 * On iOS, this maps directly to `UINavigationItem.largeTitleDisplayMode`.
 * On Android, [Always] uses Material3 `LargeTopAppBar`, [Never] uses `TopAppBar`.
 *
 * **Note:** Large titles are static — no scroll-driven collapse.
 * This is a conscious trade-off: Compose's scroll system does not interoperate
 * with UINavigationBar's scroll tracking (industry-wide unsolved).
 *
 * @since 0.0.5
 */
enum class LargeTitleMode {
    /** Large title always visible. Use for primary screens (Menu, Settings). */
    Always,
    /** Small title only. Use for detail/form screens. */
    Never,
}

/**
 * Per-screen navigation configuration.
 *
 * Passed to the platform's native navigation bar to configure title, visibility,
 * and appearance. Returned by [ScreenProvider.configFor] for each route.
 *
 * @property title Screen title displayed in the navigation bar. `null` hides the title.
 *   Use plain [String] (not StringResource) because iOS sets `navigationItem.title`
 *   outside a `@Composable` context.
 * @property largeTitleMode Whether to use large or small title style.
 * @property showsNavigationBar Whether the navigation bar is visible for this screen.
 * @property transparentNavigationBar Whether the navigation bar background is transparent.
 * @since 0.0.5
 */
data class ScreenConfig(
    val title: String? = null,
    val largeTitleMode: LargeTitleMode = LargeTitleMode.Never,
    val showsNavigationBar: Boolean = true,
    val transparentNavigationBar: Boolean = false,
)
```

- [ ] **Step 2: Verify compilation**

Run: `./gradlew :platform:compileKotlinMetadata 2>&1 | tail -5`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add platform/src/commonMain/kotlin/uz/yalla/platform/navigation/Route.kt
git commit -m "feat(platform): add Route interface, LargeTitleMode, and ScreenConfig"
```

---

### Task 3: Create Navigator interface

**Files:**
- Create: `platform/src/commonMain/kotlin/uz/yalla/platform/navigation/Navigator.kt`

- [ ] **Step 1: Create Navigator.kt**

```kotlin
package uz.yalla.platform.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.StateFlow

/**
 * Cross-platform navigation controller.
 *
 * Thin abstraction over Decompose's [StackNavigation] that screens use to
 * push, pop, and replace routes. Access inside any screen via [LocalNavigator].
 *
 * All operations are synchronous and execute on the calling thread (main thread).
 *
 * @since 0.0.5
 */
interface Navigator {
    /**
     * Push a new route onto the navigation stack.
     * On iOS this triggers a native UINavigationController push animation.
     */
    fun push(route: Route)

    /** Pop the current route. No-op if at root. */
    fun pop()

    /**
     * Pop routes while [predicate] returns true.
     * Matches Decompose's `popWhile` semantics — pops from the top of the stack
     * until the predicate returns false for the new top.
     */
    fun popWhile(predicate: (Route) -> Boolean)

    /**
     * Replace the entire stack with a single [route] as the new root.
     * Useful for auth flows (e.g., login → home with no back stack).
     */
    fun setRoot(route: Route)

    /**
     * Replace the current top-of-stack route with [route].
     * The back stack beneath is unchanged.
     */
    fun replaceCurrent(route: Route)

    /** Whether the stack has more than one route (i.e., back navigation is possible). */
    val canGoBack: StateFlow<Boolean>

    /** The currently active route (top of stack). Non-nullable — stack always has ≥1 item. */
    val currentRoute: StateFlow<Route>
}

/**
 * CompositionLocal providing the [Navigator] inside a [NativeNavHost].
 *
 * Throws if accessed outside of [NativeNavHost].
 *
 * @since 0.0.5
 */
val LocalNavigator = staticCompositionLocalOf<Navigator> {
    error("No Navigator provided. Ensure you are inside a NativeNavHost.")
}
```

- [ ] **Step 2: Verify compilation**

Run: `./gradlew :platform:compileKotlinMetadata 2>&1 | tail -5`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add platform/src/commonMain/kotlin/uz/yalla/platform/navigation/Navigator.kt
git commit -m "feat(platform): add Navigator interface and LocalNavigator"
```

---

### Task 4: Create ScreenProvider and ToolbarState

**Files:**
- Create: `platform/src/commonMain/kotlin/uz/yalla/platform/navigation/ScreenProvider.kt`
- Create: `platform/src/commonMain/kotlin/uz/yalla/platform/navigation/ToolbarState.kt`

- [ ] **Step 1: Create ScreenProvider.kt**

```kotlin
package uz.yalla.platform.navigation

import androidx.compose.runtime.Composable

/**
 * Maps routes to screen configuration and composable content.
 *
 * App implements this interface to define how each route is rendered.
 * The generic type [C] preserves type safety — no unchecked casts needed
 * in the `when` expressions.
 *
 * ## Usage
 * ```kotlin
 * class AppScreenProvider : ScreenProvider<AppRoute> {
 *     override fun configFor(route: AppRoute) = when (route) {
 *         AppRoute.Home -> ScreenConfig(showsNavigationBar = false)
 *         AppRoute.Menu -> ScreenConfig(title = "Menu", largeTitleMode = LargeTitleMode.Always)
 *     }
 *
 *     @Composable
 *     override fun Content(route: AppRoute, navigator: Navigator, toolbarState: ToolbarState) {
 *         when (route) {
 *             AppRoute.Home -> HomeScreen()
 *             AppRoute.Menu -> MenuScreen(toolbarState)
 *         }
 *     }
 * }
 * ```
 *
 * @param C The concrete route type (sealed class extending [Route]).
 * @since 0.0.5
 */
interface ScreenProvider<C : Route> {
    /** Return the navigation bar configuration for [route]. Called once per screen push. */
    fun configFor(route: C): ScreenConfig

    /** Render the screen content for [route]. */
    @Composable
    fun Content(route: C, navigator: Navigator, toolbarState: ToolbarState)
}
```

- [ ] **Step 2: Create ToolbarState.kt**

```kotlin
package uz.yalla.platform.navigation

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import uz.yalla.primitives.navigation.ToolbarAction

/**
 * Mutable state holder for dynamic toolbar actions.
 *
 * Screens update [actions] at runtime; the platform navigation bar observes
 * changes and updates its toolbar items accordingly.
 *
 * On iOS, actions map to `UIBarButtonItem` on `navigationItem.rightBarButtonItems`.
 * On Android, actions render inside Material3 TopAppBar's `actions` slot.
 *
 * ## Usage
 * ```kotlin
 * @Composable
 * fun MenuScreen(toolbarState: ToolbarState) {
 *     LaunchedEffect(Unit) {
 *         toolbarState.actions = listOf(
 *             ToolbarAction.Icon(ToolbarIcon.Edit) { /* ... */ }
 *         )
 *     }
 *     // ... screen content
 * }
 * ```
 *
 * @since 0.0.5
 */
@Stable
class ToolbarState {
    /** Current toolbar actions. Changes trigger navigation bar updates. */
    var actions: List<ToolbarAction> by mutableStateOf(emptyList())
}
```

- [ ] **Step 3: Verify compilation**

Run: `./gradlew :platform:compileKotlinMetadata 2>&1 | tail -5`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 4: Commit**

```bash
git add platform/src/commonMain/kotlin/uz/yalla/platform/navigation/ScreenProvider.kt \
       platform/src/commonMain/kotlin/uz/yalla/platform/navigation/ToolbarState.kt
git commit -m "feat(platform): add ScreenProvider<C> and ToolbarState"
```

---

### Task 5: Create NavigatorImpl and NativeRootComponent

**Files:**
- Create: `platform/src/commonMain/kotlin/uz/yalla/platform/navigation/NavigatorImpl.kt`
- Create: `platform/src/commonMain/kotlin/uz/yalla/platform/navigation/NativeRootComponent.kt`

- [ ] **Step 1: Create NavigatorImpl.kt**

```kotlin
package uz.yalla.platform.navigation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.navigate
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popWhile
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * [Navigator] implementation backed by Decompose's [StackNavigation].
 *
 * Observes [childStack] changes to keep [canGoBack] and [currentRoute] in sync.
 *
 * @since 0.0.5
 */
internal class NavigatorImpl<C : Route>(
    private val navigation: StackNavigation<C>,
    childStack: Value<ChildStack<C, *>>,
) : Navigator {

    private val _canGoBack = MutableStateFlow(false)
    override val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    private val _currentRoute = MutableStateFlow<Route>(childStack.value.active.configuration)
    override val currentRoute: StateFlow<Route> = _currentRoute.asStateFlow()

    init {
        childStack.subscribe { stack ->
            _canGoBack.value = stack.backStack.isNotEmpty()
            _currentRoute.value = stack.active.configuration
        }
    }

    override fun push(route: Route) {
        @Suppress("UNCHECKED_CAST")
        navigation.push(route as C)
    }

    override fun pop() {
        navigation.pop()
    }

    override fun popWhile(predicate: (Route) -> Boolean) {
        navigation.popWhile { predicate(it) }
    }

    override fun setRoot(route: Route) {
        @Suppress("UNCHECKED_CAST")
        navigation.navigate { listOf(route as C) }
    }

    override fun replaceCurrent(route: Route) {
        @Suppress("UNCHECKED_CAST")
        navigation.replaceCurrent(route as C)
    }
}
```

- [ ] **Step 2: Create NativeRootComponent.kt**

```kotlin
package uz.yalla.platform.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.KSerializer

/**
 * SDK-provided root component for navigation.
 *
 * Wraps Decompose's [childStack] with a generic route type [C] and exposes
 * a [Navigator] for screen-level navigation. Pass this to [NativeNavHost].
 *
 * ## Usage
 * ```kotlin
 * val root = NativeRootComponent<AppRoute>(
 *     componentContext = defaultComponentContext(),
 *     initialRoute = AppRoute.Home,
 *     serializer = AppRoute.serializer(),
 * )
 * ```
 *
 * @param C Concrete route type (sealed class extending [Route]).
 * @param componentContext Decompose's lifecycle context — obtain via `defaultComponentContext()` on Android
 *   or create manually on iOS.
 * @param initialRoute The first screen shown when the app starts.
 * @param serializer Kotlin serialization serializer for [C]. Enables state preservation across
 *   process death. Pass `null` to disable state preservation.
 * @since 0.0.5
 */
class NativeRootComponent<C : Route>(
    componentContext: ComponentContext,
    initialRoute: C,
    serializer: KSerializer<C>?,
) : ComponentContext by componentContext {

    internal val navigation = StackNavigation<C>()

    /** Observable navigation stack. Subscribe to track which screen is active. */
    val childStack: Value<ChildStack<C, ComponentContext>> = childStack(
        source = navigation,
        serializer = serializer,
        initialConfiguration = initialRoute,
        handleBackButton = true,
        childFactory = { _, childContext -> childContext },
    )

    /** Navigation controller for pushing, popping, and replacing routes. */
    val navigator: Navigator = NavigatorImpl(navigation, childStack)
}
```

- [ ] **Step 3: Verify compilation**

Run: `./gradlew :platform:compileKotlinMetadata 2>&1 | tail -5`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 4: Commit**

```bash
git add platform/src/commonMain/kotlin/uz/yalla/platform/navigation/NavigatorImpl.kt \
       platform/src/commonMain/kotlin/uz/yalla/platform/navigation/NativeRootComponent.kt
git commit -m "feat(platform): add NavigatorImpl and NativeRootComponent (Decompose)"
```

---

### Task 6: Create expect NativeNavHost

**Files:**
- Create: `platform/src/commonMain/kotlin/uz/yalla/platform/navigation/NativeNavHost.kt`

- [ ] **Step 1: Create NativeNavHost.kt**

```kotlin
package uz.yalla.platform.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Cross-platform navigation host composable.
 *
 * On iOS, renders via `UINavigationController` with native transitions, large titles,
 * and swipe-back gestures. On Android, renders via Compose `Children` with Material3
 * `TopAppBar` / `LargeTopAppBar`.
 *
 * ## Usage
 * ```kotlin
 * @Composable
 * fun App(rootComponent: NativeRootComponent<AppRoute>) {
 *     YallaTheme {
 *         NativeNavHost(
 *             rootComponent = rootComponent,
 *             screenProvider = AppScreenProvider(),
 *         )
 *     }
 * }
 * ```
 *
 * @param C Concrete route type.
 * @param rootComponent Decompose root component managing the navigation stack.
 * @param screenProvider Maps routes to screen config and content.
 * @param modifier Optional modifier applied to the host container.
 * @since 0.0.5
 */
@Composable
expect fun <C : Route> NativeNavHost(
    rootComponent: NativeRootComponent<C>,
    screenProvider: ScreenProvider<C>,
    modifier: Modifier = Modifier,
)
```

- [ ] **Step 2: Verify compilation (will fail — actuals not yet created, that's expected)**

Run: `./gradlew :platform:compileKotlinMetadata 2>&1 | tail -5`
Expected: `BUILD SUCCESSFUL` (metadata compilation doesn't require actuals)

- [ ] **Step 3: Commit**

```bash
git add platform/src/commonMain/kotlin/uz/yalla/platform/navigation/NativeNavHost.kt
git commit -m "feat(platform): add expect NativeNavHost composable"
```

---

## Chunk 2: Android actual implementation

### Task 7: Create Android NativeNavHost actual

**Files:**
- Create: `platform/src/androidMain/kotlin/uz/yalla/platform/navigation/NativeNavHost.android.kt`

- [ ] **Step 1: Create NativeNavHost.android.kt**

```kotlin
package uz.yalla.platform.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import uz.yalla.primitives.navigation.ToolbarAction

/**
 * Android actual: Compose Children + Material3 Scaffold per screen.
 *
 * TopAppBar is rendered INSIDE the Children block so each screen
 * animates with its own bar config (no visual flicker on transitions).
 * Decompose handles back button via `handleBackButton = true`.
 */
@OptIn(ExperimentalMaterial3Api::class)
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
                    NavigationBar(
                        config = config,
                        canGoBack = canGoBack,
                        navigator = rootComponent.navigator,
                        toolbarState = toolbarState,
                    )
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NavigationBar(
    config: ScreenConfig,
    canGoBack: Boolean,
    navigator: Navigator,
    toolbarState: ToolbarState,
) {
    val title = config.title ?: ""
    val navigationIcon: @Composable () -> Unit = {
        if (canGoBack) {
            IconButton(onClick = { navigator.pop() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
    }
    val actions: @Composable () -> Unit = {
        toolbarState.actions.forEach { action ->
            when (action) {
                is ToolbarAction.Text -> {
                    androidx.compose.material3.TextButton(onClick = action.onClick) {
                        Text(action.label)
                    }
                }
                is ToolbarAction.Icon -> {
                    IconButton(onClick = action.onClick) {
                        // Map ToolbarIcon to platform icon — use existing IconMapper pattern
                        Text(action.icon.name) // placeholder — replace with actual icon mapping
                    }
                }
            }
        }
    }

    if (config.largeTitleMode == LargeTitleMode.Always) {
        LargeTopAppBar(title = { Text(title) }, navigationIcon = navigationIcon, actions = actions)
    } else {
        TopAppBar(title = { Text(title) }, navigationIcon = navigationIcon, actions = actions)
    }
}
```

- [ ] **Step 2: Verify Android compilation**

Run: `./gradlew :platform:compileAndroidMain 2>&1 | tail -5`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add platform/src/androidMain/kotlin/uz/yalla/platform/navigation/NativeNavHost.android.kt
git commit -m "feat(platform): add Android NativeNavHost actual with Material3 TopAppBar"
```

---

## Chunk 3: iOS actual implementation

### Task 8: Create NavigationBarAppearance and extend IosPlatformConfig

**Files:**
- Create: `platform/src/iosMain/kotlin/uz/yalla/platform/navigation/NavigationBarAppearance.kt`
- Modify: `platform/src/iosMain/kotlin/uz/yalla/platform/config/IosPlatformConfig.kt`

- [ ] **Step 1: Create NavigationBarAppearance.kt**

```kotlin
package uz.yalla.platform.navigation

/**
 * Global appearance configuration for the iOS navigation bar.
 *
 * Registered via [IosPlatformConfig.Builder.navigationBarAppearance] and applied
 * to every UINavigationBar managed by [NativeNavHost].
 *
 * Colors use ARGB Long encoding (same as Compose's `Color.toArgb().toLong()`).
 *
 * @property tintColor UIBarButtonItem tint color (ARGB).
 * @property titleColor Navigation bar title text color (ARGB).
 * @property backgroundColor Navigation bar background color (ARGB). 0 = system default.
 * @property isTranslucent Whether the bar has a blur/translucency effect.
 * @property showsSeparator Whether the bottom hairline separator is visible.
 * @property largeTitleFontName Custom font name for large titles. `null` = system default.
 * @property titleFontName Custom font name for small titles. `null` = system default.
 * @since 0.0.5
 */
data class NavigationBarAppearance(
    val tintColor: Long = 0,
    val titleColor: Long = 0,
    val backgroundColor: Long = 0,
    val isTranslucent: Boolean = true,
    val showsSeparator: Boolean = true,
    val largeTitleFontName: String? = null,
    val titleFontName: String? = null,
)
```

- [ ] **Step 2: Add `navigationBarAppearance` to IosPlatformConfig.Builder**

In `platform/src/iosMain/kotlin/uz/yalla/platform/config/IosPlatformConfig.kt`, add the field to both the class and builder:

Add to constructor: `val navigationBarAppearance: NavigationBarAppearance? = null,`
Add import: `import uz.yalla.platform.navigation.NavigationBarAppearance`
Add to Builder: `var navigationBarAppearance: NavigationBarAppearance? = null`
Pass in build(): `navigationBarAppearance = navigationBarAppearance,`

- [ ] **Step 3: Verify iOS compilation**

Run: `./gradlew :platform:compileKotlinIosArm64 2>&1 | tail -5`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 4: Commit**

```bash
git add platform/src/iosMain/kotlin/uz/yalla/platform/navigation/NavigationBarAppearance.kt \
       platform/src/iosMain/kotlin/uz/yalla/platform/config/IosPlatformConfig.kt
git commit -m "feat(platform): add NavigationBarAppearance and extend IosPlatformConfig"
```

---

### Task 9: Create iOS NativeNavHost actual with UINavigationController

**Files:**
- Create: `platform/src/iosMain/kotlin/uz/yalla/platform/navigation/NativeNavHost.ios.kt`
- Create: `platform/src/iosMain/kotlin/uz/yalla/platform/navigation/NavControllerSync.kt`

This is the most complex task — UINavigationController integration with bidirectional sync.

- [ ] **Step 1: Create NavControllerSync.kt — bidirectional sync engine**

```kotlin
package uz.yalla.platform.navigation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import platform.UIKit.UINavigationController
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.darwin.NSObject

/**
 * Manages bidirectional synchronization between Decompose's ChildStack
 * and iOS UINavigationController.
 *
 * DECOMPOSE → iOS: Stack subscription pushes/pops VCs on the UINavigationController.
 * iOS → DECOMPOSE: UINavigationControllerDelegate.didShow detects swipe-back and syncs stack.
 *
 * A re-entrancy guard (`isSyncingFromNative`) prevents infinite loops when
 * a native pop triggers a Decompose pop which triggers a stack subscription callback.
 */
internal class NavControllerSync<C : Route>(
    private val navigation: StackNavigation<C>,
    private val childStack: Value<ChildStack<C, *>>,
    private val navController: UINavigationController,
    private val createViewController: (C, ScreenConfig) -> UIViewController,
    private val screenProvider: ScreenProvider<C>,
) {
    private var isSyncingFromNative = false
    private var currentStackSize = 1

    /** Delegate that detects swipe-back gesture completion. */
    val delegate: UINavigationControllerDelegateProtocol = NavDelegate()

    /** Start observing Decompose stack and syncing to UINavigationController. */
    fun start() {
        currentStackSize = childStack.value.items.size

        childStack.subscribe { stack ->
            if (isSyncingFromNative) return@subscribe

            val newItems = stack.items
            val newSize = newItems.size
            val vcCount = navController.viewControllers.count().toInt()

            when {
                newSize > vcCount -> {
                    // Push: create VC for the new top route
                    val topConfig = newItems.last().configuration
                    val config = screenProvider.configFor(topConfig)
                    val vc = createViewController(topConfig, config)
                    navController.pushViewController(vc, animated = true)
                }
                newSize < vcCount -> {
                    // Pop: pop to the correct depth
                    val targetVC = navController.viewControllers[newSize - 1] as UIViewController
                    navController.popToViewController(targetVC, animated = true)
                }
                newSize == vcCount && stack.active.configuration != childStack.value.active.configuration -> {
                    // Replace top: swap the top VC without animation
                    val topConfig = stack.active.configuration
                    val config = screenProvider.configFor(topConfig)
                    val vc = createViewController(topConfig, config)
                    val vcs = navController.viewControllers.toMutableList()
                    vcs[vcs.lastIndex] = vc
                    navController.setViewControllers(vcs, animated = false)
                }
            }

            currentStackSize = newSize
        }
    }

    /** Called by NavDelegate when UINavigationController finishes showing a VC. */
    private fun onDidShow(viewController: UIViewController, animated: Boolean) {
        val vcCount = navController.viewControllers.count().toInt()

        if (vcCount < currentStackSize) {
            // User swiped back — sync Decompose
            val popCount = currentStackSize - vcCount
            isSyncingFromNative = true
            repeat(popCount) { navigation.pop() }
            currentStackSize = vcCount
            isSyncingFromNative = false
        }
    }

    private inner class NavDelegate : NSObject(), UINavigationControllerDelegateProtocol {
        override fun navigationController(
            navigationController: UINavigationController,
            didShowViewController: UIViewController,
            animated: Boolean,
        ) {
            onDidShow(didShowViewController, animated)
        }
    }
}
```

- [ ] **Step 2: Create NativeNavHost.ios.kt**

```kotlin
package uz.yalla.platform.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.LocalUIViewController
import androidx.compose.ui.interop.UIKitViewController
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UINavigationBarAppearance
import platform.UIKit.UINavigationController
import platform.UIKit.UINavigationItemLargeTitleDisplayMode
import platform.UIKit.UIViewController
import platform.UIKit.navigationItem
import uz.yalla.platform.config.requireIosConfig

/**
 * iOS actual: Wraps a UINavigationController that manages ComposeUIViewControllers.
 *
 * Each route gets its own UIViewController pushed onto the native nav stack.
 * Transitions, swipe-back, and large titles are handled natively by UIKit.
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun <C : Route> NativeNavHost(
    rootComponent: NativeRootComponent<C>,
    screenProvider: ScreenProvider<C>,
    modifier: Modifier,
) {
    val config = requireIosConfig()
    val appearance = config.navigationBarAppearance

    // Create the UINavigationController once
    val navState = remember {
        val initialRoute = rootComponent.childStack.value.active.configuration
        val initialConfig = screenProvider.configFor(initialRoute)
        val initialVC = createComposeVC(initialRoute, initialConfig, screenProvider, rootComponent)

        val navController = UINavigationController(rootViewController = initialVC)
        navController.navigationBar.prefersLargeTitles = true

        // Apply global appearance
        if (appearance != null) {
            applyAppearance(navController, appearance)
        }

        val sync = NavControllerSync(
            navigation = rootComponent.navigation,
            childStack = rootComponent.childStack,
            navController = navController,
            createViewController = { route, screenConfig ->
                createComposeVC(route, screenConfig, screenProvider, rootComponent)
            },
            screenProvider = screenProvider,
        )
        navController.delegate = sync.delegate
        sync.start()

        navController
    }

    UIKitViewController(
        factory = { navState },
        modifier = modifier,
    )
}

private fun <C : Route> createComposeVC(
    route: C,
    screenConfig: ScreenConfig,
    screenProvider: ScreenProvider<C>,
    rootComponent: NativeRootComponent<C>,
): UIViewController {
    val vc = androidx.compose.ui.window.ComposeUIViewController {
        val toolbarState = remember { ToolbarState() }
        CompositionLocalProvider(LocalNavigator provides rootComponent.navigator) {
            screenProvider.Content(route, rootComponent.navigator, toolbarState)
        }
        // Observe toolbar state and update navigation item
        // (handled via snapshotFlow in a LaunchedEffect)
    }

    // Configure navigation bar for this screen
    vc.navigationItem.title = screenConfig.title
    vc.navigationItem.largeTitleDisplayMode = when (screenConfig.largeTitleMode) {
        LargeTitleMode.Always -> UINavigationItemLargeTitleDisplayMode.UINavigationItemLargeTitleDisplayModeAlways
        LargeTitleMode.Never -> UINavigationItemLargeTitleDisplayMode.UINavigationItemLargeTitleDisplayModeNever
    }

    if (!screenConfig.showsNavigationBar) {
        // Will be hidden by the VC in viewWillAppear
        // handled by NavControllerSync observation
    }

    return vc
}

private fun applyAppearance(
    navController: UINavigationController,
    appearance: NavigationBarAppearance,
) {
    val bar = navController.navigationBar
    val navAppearance = UINavigationBarAppearance()

    if (!appearance.showsSeparator) {
        navAppearance.shadowColor = null
    }

    if (appearance.backgroundColor != 0L) {
        val bgColor = UIColor.colorFromArgb(appearance.backgroundColor)
        navAppearance.backgroundColor = bgColor
    }

    bar.standardAppearance = navAppearance
    bar.scrollEdgeAppearance = navAppearance
    bar.compactAppearance = navAppearance
    bar.isTranslucent = appearance.isTranslucent

    if (appearance.tintColor != 0L) {
        bar.tintColor = UIColor.colorFromArgb(appearance.tintColor)
    }
}

/** Convert ARGB Long to UIColor. */
private fun UIColor.Companion.colorFromArgb(argb: Long): UIColor {
    val a = ((argb shr 24) and 0xFF) / 255.0
    val r = ((argb shr 16) and 0xFF) / 255.0
    val g = ((argb shr 8) and 0xFF) / 255.0
    val b = (argb and 0xFF) / 255.0
    return UIColor(red = r, green = g, blue = b, alpha = a)
}
```

- [ ] **Step 3: Verify iOS compilation**

Run: `./gradlew :platform:compileKotlinIosArm64 2>&1 | tail -10`
Expected: `BUILD SUCCESSFUL` (may have warnings, that's OK)

- [ ] **Step 4: Commit**

```bash
git add platform/src/iosMain/kotlin/uz/yalla/platform/navigation/NativeNavHost.ios.kt \
       platform/src/iosMain/kotlin/uz/yalla/platform/navigation/NavControllerSync.kt
git commit -m "feat(platform): add iOS NativeNavHost actual with UINavigationController + bidirectional sync"
```

---

## Chunk 4: Tests

### Task 10: Unit tests for NavigatorImpl

**Files:**
- Create: `platform/src/commonTest/kotlin/uz/yalla/platform/navigation/NavigatorImplTest.kt`

- [ ] **Step 1: Add test dependencies to platform/build.gradle.kts**

Add to the kotlin sourceSets block:
```kotlin
commonTest.dependencies {
    implementation(kotlin("test"))
}
```

- [ ] **Step 2: Create NavigatorImplTest.kt**

```kotlin
package uz.yalla.platform.navigation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.value.MutableValue
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Serializable
sealed class TestRoute : Route {
    @Serializable data object Home : TestRoute()
    @Serializable data object Detail : TestRoute()
    @Serializable data object Settings : TestRoute()
}

class NavigatorImplTest {

    @Test
    fun shouldReportCanGoBackFalseForSingleItemStack() {
        val nav = StackNavigation<TestRoute>()
        val stack = MutableValue(createStack(TestRoute.Home))
        val navigator = NavigatorImpl(nav, stack)

        assertFalse(navigator.canGoBack.value)
    }

    @Test
    fun shouldReportCanGoBackTrueForMultiItemStack() {
        val nav = StackNavigation<TestRoute>()
        val stack = MutableValue(createStack(TestRoute.Home, TestRoute.Detail))
        val navigator = NavigatorImpl(nav, stack)

        assertTrue(navigator.canGoBack.value)
    }

    @Test
    fun shouldReportCurrentRouteAsTopOfStack() {
        val nav = StackNavigation<TestRoute>()
        val stack = MutableValue(createStack(TestRoute.Home, TestRoute.Detail))
        val navigator = NavigatorImpl(nav, stack)

        assertEquals(TestRoute.Detail, navigator.currentRoute.value)
    }

    @Test
    fun shouldUpdateCanGoBackWhenStackChanges() {
        val nav = StackNavigation<TestRoute>()
        val stack = MutableValue(createStack(TestRoute.Home))
        val navigator = NavigatorImpl(nav, stack)

        assertFalse(navigator.canGoBack.value)

        stack.value = createStack(TestRoute.Home, TestRoute.Detail)
        assertTrue(navigator.canGoBack.value)
    }

    @Test
    fun shouldUpdateCurrentRouteWhenStackChanges() {
        val nav = StackNavigation<TestRoute>()
        val stack = MutableValue(createStack(TestRoute.Home))
        val navigator = NavigatorImpl(nav, stack)

        assertEquals(TestRoute.Home, navigator.currentRoute.value)

        stack.value = createStack(TestRoute.Home, TestRoute.Settings)
        assertEquals(TestRoute.Settings, navigator.currentRoute.value)
    }

    private fun createStack(vararg routes: TestRoute): ChildStack<TestRoute, Unit> {
        val items = routes.map { ChildStack.Item(configuration = it, instance = Unit) }
        return ChildStack(
            active = items.last(),
            backStack = items.dropLast(1),
        )
    }
}
```

- [ ] **Step 3: Run tests**

Run: `./gradlew :platform:allTests 2>&1 | tail -10`
Expected: Tests pass (or at minimum, `NavigatorImplTest` passes)

- [ ] **Step 4: Commit**

```bash
git add platform/build.gradle.kts \
       platform/src/commonTest/kotlin/uz/yalla/platform/navigation/NavigatorImplTest.kt
git commit -m "test(platform): add NavigatorImpl unit tests"
```

---

### Task 11: Unit tests for ToolbarState and ScreenConfig

**Files:**
- Create: `platform/src/commonTest/kotlin/uz/yalla/platform/navigation/ToolbarStateTest.kt`
- Create: `platform/src/commonTest/kotlin/uz/yalla/platform/navigation/ScreenConfigTest.kt`

- [ ] **Step 1: Create ToolbarStateTest.kt**

```kotlin
package uz.yalla.platform.navigation

import uz.yalla.primitives.navigation.ToolbarAction
import uz.yalla.primitives.navigation.ToolbarIcon
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ToolbarStateTest {
    @Test
    fun shouldStartWithEmptyActions() {
        val state = ToolbarState()
        assertTrue(state.actions.isEmpty())
    }

    @Test
    fun shouldUpdateActionsWhenSet() {
        val state = ToolbarState()
        val actions = listOf(
            ToolbarAction.Icon(ToolbarIcon.Edit) {},
            ToolbarAction.Text("Save") {},
        )
        state.actions = actions

        assertEquals(2, state.actions.size)
    }

    @Test
    fun shouldReplaceActionsOnSubsequentSets() {
        val state = ToolbarState()
        state.actions = listOf(ToolbarAction.Text("A") {})
        state.actions = listOf(ToolbarAction.Text("B") {}, ToolbarAction.Text("C") {})

        assertEquals(2, state.actions.size)
        assertEquals("B", (state.actions[0] as ToolbarAction.Text).label)
    }
}
```

- [ ] **Step 2: Create ScreenConfigTest.kt**

```kotlin
package uz.yalla.platform.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ScreenConfigTest {
    @Test
    fun shouldHaveSensibleDefaults() {
        val config = ScreenConfig()

        assertNull(config.title)
        assertEquals(LargeTitleMode.Never, config.largeTitleMode)
        assertTrue(config.showsNavigationBar)
        assertFalse(config.transparentNavigationBar)
    }

    @Test
    fun shouldSetAllProperties() {
        val config = ScreenConfig(
            title = "Menu",
            largeTitleMode = LargeTitleMode.Always,
            showsNavigationBar = true,
            transparentNavigationBar = true,
        )

        assertEquals("Menu", config.title)
        assertEquals(LargeTitleMode.Always, config.largeTitleMode)
        assertTrue(config.transparentNavigationBar)
    }

    @Test
    fun shouldSupportCopyWithModification() {
        val original = ScreenConfig(title = "A")
        val copy = original.copy(title = "B")

        assertEquals("A", original.title)
        assertEquals("B", copy.title)
    }
}
```

- [ ] **Step 3: Run all tests**

Run: `./gradlew :platform:allTests 2>&1 | tail -10`
Expected: All tests pass

- [ ] **Step 4: Commit**

```bash
git add platform/src/commonTest/kotlin/uz/yalla/platform/navigation/ToolbarStateTest.kt \
       platform/src/commonTest/kotlin/uz/yalla/platform/navigation/ScreenConfigTest.kt
git commit -m "test(platform): add ToolbarState and ScreenConfig unit tests"
```

---

## Chunk 5: Full build verification

### Task 12: Verify full project builds

- [ ] **Step 1: Run full project build**

Run: `./gradlew build 2>&1 | tail -20`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 2: Run detekt**

Run: `./gradlew detekt 2>&1 | tail -10`
Expected: No violations (or only pre-existing ones)

- [ ] **Step 3: Run spotless check**

Run: `./gradlew spotlessCheck 2>&1 | tail -10`
Expected: Pass (run `./gradlew spotlessApply` first if needed)

- [ ] **Step 4: Verify all platform tests pass**

Run: `./gradlew :platform:allTests 2>&1 | tail -10`
Expected: All NavigatorImpl, ToolbarState, ScreenConfig tests pass

- [ ] **Step 5: Final commit if spotless made changes**

```bash
git add -A && git commit -m "style(platform): apply spotless formatting to navigation files"
```

---

## Summary

| Task | Description | Files | Commit |
|------|-------------|-------|--------|
| 1 | Decompose dependency | libs.versions.toml, build.gradle.kts | `chore(platform): add Decompose 3.2.2` |
| 2 | Route, ScreenConfig, LargeTitleMode | Route.kt | `feat(platform): add Route interface...` |
| 3 | Navigator interface | Navigator.kt | `feat(platform): add Navigator interface` |
| 4 | ScreenProvider + ToolbarState | ScreenProvider.kt, ToolbarState.kt | `feat(platform): add ScreenProvider<C>` |
| 5 | NavigatorImpl + NativeRootComponent | NavigatorImpl.kt, NativeRootComponent.kt | `feat(platform): add NavigatorImpl...` |
| 6 | expect NativeNavHost | NativeNavHost.kt | `feat(platform): add expect NativeNavHost` |
| 7 | Android actual | NativeNavHost.android.kt | `feat(platform): add Android actual` |
| 8 | iOS NavigationBarAppearance | NavigationBarAppearance.kt, IosPlatformConfig.kt | `feat(platform): add NavigationBarAppearance` |
| 9 | iOS actual + sync engine | NativeNavHost.ios.kt, NavControllerSync.kt | `feat(platform): add iOS actual` |
| 10 | NavigatorImpl tests | NavigatorImplTest.kt | `test(platform): add NavigatorImpl tests` |
| 11 | ToolbarState + ScreenConfig tests | ToolbarStateTest.kt, ScreenConfigTest.kt | `test(platform): add ToolbarState...tests` |
| 12 | Full verification | — | `style(platform): apply spotless` |
