# Button Gold Standard Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Refactor all button primitives to gold standard — shared infrastructure, flat API, content slots, @Immutable annotations, full test coverage, zero duplication.

**Architecture:** Extract shared `ButtonLayout` composable → refactor PrimaryButton as template → migrate all button variants (Secondary, Text, Sensitive, BottomSheet, Gender, Navigation, Icon) → remove 4 unused buttons → migrate all 38 YallaClient call sites.

**Tech Stack:** Kotlin 2.3.0, Compose Multiplatform 1.10.0, kotlin.test, Material3 Surface

**Spec:** `docs/superpowers/specs/2026-03-25-ui-gold-standard-design.md`
**Standard:** `COMPONENT_STANDARD.md`
**Audit:** `AUDIT_RESULTS.md`

---

## File Structure

### New Files
- `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/ButtonLayout.kt` — shared internal composable for all buttons
- `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/PrimaryButtonColors.kt` — top-level @Immutable colors
- `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/PrimaryButtonDimens.kt` — top-level @Immutable dimens
- `primitives/src/commonTest/kotlin/uz/yalla/primitives/button/PrimaryButtonTest.kt` — behavioral tests
- `primitives/src/commonTest/kotlin/uz/yalla/primitives/button/SecondaryButtonTest.kt`
- `primitives/src/commonTest/kotlin/uz/yalla/primitives/button/TextButtonTest.kt`
- `primitives/src/commonTest/kotlin/uz/yalla/primitives/button/IconButtonTest.kt`
- `primitives/src/commonTest/kotlin/uz/yalla/primitives/button/SensitiveButtonTest.kt`
- `primitives/src/commonTest/kotlin/uz/yalla/primitives/button/BottomSheetButtonTest.kt`
- `primitives/src/commonTest/kotlin/uz/yalla/primitives/button/GenderButtonTest.kt`

### Modified Files
- `primitives/build.gradle.kts` — add commonTest dependencies (kotlin-test, compose-ui-test)
- `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/PrimaryButton.kt` — full rewrite
- `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/SecondaryButton.kt` — full rewrite
- `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/TextButton.kt` — full rewrite
- `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/IconButton.kt` — remove State class, flatten params
- `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/SensitiveButton.kt` — rewrite with ButtonLayout
- `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/BottomSheetButton.kt` — rewrite
- `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/GenderButton.kt` — rewrite
- `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/NavigationButton.kt` — rewrite

### Deleted Files
- `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/GradientButton.kt` — 0 usages
- `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/SupportButton.kt` — 0 usages
- `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/EnableLocationButton.kt` — 0 usages

### YallaClient Migration (after SDK changes)
- 24 files using PrimaryButton
- 4 files using SecondaryButton
- 3 files using TextButton (SDK variant)
- 3 files using BottomSheetButton
- 2 files using GenderButton
- 1 file using NavigationButton
- 1 file using SensitiveButton
- 1 file using IconButton (if API changes)

---

## Task 1: Add Test Infrastructure to Primitives Module

**Files:**
- Modify: `primitives/build.gradle.kts`

- [ ] **Step 1: Add commonTest dependencies**

Add to `primitives/build.gradle.kts` inside the `kotlin` block:

```kotlin
commonTest.dependencies {
    implementation(kotlin("test"))
}
```

- [ ] **Step 2: Create commonTest directory structure**

```bash
mkdir -p /Users/macbookpro/Ildam/yalla/yalla-sdk/primitives/src/commonTest/kotlin/uz/yalla/primitives/button
```

- [ ] **Step 3: Verify build compiles**

```bash
cd /Users/macbookpro/Ildam/yalla/yalla-sdk && ./gradlew :primitives:compileCommonMainKotlinMetadata
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add primitives/build.gradle.kts
git commit -m "chore(primitives): add commonTest dependencies for button testing"
```

---

## Task 2: Delete Unused Button Components

**Files:**
- Delete: `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/GradientButton.kt`
- Delete: `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/SupportButton.kt`
- Delete: `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/EnableLocationButton.kt`

- [ ] **Step 1: Verify zero usages in YallaClient**

```bash
cd /Users/macbookpro/Ildam/yalla/YallaClient && grep -r "GradientButton\|SupportButton\|EnableLocationButton" --include="*.kt" feature/ composeApp/
```

