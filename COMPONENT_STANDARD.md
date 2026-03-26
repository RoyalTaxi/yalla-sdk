# Yalla SDK — Component Gold Standard

> Every component in yalla-sdk MUST follow this standard. No exceptions.
> This document is the single source of truth for API design, architecture,
> testing, and documentation of all UI components.

**Authority:** Based on [Google Compose API Guidelines](https://github.com/androidx/androidx/blob/androidx-main/compose/docs/compose-api-guidelines.md), [Compose Component API Guidelines](https://github.com/androidx/androidx/blob/androidx-main/compose/docs/compose-component-api-guidelines.md), Material3 source patterns, and Radix UI documentation standards.

---

## Table of Contents

1. [API Design Rules](#1-api-design-rules)
2. [Component Architecture](#2-component-architecture)
3. [State Management](#3-state-management)
4. [Design Tokens](#4-design-tokens)
5. [Platform Adaptation](#5-platform-adaptation)
6. [Testing Requirements](#6-testing-requirements)
7. [Documentation Format](#7-documentation-format)
8. [Checklist](#8-gold-standard-checklist)
9. [Anti-Patterns](#9-anti-patterns-banned)
10. [Migration Guide](#10-migration-guide-from-current-state)

---

## 1. API Design Rules

### 1.1 Naming

| Rule | Example |
|------|---------|
| Unit-returning composables: **PascalCase nouns** | `PrimaryButton`, `AddressCard`, `Sheet` |
| Value-returning composables: **camelCase** | `rememberSheetState()` |
| Remember-based factories: **`remember` prefix** | `rememberExpandableSheetState()` |
| Event callbacks: **`on` + present-tense verb** | `onClick`, `onValueChange`, `onDismissRequest` |
| Defaults objects: **`{Component}Defaults`** | `PrimaryButtonDefaults`, `SheetDefaults` |
| Color classes: **`{Component}Colors`** | `PrimaryButtonColors`, `TopBarColors` |
| CompositionLocals: **`Local` prefix** | `LocalColorScheme`, `LocalFontScheme` |

### 1.2 Parameter Ordering (Mandatory, Non-Negotiable)

```kotlin
@Composable
fun ComponentName(
    // 1. REQUIRED parameters (no defaults) — what makes this component work
    onClick: () -> Unit,

    // 2. MODIFIER — always first optional parameter
    modifier: Modifier = Modifier,

    // 3. BEHAVIORAL parameters — enabled, loading, etc.
    enabled: Boolean = true,

    // 4. STYLING parameters — colors, dimens, shape
    colors: ComponentColors = ComponentDefaults.colors(),
    dimens: ComponentDimens = ComponentDefaults.dimens(),

    // 5. SLOT parameters (non-trailing) — optional composable slots
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,

    // 6. TRAILING LAMBDA — primary content slot, always last
    content: @Composable RowScope.() -> Unit,
)
```

### 1.3 Modifier Rules

- **Name:** Always `modifier`, never `buttonModifier` or `rootModifier`
- **Type:** `Modifier`, never a subclass
- **Default:** `Modifier` (empty), never `Modifier.fillMaxWidth()` or any other preset
- **Position:** First optional parameter, always
- **Application:** Applied to root layout node. Internal modifiers appended AFTER caller's modifier
- **Quantity:** ONE per component. Multiple modifier params = bad API design

```kotlin
// CORRECT — caller's modifier applied first, internal appended
Surface(
    modifier = modifier          // caller's modifier
        .clip(shape)             // then internal
        .clickable(onClick = onClick)
        .padding(contentPadding),
    ...
)

// WRONG — internal modifier overwrites caller's
Surface(
    modifier = Modifier.clip(shape).then(modifier), // caller's lost
    ...
)
```

### 1.4 Content Over Configuration

**Use `@Composable` lambdas (slots) instead of primitive parameters for content.**

```kotlin
// WRONG — limits what title can be
@Composable
fun TopBar(title: String?, ...)

// CORRECT — any composable content
@Composable
fun TopBar(title: @Composable (() -> Unit)? = null, ...)

// Usage — simple text
TopBar(title = { Text("Home") })

// Usage — rich content (icon + text, styled text, etc.)
TopBar(title = {
    Row {
        Icon(YallaIcons.Location, null)
        Text("Tashkent", style = System.font.title.base.bold)
    }
})
```

**Exception:** When the content is ALWAYS a simple string and rich content makes no sense (e.g., `contentDescription: String` for accessibility).

### 1.5 Named Variants Over Style Enums

```kotlin
// WRONG — style parameter creates grab-bag API
@Composable
fun Button(style: ButtonStyle = ButtonStyle.Primary, ...)

// CORRECT — each variant is its own composable
@Composable fun PrimaryButton(onClick: () -> Unit, ...)
@Composable fun SecondaryButton(onClick: () -> Unit, ...)
@Composable fun TextButton(onClick: () -> Unit, ...)
@Composable fun GradientButton(onClick: () -> Unit, ...)
@Composable fun IconButton(onClick: () -> Unit, ...)
```

**Why:** Each variant has a focused API. `PrimaryButton` doesn't expose `borderWidth` (it has no border). `IconButton` doesn't expose `text` (it only has an icon).

---

## 2. Component Architecture

### 2.1 Building Block Hierarchy

Components are organized in layers. Higher layers compose from lower layers.

```
Layer 0: Design Tokens (ColorScheme, FontScheme, YallaTheme)
   ↓
Layer 1: Primitives (PrimaryButton, PrimaryField, TopBar, LoadingIndicator)
   ↓
Layer 2: Composites (AddressCard, Sheet, ListItem, Snackbar)
   ↓
Layer 3: Features (SearchLocationSheet, AddCardSheet — in app, not SDK)
```

**Rules:**
- Layer N can ONLY depend on Layer N-1 or lower
- Composites MUST be composed from Primitives
- Features MUST be composed from Composites + Primitives
- Never skip layers (a Feature should not build raw `Box + Text` when a `ContentCard` exists)

### 2.2 Component Layering (Internal Structure)

Every component follows the **Container → Provider → Layout → Content** pattern:

```kotlin
@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    colors: PrimaryButtonColors = PrimaryButtonDefaults.colors(),
    dimens: PrimaryButtonDimens = PrimaryButtonDefaults.dimens(),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    // 1. RESOLVE state-dependent values
    val containerColor = colors.containerColor(enabled)
    val contentColor = colors.contentColor(enabled)

    // 2. CONTAINER — handles click, shape, color, elevation, semantics
    Surface(
        onClick = onClick,
        modifier = modifier.semantics { role = Role.Button },
        enabled = enabled && !loading,
        shape = dimens.shape,
        color = containerColor,
    ) {
        // 3. PROVIDER — establish content color/style for children
        CompositionLocalProvider(
            LocalContentColor provides contentColor,
        ) {
            // 4. LAYOUT — arrangement and spacing
            Row(
                modifier = Modifier
                    .defaultMinSize(minHeight = dimens.minHeight)
                    .padding(dimens.contentPadding),
                horizontalArrangement = Arrangement.spacedBy(
                    dimens.iconSpacing,
                    Alignment.CenterHorizontally,
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 5. CONTENT — slots
                if (loading) {
                    LoadingIndicator(size = LoadingIndicatorSize.Small)
                } else {
                    leadingIcon?.invoke()
                    content()
                    trailingIcon?.invoke()
                }
            }
        }
    }
}
```

### 2.3 Composable Reuse Within SDK

Composites MUST use SDK primitives, not rebuild from scratch:

```kotlin
// WRONG — AddressCard builds its own button
@Composable
fun AddressCard(...) {
    Box(Modifier.clickable { ... }.background(...).padding(...)) {
        Text(address, ...)  // raw Text, no design tokens
    }
}

// CORRECT — AddressCard composes from ContentCard + primitives
@Composable
fun AddressCard(
    address: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    colors: AddressCardColors = AddressCardDefaults.colors(),
) {
    ContentCard(
        onClick = onClick,
        modifier = modifier,
        colors = colors.cardColors,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            leadingIcon?.invoke()
            Text(address, style = System.font.body.base.regular)
        }
    }
}
```

---

## 3. State Management

### 3.1 The Golden Rule: State Hoisting

**Components MUST be stateless.** State is owned by the caller, not the component.

```kotlin
// WRONG — component owns state
@Composable
fun Checkbox() {
    var checked by remember { mutableStateOf(false) }
    // ...
}

// CORRECT — caller owns state
@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
)
```

### 3.2 No Content-State Bundling

**NEVER bundle content (text, icons) with behavioral state (enabled, loading) in a single class.**

```kotlin
// BANNED — text is content, enabled/loading are state
data class PrimaryButtonState(
    val text: String,
    val enabled: Boolean = true,
    val loading: Boolean = false,
    val size: ButtonSize = ButtonSize.Medium,
)

// CORRECT — content and state are separate parameters
@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    colors: PrimaryButtonColors = PrimaryButtonDefaults.colors(),
    dimens: PrimaryButtonDimens = PrimaryButtonDefaults.dimens(),
    content: @Composable RowScope.() -> Unit,  // content is a slot
)

// Usage
PrimaryButton(onClick = { submit() }, loading = isSubmitting) {
    Text("Submit Order")
}
```

### 3.3 State Objects for Complex State

When a component has complex interrelated state, use a **state holder class:**

```kotlin
@Stable
class SheetState(
    initialExpanded: Boolean = false,
) {
    var isExpanded by mutableStateOf(initialExpanded)
        internal set

    suspend fun expand() { /* animation logic */ }
    suspend fun collapse() { /* animation logic */ }
}

@Composable
fun rememberSheetState(initialExpanded: Boolean = false): SheetState =
    remember { SheetState(initialExpanded) }

// Component takes state as parameter
@Composable
fun ExpandableSheet(
    state: SheetState = rememberSheetState(),
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
)
```

**Rules for state holders:**
- Declare as `class` (not data class) — identity matters
- Mark with `@Stable`
- Internal mutation only (`internal set`)
- Provide `remember*` factory
- State flows down, events flow up

### 3.4 No Dual State (Controlled + Uncontrolled)

**NEVER maintain internal state alongside controlled state.**

```kotlin
// BANNED — internal state duplicates external
@Composable
fun PinView(state: PinViewState, onValueChange: (Char?) -> Unit) {
    var internalValue by remember { mutableStateOf(state.value) }  // BANNED
    // ...
}

// CORRECT — fully controlled, no internal state
@Composable
fun PinView(
    value: String,
    length: Int,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
)
```

### 3.5 Never Expose MutableState or State as Parameters

```kotlin
// BANNED
fun Component(value: MutableState<String>)
fun Component(value: State<String>)

// CORRECT
fun Component(value: String, onValueChange: (String) -> Unit)
```

---

## 4. Design Tokens

### 4.1 Zero Hardcoded Values

**Every visual value MUST come from the design system.** No magic numbers.

```kotlin
// BANNED
Box(modifier = Modifier.padding(16.dp))
Text(color = Color(0xFF333333))
RoundedCornerShape(50)

// CORRECT
Box(modifier = Modifier.padding(dimens.contentPadding))
Text(color = colors.textColor)
dimens.shape  // defined in Defaults
```

### 4.2 Defaults Object Pattern

Every component MUST have a `{Component}Defaults` object:

```kotlin
object PrimaryButtonDefaults {

    /**
     * Default content padding for [PrimaryButton].
     */
    val ContentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)

    /**
     * Minimum height for [PrimaryButton].
     */
    val MinHeight = 48.dp

    /**
     * Default shape for [PrimaryButton].
     */
    val Shape: Shape = RoundedCornerShape(12.dp)

    /**
     * Creates [PrimaryButtonColors] with the default color values.
     */
    @Composable
    fun colors(
        containerColor: Color = System.color.button.active,
        contentColor: Color = System.color.text.inverse,
        disabledContainerColor: Color = System.color.button.disabled,
        disabledContentColor: Color = System.color.text.disabled,
    ): PrimaryButtonColors = PrimaryButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
    )

    /**
     * Creates [PrimaryButtonDimens] with the default dimension values.
     */
    fun dimens(
        minHeight: Dp = MinHeight,
        contentPadding: PaddingValues = ContentPadding,
        shape: Shape = Shape,
        iconSpacing: Dp = 8.dp,
    ): PrimaryButtonDimens = PrimaryButtonDimens(
        minHeight = minHeight,
        contentPadding = contentPadding,
        shape = shape,
        iconSpacing = iconSpacing,
    )
}
```

### 4.3 State-Aware Color Classes

For components with multiple visual states:

```kotlin
@Immutable
data class PrimaryButtonColors(
    val containerColor: Color,
    val contentColor: Color,
    val disabledContainerColor: Color,
    val disabledContentColor: Color,
) {
    @Composable
    fun containerColor(enabled: Boolean): Color =
        if (enabled) containerColor else disabledContainerColor

    @Composable
    fun contentColor(enabled: Boolean): Color =
        if (enabled) contentColor else disabledContentColor
}
```

### 4.4 Dimension Classes

```kotlin
@Immutable
data class PrimaryButtonDimens(
    val minHeight: Dp,
    val contentPadding: PaddingValues,
    val shape: Shape,
    val iconSpacing: Dp,
)
```

### 4.5 @Stable and @Immutable Annotations

| Annotation | When to use |
|-----------|-------------|
| `@Immutable` | Data classes that never change after construction: Colors, Dimens |
| `@Stable` | State holders with observable mutation: SheetState, ScrollState |

```kotlin
@Immutable  // all properties are val, no mutation
data class TopBarColors(val backgroundColor: Color, val contentColor: Color)

@Stable  // has mutableStateOf, but Compose is notified of changes
class SheetState(initialExpanded: Boolean) {
    var isExpanded by mutableStateOf(initialExpanded)
}
```

---

## 5. Platform Adaptation

### 5.1 When to Use expect/actual

| Component Type | Approach | Reason |
|---------------|----------|--------|
| Sheet / Bottom Sheet | `expect/actual` or native | iOS users expect UIKit sheet behavior (rubber band, detents) |
| Date/Time Picker | `expect/actual` | iOS wheel picker vs Android calendar |
| Switch / Toggle | `expect/actual` | UISwitch vs Material Switch |
| Loading Spinner | `expect/actual` | UIActivityIndicatorView vs CircularProgressIndicator |
| Navigation transitions | `expect/actual` | iOS push/pop vs Android shared element |
| Haptic feedback | `expect/actual` | UIImpactFeedbackGenerator vs VibrationEffect |
| Buttons | **Compose** | Custom design, no native equivalent |
| Cards | **Compose** | Custom design, no native equivalent |
| Fields / Inputs | **Compose** | Custom design (but keyboard handling may need platform code) |
| Lists / Items | **Compose** | Custom design |
| Indicators / Progress | **Compose** (except spinner) | Custom design |

### 5.2 Platform-Aware Defaults

Use `expect` for platform-specific default values:

```kotlin
// commonMain
expect val DefaultOverscrollEffect: OverscrollEffect

// androidMain
actual val DefaultOverscrollEffect = AndroidOverscrollEffect()

// iosMain
actual val DefaultOverscrollEffect = BouncingOverscrollEffect()  // iOS rubber-band
```

### 5.3 iOS Native Feel Checklist

When implementing a component, verify:
- [ ] Touch feedback feels native (no Material ripple on iOS)
- [ ] Scroll behavior matches platform (rubber band on iOS)
- [ ] Keyboard behavior is correct (keyboard avoidance, done/next buttons)
- [ ] System font is used where appropriate
- [ ] Minimum touch target is 44pt on iOS (48dp on Android)

---

## 6. Testing Requirements

### 6.1 What to Test

Every component MUST have tests for:

| Category | What to Test | Example |
|----------|-------------|---------|
| **Rendering** | Component displays correctly | `onNodeWithText("Submit").assertIsDisplayed()` |
| **Interaction** | Click/tap triggers callback | `onNode(...).performClick()` → verify callback |
| **State** | Enabled/disabled/loading states | `assertIsEnabled()`, `assertIsNotEnabled()` |
| **Slots** | Slot content is rendered | `onNodeWithTag("leadingIcon").assertExists()` |
| **Accessibility** | Semantic properties correct | `assertHasClickAction()`, content description |
| **Edge cases** | Empty content, null slots, max content | Verify no crash |

### 6.2 Test Structure

```kotlin
class PrimaryButtonTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun clicksWhenEnabled() = runComposeUiTest {
        var clicked = false
        setContent {
            PrimaryButton(
                onClick = { clicked = true },
                modifier = Modifier.testTag("button"),
            ) {
                Text("Submit")
            }
        }

        onNodeWithTag("button").performClick()
        assertTrue(clicked)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun doesNotClickWhenDisabled() = runComposeUiTest {
        var clicked = false
        setContent {
            PrimaryButton(
                onClick = { clicked = true },
                enabled = false,
                modifier = Modifier.testTag("button"),
            ) {
                Text("Submit")
            }
        }

        onNodeWithTag("button").performClick()
        assertFalse(clicked)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun showsLoadingIndicatorWhenLoading() = runComposeUiTest {
        setContent {
            PrimaryButton(
                onClick = {},
                loading = true,
                modifier = Modifier.testTag("button"),
            ) {
                Text("Submit")
            }
        }

        onNodeWithText("Submit").assertDoesNotExist()
        // Loading indicator should be visible
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun rendersSlotContent() = runComposeUiTest {
        setContent {
            PrimaryButton(
                onClick = {},
                leadingIcon = {
                    Icon(
                        YallaIcons.Plus,
                        contentDescription = "Add",
                        modifier = Modifier.testTag("icon"),
                    )
                },
            ) {
                Text("Add Item")
            }
        }

        onNodeWithTag("icon").assertIsDisplayed()
        onNodeWithText("Add Item").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun hasCorrectSemantics() = runComposeUiTest {
        setContent {
            PrimaryButton(
                onClick = {},
                modifier = Modifier.testTag("button"),
            ) {
                Text("Submit")
            }
        }

        onNodeWithTag("button").assertHasClickAction()
    }
}
```

### 6.3 Test File Location

```
primitives/
  src/
    commonMain/kotlin/uz/yalla/primitives/button/PrimaryButton.kt
    commonTest/kotlin/uz/yalla/primitives/button/PrimaryButtonTest.kt
```

### 6.4 Minimum Test Count Per Component

| Component Type | Min Tests |
|---------------|-----------|
| Interactive (button, field, checkbox) | 5+ (click, disabled, loading, slots, semantics) |
| Display (card, item, indicator) | 3+ (rendering, slots, edge cases) |
| Container (sheet, dialog) | 4+ (show, dismiss, content, state) |

---

## 7. Documentation Format

### 7.1 KDoc Template

Every public composable MUST have KDoc following this exact structure:

```kotlin
/**
 * A primary action button with solid background color.
 *
 * Primary buttons are used for the most important action on a screen.
 * Use sparingly — typically one per screen or dialog.
 *
 * For other emphasis levels, see [SecondaryButton], [TextButton], and [GradientButton].
 *
 * ## Building Blocks
 * Uses [Surface] for click handling and shape, [LoadingIndicator] for loading state.
 *
 * ## Usage
 * ```kotlin
 * PrimaryButton(onClick = { submitOrder() }) {
 *     Text("Submit Order")
 * }
 * ```
 *
 * ## With Icons
 * ```kotlin
 * PrimaryButton(
 *     onClick = { addToCart() },
 *     leadingIcon = { Icon(YallaIcons.Plus, contentDescription = null) },
 * ) {
 *     Text("Add to Cart")
 * }
 * ```
 *
 * @param onClick Called when this button is clicked.
 * @param modifier [Modifier] applied to the root container.
 * @param enabled Controls the enabled state. When `false`, the button will not respond
 *   to user input and will appear visually disabled.
 * @param loading When `true`, shows a loading indicator instead of content.
 *   The button is not clickable while loading.
 * @param colors [PrimaryButtonColors] that resolve colors for different states.
 *   See [PrimaryButtonDefaults.colors].
 * @param dimens [PrimaryButtonDimens] that define dimensions and shape.
 *   See [PrimaryButtonDefaults.dimens].
 * @param leadingIcon Optional composable displayed before the content.
 * @param trailingIcon Optional composable displayed after the content.
 * @param content The button content, typically a [Text].
 *
 * @see SecondaryButton
 * @see TextButton
 * @see GradientButton
 */
@Composable
fun PrimaryButton(...)
```

### 7.2 MODULE.md Template

Each module directory MUST have a `MODULE.md` following this structure:

```markdown
# Module Name

> One-line description of the module's purpose.

## Components

### ComponentName

**Description:** What this component does and when to use it.

**Anatomy:**
- Container (Surface) — handles click, shape, elevation
- Content Row — horizontal arrangement of slots
- Slots: leadingIcon, content, trailingIcon

**Variants:** (if applicable)
- `PrimaryButton` — high emphasis, solid background
- `SecondaryButton` — medium emphasis, outlined
- `TextButton` — low emphasis, text only

**When to use:**
- Use `PrimaryButton` for the main action (1 per screen)
- Use `SecondaryButton` for secondary actions
- Use `TextButton` for tertiary or cancel actions

**Platform behavior:**
- Android: Material ripple effect
- iOS: Opacity feedback on press

**Example:**
\```kotlin
PrimaryButton(onClick = { }) {
    Text("Get Started")
}
\```

---
(repeat for each component)
```

---

## 8. Gold Standard Checklist

Use this checklist when creating or auditing any component. **ALL items must pass.**

### API Design
- [ ] PascalCase noun name
- [ ] Parameter order: required → modifier → behavioral → styling → slots → trailing lambda
- [ ] `modifier: Modifier = Modifier` as first optional parameter
- [ ] No default modifier other than `Modifier` (no `Modifier.fillMaxWidth()`)
- [ ] Content exposed as `@Composable` lambda slots (not `String?`)
- [ ] Event callbacks named `on` + present-tense verb
- [ ] Named variants instead of style enums

### State Management
- [ ] Stateless — no internal `remember { mutableStateOf() }` for controlled state
- [ ] No content-state bundling (no `ButtonState(text, enabled)`)
- [ ] Complex state uses `@Stable` state holder class
- [ ] State holders have `remember*` factory
- [ ] No `MutableState` or `State` in public parameters

### Design Tokens
- [ ] All colors from `System.color.*` via Defaults
- [ ] All typography from `System.font.*` via Defaults
- [ ] All dimensions in Defaults object
- [ ] Zero hardcoded `Color()`, `Dp`, `Shape` values in component body
- [ ] Colors class annotated `@Immutable`
- [ ] Dimens class annotated `@Immutable`
- [ ] State-aware color resolution (`colors.containerColor(enabled)`)

### Architecture
- [ ] Follows Container → Provider → Layout → Content pattern
- [ ] Modifier applied to root node, internal modifiers appended after
- [ ] Composites use SDK Primitives (no rebuilding from scratch)
- [ ] Single `modifier` parameter (not multiple)
- [ ] `Defaults` object with `colors()` and `dimens()` factories

### Testing
- [ ] Test file exists in `commonTest`
- [ ] Interaction tests (click, input)
- [ ] State tests (enabled, disabled, loading)
- [ ] Slot rendering tests
- [ ] Accessibility/semantics tests
- [ ] Minimum test count met (see 6.4)

### Documentation
- [ ] KDoc with description, usage examples, @param for ALL parameters
- [ ] `@see` references to related components
- [ ] Building blocks section in KDoc
- [ ] Entry in MODULE.md with anatomy, variants, when-to-use, example
- [ ] `@Preview` annotation with light/dark variants

### Platform
- [ ] iOS: No Material ripple (use appropriate feedback)
- [ ] Touch target minimum: 48dp Android, 44pt iOS
- [ ] expect/actual where platform-native is better (see Section 5.1)

---

## 9. Anti-Patterns (BANNED)

### 9.1 Content-State Bundling
```kotlin
// BANNED
data class ButtonState(val text: String, val enabled: Boolean)
fun Button(state: ButtonState, onClick: () -> Unit)

// REQUIRED
fun Button(onClick: () -> Unit, enabled: Boolean = true, content: @Composable () -> Unit)
```

### 9.2 Dual State
```kotlin
// BANNED — internal state alongside controlled
@Composable
fun Input(value: String, onValueChange: (String) -> Unit) {
    var internal by remember { mutableStateOf(value) }  // BANNED
}
```

### 9.3 String Parameters for Content
```kotlin
// BANNED (for content that could be rich)
fun TopBar(title: String? = null)

// REQUIRED
fun TopBar(title: @Composable (() -> Unit)? = null)
```

### 9.4 Hardcoded Visual Values
```kotlin
// BANNED
Box(modifier = Modifier.padding(16.dp).background(Color(0xFFE0E0E0)))

// REQUIRED
Box(modifier = Modifier.padding(dimens.padding).background(colors.backgroundColor))
```

### 9.5 Default Modifier with Behavior
```kotlin
// BANNED — caller doesn't expect fillMaxWidth by default
fun Card(modifier: Modifier = Modifier.fillMaxWidth())

// REQUIRED
fun Card(modifier: Modifier = Modifier)
```

### 9.6 Null Sentinel for "Use Default"
```kotlin
// BANNED — null means "use theme color" is implicit
fun Button(color: Color? = null) {
    val resolved = color ?: MaterialTheme.colorScheme.primary  // hidden
}

// REQUIRED — default reads theme explicitly
fun Button(color: Color = System.color.button.active)  // discoverable
```

### 9.7 Code Duplication for Optional Click
```kotlin
// BANNED — two branches that are 90% identical
if (onClick != null) {
    Surface(onClick = onClick) { content() }
} else {
    Surface { content() }
}

// REQUIRED — extract to single path
Surface(
    onClick = onClick ?: {},
    enabled = onClick != null,
) {
    content()
}
```

---

## 10. Migration Guide (From Current State)

### Button Components

**Before:**
```kotlin
data class PrimaryButtonState(
    val text: String,
    val enabled: Boolean = true,
    val loading: Boolean = false,
    val size: ButtonSize = ButtonSize.Medium,
)

@Composable
fun PrimaryButton(
    state: PrimaryButtonState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: PrimaryButtonDefaults.PrimaryButtonColors = ...,
    style: PrimaryButtonDefaults.PrimaryButtonStyle = ...,
    dimens: PrimaryButtonDefaults.PrimaryButtonDimens = ...,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
)
```

**After:**
```kotlin
@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    colors: PrimaryButtonColors = PrimaryButtonDefaults.colors(),
    dimens: PrimaryButtonDimens = PrimaryButtonDefaults.dimens(),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
)
```

**Migration steps:**
1. Remove `*State` data class
2. Move `text` to trailing `content` lambda
3. Move `enabled`, `loading` to direct parameters
4. Move `size` into `dimens` factory
5. Flatten `colors`/`style`/`dimens` into `colors`/`dimens` (merge style into dimens)
6. Rename nested classes: `PrimaryButtonDefaults.PrimaryButtonColors` → `PrimaryButtonColors` (top-level)
7. Update all call sites

### TopBar / Fields

**Before:** `title: String?`, `placeholder: String?`
**After:** `title: @Composable (() -> Unit)?`, `placeholder: @Composable (() -> Unit)?`

### PinView

**Before:** `PinViewState` + internal `mutableStateOf`
**After:** `value: String, length: Int, onValueChange: (String) -> Unit` — fully controlled

### Cards with Optional Click

**Before:** Two code branches for clickable/non-clickable
**After:** Single path with `enabled = onClick != null`

---

## Appendix: Quick Reference Card

```
PARAMETER ORDER:
  required → modifier → behavioral → styling → slots → content

NAMING:
  Component:  PascalCase noun     (PrimaryButton)
  State:      {Component}State    (SheetState)
  Defaults:   {Component}Defaults (PrimaryButtonDefaults)
  Colors:     {Component}Colors   (PrimaryButtonColors)
  Dimens:     {Component}Dimens   (PrimaryButtonDimens)
  Events:     on + verb           (onClick, onDismissRequest)
  Remember:   remember + noun     (rememberSheetState)

SLOT PATTERN:
  content: @Composable RowScope.() -> Unit  (last param, trailing lambda)
  leadingIcon: @Composable (() -> Unit)?    (optional slot)

DEFAULTS PATTERN:
  object PrimaryButtonDefaults {
      fun colors(...): PrimaryButtonColors
      fun dimens(...): PrimaryButtonDimens
  }

LAYERING:
  Container (Surface) → Provider (CompositionLocal) → Layout (Row/Column) → Content (slots)

TESTING:
  Interactive: 5+ tests (click, disabled, loading, slots, semantics)
  Display:     3+ tests (rendering, slots, edge cases)
  Container:   4+ tests (show, dismiss, content, state)
```
