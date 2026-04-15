---
name: audit-api
description: Audit the public API surface of yalla-sdk modules and produce a diff against the last published version. Used as the fallback for apiCheck since binary-compatibility-validator is not currently wired up. Triggers include phrases like "audit API", "what changed in API", "check api compatibility", "apicheck", "public api diff".
allowed-tools: Read, Bash, Glob, Grep
---

# Audit Public API

Produce a report of what changed in the public API surface of yalla-sdk modules since the last release.

## Important: apiCheck Is Not Wired Up

The `org.jetbrains.kotlinx.binary-compatibility-validator` plugin is **not currently applied** in this repo. `./gradlew apiCheck` and `./gradlew apiDump` do not exist.

Confirm before starting:
```bash
./gradlew :<module>:tasks --all 2>&1 | grep -iE "(apiCheck|apiDump)" || echo "not wired up"
```

If the grep is empty, fall back to manual diffing (steps below). If/when the validator is wired up, this skill should switch to running `./gradlew apiCheck` as the primary mechanism.

## Process

### 1. Scope

Ask (or default to all):
- Single module, or all modules?
- Compare against: last published version tag, `main` HEAD, or a specific SHA?

### 2. Find the Baseline

If comparing against the last published version, find the last tag or commit that represents the last release. The repo doesn't use per-module tags — it uses a single global tag (or just main's publish commits):

```bash
git log --oneline --all --grep="bump SDK version" | head -10
git log --oneline --all -- gradle.properties | head -10
```

Pick the SHA of the most recent `yalla.sdk.version` bump that matches the last published version (cross-check against `gh api "/orgs/RoyalTaxi/packages/maven/uz.yalla.sdk.<module>/versions"` if needed).

### 3. Manual Public Surface Diff

For each module being audited:

```bash
# Diff all Kotlin source from baseline to HEAD
git diff <baseline-sha>..HEAD -- '<module>/src/commonMain/**/*.kt' '<module>/src/androidMain/**/*.kt' '<module>/src/iosMain/**/*.kt'
```

Walk the diff. Focus only on declarations with `public` visibility (Kotlin default visibility is `public`). Ignore `internal`, `private`, and `@InternalYallaApi`-annotated declarations.

Tools that help:
```bash
# Find all public declarations in a module at HEAD
grep -rn --include='*.kt' -E '^\s*(public\s+)?(class|interface|object|fun|val|var|typealias)' <module>/src/commonMain/
```

### 4. Classify Changes

For each changed declaration, classify:

| Change | Severity |
|--------|----------|
| New `public` class/interface/fun/property added | **Additive** |
| `public` removed or changed to `internal` | **BREAKING** |
| Parameter added without default | **BREAKING** |
| Parameter added with default value | **Additive** (source-compatible, but Kotlin consumers still need to recompile) |
| Parameter removed | **BREAKING** |
| Return type changed | **BREAKING** |
| Nullability changed (`String` → `String?` or vice versa) | **BREAKING** |
| `enum` value added | **Minor-breaking** (consumer `when` becomes non-exhaustive) |
| `enum` value removed | **BREAKING** |
| `sealed` hierarchy gains a subtype | **Minor-breaking** (consumer `when` breaks) |
| `sealed` hierarchy loses a subtype | **BREAKING** |
| `data class` primary constructor changed | **BREAKING** (copy() signature shifts) |
| KDoc / comment change only | **Patch** |
| Internal refactor with identical public surface | **Patch** |

### 5. Report

```
# API Audit — <module> (baseline <sha-or-version> → HEAD)

## Summary
- Files touched: N
- Public-surface changes: N
- Breaking: B
- Minor-breaking: M
- Additive: A

## Breaking Changes (require major/pre-release bump with reset)
1. `public fun foo(x: Int)` → `public fun foo(x: String)` — file:line
2. ...

## Minor-Breaking (sealed/enum additions)
1. `enum class Status` added `IN_REVIEW` — file:line
2. ...

## Additive Changes (safe in pre-release increment)
1. `public class NewThing` at file:line
2. ...

## Recommendation
Bump level:
- If any BREAKING → patch segment bump + reset to alpha01 (per docs/04-PUBLISHING.md)
- Otherwise → pre-release alpha increment (alphaNN → alphaNN+1)
```

### 6. Next Steps for Islom

- If breaking: recommend writing an ADR in `docs/06-DECISIONS.md` before calling `bump-version`
- Otherwise: call `bump-version` to increment the alpha
- CI will publish after push to main

## Non-goals

- Do NOT modify any source files — this skill is read-only
- Do NOT wire up `binary-compatibility-validator` yourself — if Islom wants it, he'll open a separate task for it
- Do NOT bump versions — that's `bump-version`'s job
- Do NOT publish — CI does that automatically after the version bump lands on main