Expected: No output (0 usages confirmed in audit)

- [ ] **Step 2: Delete the files**

```bash
cd /Users/macbookpro/Ildam/yalla/yalla-sdk
rm primitives/src/commonMain/kotlin/uz/yalla/primitives/button/GradientButton.kt
rm primitives/src/commonMain/kotlin/uz/yalla/primitives/button/SupportButton.kt
rm primitives/src/commonMain/kotlin/uz/yalla/primitives/button/EnableLocationButton.kt
```

- [ ] **Step 3: Verify SDK still builds**

```bash
./gradlew :primitives:compileCommonMainKotlinMetadata
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add -A primitives/src/commonMain/kotlin/uz/yalla/primitives/button/
git commit -m "chore(primitives): remove unused GradientButton, SupportButton, EnableLocationButton

Audit confirmed 0 usages in YallaClient for all three components."
```

---

## Task 3: Create ButtonLayout Shared Infrastructure

**Files:**
- Create: `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/ButtonLayout.kt`

- [ ] **Step 1: Write ButtonLayout**

```kotlin
package uz.yalla.primitives.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.platform.indicator.NativeLoadingIndicator

/**
 * Shared button layout used by all button variants.
 *
 * Provides the standard Container -> Provider -> Layout -> Content structure:
 * - [Surface] handles click, shape, color, and semantics
 * - [CompositionLocalProvider] establishes content color for children
 * - [Row] arranges icons and content horizontally
 * - Loading state replaces content with [NativeLoadingIndicator]
 *
 * This is internal API — not exposed to SDK consumers.
 * Button variants (PrimaryButton, SecondaryButton, etc.) delegate to this.
 *
 * @param onClick Called when the button is clicked.
 * @param modifier Applied to the root Surface.
 * @param enabled Whether the button is interactive.
 * @param loading When true, shows loading indicator instead of content.
 * @param shape Button container shape.
 * @param containerColor Background color.
 * @param contentColor Foreground color for text and icons.
 * @param contentPadding Internal padding between container and content.
 * @param minHeight Minimum button height.
 * @param iconSize Size constraint for leading/trailing icon slots.
 * @param iconSpacing Spacing between icons and content.
 * @param leadingIcon Optional composable before the main content.
 * @param trailingIcon Optional composable after the main content.
 * @param content The main button content, typically [Text].
 */
@Composable
internal fun ButtonLayout(
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    loading: Boolean,
    shape: Shape,
    containerColor: Color,
    contentColor: Color,
    contentPadding: PaddingValues,
    minHeight: Dp,
    iconSize: Dp = 20.dp,
    iconSpacing: Dp = 8.dp,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .defaultMinSize(minHeight = minHeight)
            .semantics { role = Role.Button },
        enabled = enabled && !loading,
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Row(
                modifier = Modifier.padding(contentPadding),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (loading) {
                    NativeLoadingIndicator(
                        modifier = Modifier.size(iconSize),
                        color = contentColor,
                        backgroundColor = containerColor,
                    )
                } else {
                    leadingIcon?.let { icon ->
                        Box(
                            modifier = Modifier.size(iconSize),
                            contentAlignment = Alignment.Center,
                        ) { icon() }
                        Spacer(Modifier.width(iconSpacing))
                    }
                    content()
                    trailingIcon?.let { icon ->
                        Spacer(Modifier.width(iconSpacing))
                        Box(
                            modifier = Modifier.size(iconSize),
                            contentAlignment = Alignment.Center,
                        ) { icon() }
                    }
                }
            }
        }
    }
}
```

- [ ] **Step 2: Verify it compiles**

```bash
cd /Users/macbookpro/Ildam/yalla/yalla-sdk && ./gradlew :primitives:compileCommonMainKotlinMetadata
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add primitives/src/commonMain/kotlin/uz/yalla/primitives/button/ButtonLayout.kt
git commit -m "feat(primitives): add ButtonLayout shared infrastructure for all button variants

Internal composable that provides Container -> Provider -> Layout -> Content
structure. All button variants will delegate to this, eliminating ~500 LOC
of duplication across PrimaryButton, SecondaryButton, and TextButton."
```

---

## Task 4: Rewrite PrimaryButton to Gold Standard

