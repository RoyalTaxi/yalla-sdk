---
paths:
  - "**/src/commonMain/**/*.kt"
  - "**/src/androidMain/**/*.kt"
  - "**/src/iosMain/**/*.kt"
---

# Library Public API Rules

This is a **published library**, not an application. Anything visible to consumers is public API and subject to binary compatibility rules.

## Visibility Discipline

- Default to `internal`. Make things `public` only when consumers genuinely need them
- `internal` declarations in `commonMain` are still visible across the module's targets — if you don't want cross-target visibility, use `private`
- Never use wildcard imports (`import foo.*`) in published API — they break binary compatibility if the star-imported package changes

## Stability Annotations

- **Stable public API**: no annotation needed, but must have KDoc and remain binary-compatible
- **Experimental**: gate with `@RequiresOptIn` marker:
  ```kotlin
  @RequiresOptIn(message = "This API is experimental and may change.", level = RequiresOptIn.Level.WARNING)
  @Retention(AnnotationRetention.BINARY)
  annotation class ExperimentalYallaApi
  ```
- **Internal-to-Yalla**: gate with `InternalYallaApi` marker. Consumers shouldn't call it, and if they do, it's at their own risk

## KDoc Requirements

Every public declaration on a published module MUST have KDoc:
- Class/interface: one paragraph explaining purpose + usage
- Function: one line + `@param`/`@return`/`@throws` if non-trivial
- Property: one line unless self-explanatory from the name

No KDoc = reject in code review.

## Breaking Changes

A breaking change is ANY of:
- Removing a public declaration
- Changing a public function signature (parameters, return type)
- Changing a public property type
- Making a non-nullable type nullable or vice versa (consumer-visible)
- Changing a visibility from `public` to anything else
- Removing or changing the type parameter of a generic class
- Reordering `enum` values (ordinal-sensitive consumers break)
- Changing a `data class`'s primary constructor (copy() signature changes)
- Changing the serializable shape of a `@Serializable` class

If you're making a breaking change:
1. Stop and confirm it's genuinely necessary
2. Bump the module's major version (1.x.x → 2.x.x)
3. Add an ADR in `docs/06-DECISIONS.md` explaining why and how consumers migrate
4. Add migration notes to the module's `CHANGELOG.md` (or create one)
5. Update `yalla-bom` to track the new major version

## Non-Breaking Additive Changes

These are safe and only need a minor version bump:
- Adding new public declarations
- Adding optional parameters with default values to functions (Kotlin-side; but note: binary compatibility requires `@JvmOverloads` for Java consumers — we're KMP-only, so this is usually fine)
- Adding new members to a sealed hierarchy (if `when` exhaustiveness is acceptable to break)

Caution: adding a member to a sealed class forces consumers to update their `when` expressions. Treat as a minor-breaking change and bump minor, not patch.

## Bug Fixes

Patch version bump. Pure bug fixes with no API surface change.

## `expect`/`actual` Rules

- Every `expect` declaration must have an `actual` in every target (Android, iOS)
- Keep `expect` surface small — if a platform-specific API is large, wrap it internally and only expose the wrapper as expect
- `actual` implementations in `androidMain` and `iosMain` must follow the same nullability and visibility as `expect`
- Never put business logic in `expect`/`actual` — those are for platform bridges only

## Coroutine Scope

Library functions that launch coroutines must accept a `CoroutineScope` or be `suspend`. Never launch into `GlobalScope` or create unmanaged scopes inside library code — the consumer owns lifecycle.
