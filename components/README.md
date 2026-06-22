# `uz.yalla.sdk:components`

The Yalla design-system components: buttons, fields, chips, cards, items, pins, sheets,
feedback. Cross-platform Compose Multiplatform; the public API is `@Composable` and identical
on Android and iOS.

This doc defines **one thing only**: the *shape* a configurable component takes — the
`Colors` / `Dimens` / `Styles` holders, the `Defaults` object, and the composable's parameter
list. It is the convention that the rest of the file (layout, behaviour) is free to vary around.

A Konsist rule (`:konsistTest` → [
`ComponentShapeKonsistTest`](../konsistTest/src/test/kotlin/uz/yalla/konsist/ComponentShapeKonsistTest.kt))
enforces the load-bearing parts of this shape in CI. **If you change the convention, change the
rule and this doc together.**

---

## 1. Why this convention exists

Every component that lets the caller restyle it grew the same three concerns, by hand, in ~15
files: a `Colors` holder, a `Dimens` holder, a `Styles` holder, and a `Defaults` object that
builds each from theme tokens. Because it was hand-copied, it drifted:

- some `Defaults` objects exposed `dimens()` but no `colors()` even though a `*Colors` holder
  existed (or the holder existed with no factory to build it),
- some components stopped at `Colors` and never grew `Dimens`/`Styles`,
- the parameter order and defaulting (`colors = XDefaults.colors()`) varied file to file.

Drift is the cost: a caller who learns one component can't predict the next, and a holder with
no matching `Defaults` is a dead-end the compiler won't catch. The fix is to **write the shape
down once and let a test guard it**, not to police it in review.

> **A component's *configuration surface* has a fixed shape. Its *implementation* does not.**

---

## 2. The shape

For a public component named `X`, the configuration surface is:

```
@Immutable public data class XColors(…)       // the resolved values the composable reads
@Immutable public data class XDimens(…)        // shapes, sizes, spacing, padding
@Immutable public data class XStyles(…)         // TextStyles

public object XDefaults {                       // the ONE place theme tokens enter
    @Composable public fun colors(…): XColors = …
    @Composable public fun dimens(…): XDimens = …
    @Composable public fun styles(…): XStyles = …
}

@Composable public fun X(
    …,                                          // semantic params (text, onClick, state, slots)
    colors: XColors = XDefaults.colors(),       // each holder defaulted from its factory
    dimens: XDimens = XDefaults.dimens(),
    styles: XStyles = XDefaults.styles(),
    …
)
```

```
   theme tokens (uz.yalla.design.System.*)
                 │  resolved only here
                 ▼
        ┌──────────────────┐      builds      ┌──────────┐  ┌──────────┐  ┌──────────┐
        │   XDefaults      │ ───────────────▶ │ XColors  │  │ XDimens  │  │ XStyles  │
        │  colors()        │                  │@Immutable│  │@Immutable│  │@Immutable│
        │  dimens()        │                  └────┬─────┘  └────┬─────┘  └────┬─────┘
        │  styles()        │                       │             │             │
        └──────────────────┘                       └──────┬──────┴──────┬──────┘
                                                          │ passed in    │
                                                   ┌──────▼──────────────▼──────┐
                                                   │  @Composable fun X(…)       │
                                                   │  reads holders, never tokens│
                                                   └─────────────────────────────┘
```

`PrimaryButton.kt` and `SecondaryButton.kt` are the reference implementations — copy their shape.

### Rules of the shape

1. **Holders are `@Immutable public data class`.** `@Immutable` so Compose can skip; `data class`
   so callers get `copy()` to tweak one field.
2. **`Defaults` is the only place `uz.yalla.design.System.*` (theme tokens) is read.** Holders
   carry already-resolved `Color` / `Dp` / `TextStyle`; the composable reads holders, never tokens.
   This is what makes a component themeable and previewable in isolation.
3. **A holder and its factory come as a pair.** If `XColors` exists, `XDefaults.colors()` must
   build it; if `XDefaults.colors()` exists, `XColors` must exist. Same for `Dimens`/`Styles`.
   A holder with no factory is unreachable; a factory with no holder can't compile.
4. **Each factory parameter defaults to a token** so `XDefaults.colors()` is a complete, callable
   default, and callers override only what they need (`XDefaults.colors(containerColor = …)`).
5. **The composable defaults each holder param to its factory:**
   `colors: XColors = XDefaults.colors()`.

### What is *not* mandatory

A component only needs the holders for axes it actually exposes. **`Dimens`-only is valid**
(`BonusCard`, `CarPaletteCard` — they let callers tune shape/padding but not colours). The
convention is "holders pair with their factory", **not** "every component has all three". Add an
axis when a real caller needs it, not pre-emptively.

---

## 3. What the Konsist rule guards

[
`ComponentShapeKonsistTest`](../konsistTest/src/test/kotlin/uz/yalla/konsist/ComponentShapeKonsistTest.kt)
scans `uz.yalla.components..` and fails the build on the two drift modes that bit us:

| Rule                                   | Fails when                                                                                         | Catches                                      |
|----------------------------------------|----------------------------------------------------------------------------------------------------|----------------------------------------------|
| `everyColorsHolderHasMatchingDefaults` | a `*Colors` data class has no sibling `*Defaults` object                                           | a holder wired to nothing — the dead-end     |
| `everyHolderHasItsDefaultsFactory`     | a `*Colors`/`*Dimens`/`*Styles` holder lacks its `Defaults.colors()`/`dimens()`/`styles()` factory | the "Defaults miss Colors/Styles" half-pairs |

Both are **structural** — they assert the shape exists, not what's inside it. They start narrow
(`Colors`-anchored, plus holder→factory pairing) on purpose; tighten them as the convention
earns more teeth, but only with a green run and a matching edit here.

Run locally:

```
./gradlew :konsistTest:test
```

It already runs in the required CI check, so a drift PR goes red before review.
