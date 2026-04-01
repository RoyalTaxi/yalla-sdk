# Component Guide

> Step-by-step guide for building a new UI component. Follow this exactly.

For the complete specification, see [`COMPONENT_STANDARD.md`](../COMPONENT_STANDARD.md).
This page is the practical, condensed version.

## Step 1: Decide Where It Goes

| If the component... | Put it in... |
|---------------------|-------------|
| Is a basic building block (button, field, indicator) | `primitives/` |
| Composes from other SDK components (card, sheet, item) | `composites/` |
| Requires platform-native UI (bottom sheet, switch) | `platform/` |

## Step 2: Create the File Structure

```kotlin
// File: {module}/src/commonMain/kotlin/uz/yalla/{module}/{category}/{Component}.kt
package uz.yalla.{module}.{category}
```

## Step 3: Define Colors and Dimens

```kotlin
/**
 * Color configuration for [MyComponent].
 *
 * @property container Background color of the component.
 * @property content Text/icon color.
 * @since 0.0.X
 */
@Immutable
data class MyComponentColors(
    val container: Color,
    val content: Color,
)

/**
 * Dimension configuration for [MyComponent].
 *
 * @property height Component height.
 * @property shape Corner shape.
 * @property contentPadding Inner padding.
 * @since 0.0.X
 */
@Immutable
data class MyComponentDimens(
    val height: Dp,
    val shape: Shape,
    val contentPadding: PaddingValues,
)
```

**Rules:**
- Always `@Immutable`
- Always `data class`
- Every property has `@property` KDoc

## Step 4: Create the Defaults Object

```kotlin
/**
 * Default styling for [MyComponent].
 *
 * @since 0.0.X
 */
object MyComponentDefaults {
    /**
     * Default colors reading from the current theme.
     *
     * @param container Background color. Defaults to [System.color.background.secondary].
     * @param content Content color. Defaults to [System.color.text.base].
     */
    fun colors(
        container: Color = System.color.background.secondary,
        content: Color = System.color.text.base,
    ) = MyComponentColors(
        container = container,
        content = content,
    )

    /**
     * Default dimensions.
     *
     * @param height Component height. Defaults to 56.dp.
     * @param shape Corner shape. Defaults to RoundedCornerShape(16.dp).
     */
    fun dimens(
        height: Dp = 56.dp,
        shape: Shape = RoundedCornerShape(16.dp),
        contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    ) = MyComponentDimens(
        height = height,
        shape = shape,
        contentPadding = contentPadding,
    )
}
```

**Rules:**
- `colors()` reads from `System.color.*` — never hardcoded hex
- `dimens()` is NOT `@Composable` (dimensions don't read theme)
- Every parameter has a KDoc `@param`

## Step 5: Write the Composable

```kotlin
/**
 * Brief description of what this component does.
 *
 * ## Usage
 * ```kotlin
 * MyComponent(
 *     onClick = { /* handle click */ },
 *     modifier = Modifier.fillMaxWidth(),
 * ) {
 *     Text("Content")
 * }
 * ```
 *
 * @param onClick Called when the component is clicked.
 * @param modifier Modifier applied to the root container.
 * @param enabled Whether the component is interactive.
 * @param colors Color configuration. Defaults to [MyComponentDefaults.colors].
 * @param dimens Dimension configuration. Defaults to [MyComponentDefaults.dimens].
 * @param content The content displayed inside the component.
 * @see RelatedComponent
 * @since 0.0.X
 */
@Composable
fun MyComponent(
    // 1. REQUIRED (no defaults)
    onClick: () -> Unit,

    // 2. MODIFIER (always second)
    modifier: Modifier = Modifier,

    // 3. BEHAVIORAL (optional behavior flags)
    enabled: Boolean = true,

    // 4. STYLING (colors, dimens)
    colors: MyComponentColors = MyComponentDefaults.colors(),
    dimens: MyComponentDimens = MyComponentDefaults.dimens(),

    // 5. CONTENT (trailing lambda)
    content: @Composable () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(dimens.height),
        enabled = enabled,
        shape = dimens.shape,
        colors = CardDefaults.cardColors(containerColor = colors.container),
    ) {
        Box(
            modifier = Modifier.padding(dimens.contentPadding),
            contentAlignment = Alignment.CenterStart,
        ) {
            content()
        }
    }
}
```

**Parameter order is mandatory:**
1. Required parameters (no defaults)
2. `modifier: Modifier = Modifier`
3. Behavioral (enabled, loading, etc.)
4. Styling (colors, dimens)
5. Slots/content (trailing lambda)

## Step 6: Write Tests

```kotlin
// File: {module}/src/commonTest/kotlin/uz/yalla/{module}/{category}/MyComponentTest.kt

class MyComponentTest {
    @Test
    fun colorsShouldImplementEquality() {
        val a = MyComponentColors(container = Color.White, content = Color.Black)
        val b = MyComponentColors(container = Color.White, content = Color.Black)
        assertEquals(a, b)
    }

    @Test
    fun dimensShouldImplementEquality() {
        val a = MyComponentDimens(height = 56.dp, shape = RoundedCornerShape(16.dp), ...)
        val b = MyComponentDimens(height = 56.dp, shape = RoundedCornerShape(16.dp), ...)
        assertEquals(a, b)
    }

    @Test
    fun colorsShouldSupportCopy() {
        val original = MyComponentDefaults.colors()
        val modified = original.copy(container = Color.Red)
        assertNotEquals(original.container, modified.container)
    }
}
```

## Step 7: Update Documentation

1. Add to the module's `MODULE.md`
2. If it's a new category, add a `# Package` section
3. Update `SDK_STATUS.md` if relevant

## Checklist

Before submitting:
- [ ] Parameter order: required → modifier → behavioral → styling → content
- [ ] `@Immutable` on Colors and Dimens classes
- [ ] Colors from `System.color.*`, not hardcoded
- [ ] Fonts from `System.font.*`, not hardcoded
- [ ] Modifier applied to root only
- [ ] KDoc with `@param`, `@since`, `@see`
- [ ] Tests for Colors/Dimens equality
- [ ] Added to MODULE.md