**Files:**
- Rewrite: `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/PrimaryButton.kt`

This is the template — all other buttons follow this pattern.

- [ ] **Step 1: Rewrite PrimaryButton.kt**

Replace the entire file with the gold standard implementation. Key changes:
1. Remove `PrimaryButtonState` data class
2. Flatten `text` to trailing `content: @Composable RowScope.() -> Unit`
3. Flatten `enabled`, `loading` to direct parameters
4. Remove `size` from public API (always Medium internally)
5. Remove `PrimaryButtonStyle` — text style resolved internally
6. Simplify `PrimaryButtonDimens` (4 params instead of 12)
7. Move `PrimaryButtonColors` and `PrimaryButtonDimens` to top-level with `@Immutable`
8. Delegate to `ButtonLayout`
9. `PrimaryButtonDefaults.colors()` — `@Composable` (reads theme)
10. `PrimaryButtonDefaults.dimens()` — regular function (no theme reads)

The new file should be ~150 LOC (down from 469).

**New API:**
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

**@Immutable data classes:**
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

@Immutable
data class PrimaryButtonDimens(
    val minHeight: Dp,
    val contentPadding: PaddingValues,
    val shape: Shape,
    val iconSpacing: Dp,
)
```

**Defaults:**
```kotlin
object PrimaryButtonDefaults {
    val MinHeight = 60.dp
    val ContentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
    val Shape: Shape = RoundedCornerShape(16.dp)

    @Composable
    fun colors(
        containerColor: Color = System.color.button.active,
        contentColor: Color = System.color.text.white,
        disabledContainerColor: Color = System.color.button.disabled,
        disabledContentColor: Color = System.color.text.white,
    ): PrimaryButtonColors = PrimaryButtonColors(...)

    fun dimens(
        minHeight: Dp = MinHeight,
        contentPadding: PaddingValues = ContentPadding,
        shape: Shape = Shape,
        iconSpacing: Dp = 8.dp,
    ): PrimaryButtonDimens = PrimaryButtonDimens(...)
}
```

- [ ] **Step 2: Verify SDK compiles**

```bash
cd /Users/macbookpro/Ildam/yalla/yalla-sdk && ./gradlew :primitives:compileCommonMainKotlinMetadata
```

Expected: BUILD SUCCESSFUL (SDK compiles, YallaClient will break — that's expected, migration comes later)

- [ ] **Step 3: Commit**

```bash
git add primitives/src/commonMain/kotlin/uz/yalla/primitives/button/PrimaryButton.kt
git commit -m "refactor(primitives): rewrite PrimaryButton to gold standard API

BREAKING: PrimaryButtonState removed. New API uses flat parameters + content slot.

Before: PrimaryButton(state = PrimaryButtonState(text = \"Submit\"), onClick = {})
After:  PrimaryButton(onClick = {}) { Text(\"Submit\") }

- Removed content-state bundling (PrimaryButtonState)
- text -> trailing content lambda (slot API)
- Removed size from public API (audit: 0 overrides)
- Simplified Dimens (12 params -> 4 params)
- Removed Style class (text style resolved internally)
- Added @Immutable to Colors and Dimens
- Delegates to shared ButtonLayout"
```

---

## Task 5: Write PrimaryButton Tests

**Files:**
- Create: `primitives/src/commonTest/kotlin/uz/yalla/primitives/button/PrimaryButtonTest.kt`

- [ ] **Step 1: Write behavioral tests**

```kotlin
package uz.yalla.primitives.button

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PrimaryButtonTest {

    @Test
    fun colorsResolveCorrectlyWhenEnabled() {
        val colors = PrimaryButtonColors(
            containerColor = Color.Blue,
            contentColor = Color.White,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.LightGray,
        )
        // Non-composable assertions on the data class
        assertTrue(colors.containerColor == Color.Blue)
        assertTrue(colors.disabledContainerColor == Color.Gray)
    }

    @Test
    fun dimensHaveCorrectDefaults() {
        val dimens = PrimaryButtonDefaults.dimens()
        assertTrue(dimens.minHeight == PrimaryButtonDefaults.MinHeight)
        assertTrue(dimens.shape == PrimaryButtonDefaults.Shape)
    }

    @Test
    fun defaultColorsAreNotEqual() {
        // Ensure enabled and disabled colors differ
        val colors = PrimaryButtonColors(
            containerColor = Color.Blue,
            contentColor = Color.White,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.LightGray,
        )
        assertFalse(colors.containerColor == colors.disabledContainerColor)
    }
}
```

Note: Full Compose UI tests (`runComposeUiTest`) require additional setup. Start with unit tests for data classes/defaults. UI interaction tests will be added once compose-ui-test dependency is configured.

- [ ] **Step 2: Run tests**

```bash
cd /Users/macbookpro/Ildam/yalla/yalla-sdk && ./gradlew :primitives:jvmTest
```

Expected: All tests PASS. If `jvmTest` task doesn't exist, try `:primitives:allTests` or check available test tasks.

- [ ] **Step 3: Commit**

```bash
git add primitives/src/commonTest/
git commit -m "test(primitives): add PrimaryButton unit tests for colors and dimens"
```

---

## Task 6: Rewrite SecondaryButton with ButtonLayout

**Files:**
- Rewrite: `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/SecondaryButton.kt`
- Create: `primitives/src/commonTest/kotlin/uz/yalla/primitives/button/SecondaryButtonTest.kt`

- [ ] **Step 1: Rewrite SecondaryButton**

Same pattern as PrimaryButton:
- Remove `SecondaryButtonState`
- Flat params + content slot
- Delegate to `ButtonLayout`
- `@Immutable` Colors/Dimens
- Only difference: `colors()` defaults use `System.color.button.tertiary` for container

- [ ] **Step 2: Write tests**

Same pattern as PrimaryButtonTest — test colors, dimens defaults.

- [ ] **Step 3: Verify SDK compiles and tests pass**

```bash
cd /Users/macbookpro/Ildam/yalla/yalla-sdk && ./gradlew :primitives:compileCommonMainKotlinMetadata
```

- [ ] **Step 4: Commit**

```bash
git commit -m "refactor(primitives): rewrite SecondaryButton to gold standard with ButtonLayout"
```

---

## Task 7: Rewrite TextButton with ButtonLayout

Same pattern. Only difference: `colors()` defaults — transparent container, `System.color.text.base` content.

- [ ] **Step 1: Rewrite TextButton** — remove `TextButtonState`, flat params, delegate to `ButtonLayout`
- [ ] **Step 2: Write TextButtonTest**
- [ ] **Step 3: Verify SDK compiles and tests pass**
- [ ] **Step 4: Commit**

---

## Task 8: Rewrite IconButton (Flatten State)

**Files:**
- Rewrite: `primitives/src/commonMain/kotlin/uz/yalla/primitives/button/IconButton.kt`

IconButton is already close to gold standard (uses content slot). Changes:
- Remove `IconButtonState` — flatten `enabled` to direct param (remove `size` — 0 overrides)
- Add `@Immutable` to Colors/Dimens
- Delegate to `Surface` directly (IconButton has no text/loading — ButtonLayout is overkill)

- [ ] **Step 1: Rewrite** — remove state class, flatten params
- [ ] **Step 2: Write IconButtonTest**
- [ ] **Step 3: Verify and commit**

---

## Task 9: Rewrite SensitiveButton

SensitiveButton is unique — it has a countdown timer with animation. It does NOT use ButtonLayout (completely different internal structure). Changes:
- Remove `SensitiveButtonState` — flatten to direct params
- Add `@Immutable` to Colors/Dimens
- Keep countdown animation logic intact
- Content is internally managed (countdown text → confirm text), not a slot

- [ ] **Step 1: Rewrite** — remove state class, flatten params
- [ ] **Step 2: Write SensitiveButtonTest** — test countdown state, enabled/disabled
- [ ] **Step 3: Verify and commit**

---

## Task 10: Rewrite BottomSheetButton

BottomSheetButton has icon + text layout. Changes:
- Remove `BottomSheetButtonState` — `text` and `painter` become direct params
- Currently uses Material3 `Button` — switch to `ButtonLayout` or `Surface`
- `@Immutable` Colors/Dimens

- [ ] **Step 1: Rewrite** — remove state class, icon + text as params, use ButtonLayout
- [ ] **Step 2: Write BottomSheetButtonTest**
- [ ] **Step 3: Verify and commit**

---

## Task 11: Rewrite GenderButton

GenderButton takes `GenderKind` and shows selection state. Changes:
- Remove `GenderButtonState` — `gender: GenderKind` and `isSelected: Boolean` become direct params
- Currently uses Material3 `Button` — switch to `Surface` or `ButtonLayout`
- `@Immutable` Colors/Dimens

- [ ] **Step 1: Rewrite** — remove state class, flatten params
- [ ] **Step 2: Write GenderButtonTest**
- [ ] **Step 3: Verify and commit**

---

## Task 12: Rewrite NavigationButton

NavigationButton shows a back arrow. Changes:
- Remove `NavigationButtonState` — `icon: ImageVector` and `contentDescription: String?` become direct params
- `@Immutable` Colors/Dimens

- [ ] **Step 1: Rewrite** — remove state class, flatten params
- [ ] **Step 2: Write NavigationButtonTest**
- [ ] **Step 3: Verify and commit**

---

## Task 13: Migrate YallaClient — PrimaryButton Call Sites (24 files)

**Prerequisite:** SDK published with new API (or local dependency).

Each file needs mechanical migration:
```kotlin
// Before
PrimaryButton(
    state = PrimaryButtonState(text = stringResource(Res.string.xxx), enabled = condition, loading = loading),
    onClick = { onIntent(SomeIntent) },
)

// After
PrimaryButton(
    onClick = { onIntent(SomeIntent) },
    enabled = condition,
    loading = loading,
) {
    Text(stringResource(Res.string.xxx))
}
```

- [ ] **Step 1: Migrate all 24 files** — mechanical find-and-replace pattern
- [ ] **Step 2: Build Android** — `./gradlew :androidApp:compileDebugKotlin`
- [ ] **Step 3: Build iOS** — `./gradlew :composeApp:compileKotlinIosArm64`
- [ ] **Step 4: Commit**

```bash
git commit -m "refactor(client): migrate 24 PrimaryButton call sites to gold standard API"
```

---

## Task 14: Migrate YallaClient — All Other Button Call Sites

- [ ] **Step 1: Migrate SecondaryButton** (4 files)
- [ ] **Step 2: Migrate TextButton** (3 files)
- [ ] **Step 3: Migrate BottomSheetButton** (3 files)
- [ ] **Step 4: Migrate GenderButton** (2 files)
- [ ] **Step 5: Migrate NavigationButton** (1 file)
- [ ] **Step 6: Migrate SensitiveButton** (1 file)
- [ ] **Step 7: Migrate IconButton** (if API changed)
- [ ] **Step 8: Build both platforms**

```bash
cd /Users/macbookpro/Ildam/yalla/YallaClient
./gradlew :androidApp:compileDebugKotlin
./gradlew :composeApp:compileKotlinIosArm64
```

- [ ] **Step 9: Commit**

```bash
git commit -m "refactor(client): migrate all remaining button call sites to gold standard API"
```

---

## Task 15: Final Verification

- [ ] **Step 1: Run all SDK tests**

```bash
cd /Users/macbookpro/Ildam/yalla/yalla-sdk && ./gradlew test
```

- [ ] **Step 2: Run YallaClient full build**

```bash
cd /Users/macbookpro/Ildam/yalla/YallaClient && ./gradlew build
```

- [ ] **Step 3: Verify checklist**

```
☑ PrimaryButtonState removed — flat params + content slot
☑ SecondaryButtonState removed — flat params + content slot
☑ TextButtonState removed — flat params + content slot
☑ IconButtonState removed — flat params
☑ SensitiveButtonState removed — flat params
☑ BottomSheetButtonState removed — flat params
☑ GenderButtonState removed — flat params
☑ NavigationButtonState removed — flat params
☑ ButtonLayout shared infrastructure in use by Primary/Secondary/Text/BottomSheet
☑ @Immutable on all Colors/Dimens classes
☑ 3 unused buttons deleted (Gradient, Support, EnableLocation)
☑ All button tests pass
☑ YallaClient builds on Android + iOS
☑ Zero content-state bundling in any button
```

- [ ] **Step 4: Commit and tag**

```bash
cd /Users/macbookpro/Ildam/yalla/yalla-sdk
git commit -m "chore(primitives): complete button gold standard refactoring — Phase 1-3 done"
```
