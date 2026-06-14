# 1. Comments: ban narration, mandate load-bearing documentation

- Status: accepted
- Date: 2026-06-14
- Deciders: yalla-sdk maintainers (design root: A Philosophy of Software Design)

## Context

The yalla codebase has long run a blunt "no inline comments" rule. It is a good
default — most comments in most codebases restate the code, rot, and lie — but it
is too blunt at the edges. Two design authorities pull in opposite directions:

- **Ousterhout (APoSD)** is firmly *pro-comment*. Ch. 12 rebuts the four excuses for
  skipping comments; Ch. 13 argues comments capture the *why* and the abstraction the
  code itself cannot express; he advises writing them first. His own red flag, though,
  is **Comment Repeats Code** (p. 104): a comment whose information is obvious from the
  adjacent code adds nothing.
- **Martin (Clean Code, Ch. 4)** treats a comment as *a failure to express yourself in
  code*: prefer a rename or an extracted function over an explanatory comment; never
  leave commented-out code.

These two are not actually in conflict, and that is the whole point of this ADR. Both
condemn the same thing — comments that paraphrase code — and both bless the same thing —
information the reader genuinely cannot recover from the code. The blunt rule fails
because it also bans the second kind. When it does, the knowledge does not disappear; it
goes back into someone's head and is lost on the next rotation.

We already have proof that the *good* kind earns its place. `media/README.md` §6 does
not narrate the picker code — it records two facts the code cannot show you:

- the **presentation race** (the picker is launched as the photo action sheet is still
  dismissing; we wait on the dismissal's `transitionCoordinator` with a bounded
  next-runloop fallback) — "this is why the gallery now opens reliably instead of
  silently failing to present over a half-dismissed sheet";
- the **delegate-retention** fact (the picker controllers do not retain their delegates,
  so we hold one in `activeDelegate`).

Delete either line and the next reader re-derives a race from a stack trace. That is the
test we are formalizing.

The same standard already lives in the Swift bridge. `YallaClient/iosApp/iosApp/SheetHelper.swift`
keeps exactly two comments in its detent/presentation logic — the next-runloop-tick note
("just enough to exit Compose's measure pass without the full 50ms debounce delay") — and
nothing narrating the `UISheetPresentationController` setup. The PHPicker presentation-race
and custom-detent facts are the canonical platform comments; everything mechanical is left
to read as code. These files are the bar.

## Decision

A comment (inline `//`, KDoc, or DocC) **earns its place if and only if removing it would
force the next reader to reverse-engineer a race, a platform fact, or a non-obvious
contract.** Nothing else.

**1. BAN narration.** A comment that restates what the adjacent code already says is a
defect, not documentation. The fix is never to keep the comment — it is to **rename** the
symbol or **extract** a well-named function until the code says it itself. This is APoSD's
*Comment Repeats Code* and Clean Code's "comments are a failure to express yourself."
Commented-out code is deleted; git remembers.

**2. MANDATE load-bearing documentation**, in exactly three cases:

- **Non-obvious invariants behind a fix** — most often the *why* behind a race or
  ordering fix. The code shows *what* it does; the comment records *what breaks if you
  change it.* (Exemplars: the presentation-race and delegate-retention notes in
  `media/README.md` §6; the runloop-tick note in `SheetHelper.swift`.)
- **Platform facts** — behavior forced by UIKit / Activity / Kotlin/Native / the OS
  pickers that is invisible in the call site. (Exemplars: PHPicker needs no permission;
  a custom detent's height must *exclude* the bottom safe area; `platform.*` APIs break
  `commonMain` portability — already documented in `CorePurityKonsistTest.kt`.)
- **Surprising public-contract behavior** on the most-depended-on SDK surfaces — the
  `expect`/`actual` entry points, the `Factory` interfaces, the `@Composable` public API.
  Document the surprise, not the signature: threading guarantees, cancellation/empty-result
  semantics, ordering, idempotency. (Exemplar: `media/README.md` §7 — "`onResult` is always
  called on the main thread, with an empty list if the user cancels.") If the behavior is
  what any reader would already assume from the types, no KDoc.

**Scope of the mandate.** It is strongest at the public seams every host app depends on
(KDoc on the SDK's exported API and contracts). Inside a module, prefer the README pattern
(`media/`, `components/`) for the *why*, and reserve inline comments for the rare local
race/platform fact that has no natural home in prose.

**The one test.** Before writing a comment: *if I delete this, does the next reader have to
re-derive a race, a platform fact, or a non-obvious contract?* If yes, keep it and make it
carry that load. If no, delete it and fix the code instead.

## Consequences

**Positive**

- Resolves the standing APoSD-vs-Clean-Code tension with a single, testable rule both
  authorities actually agree on. The blunt "no comments" rule is replaced, not weakened.
- The expensive knowledge — why a race fix is shaped the way it is, which platform quirk
  forced a workaround, what a contract really promises — stops living only in the author's
  head.
- Keeps the diff signal high: every surviving comment is load-bearing, so reviewers can
  trust that a comment marks something genuinely non-obvious rather than skim past noise.

**Negative / costs**

- "Non-obvious" is a judgment call. We accept reviewer judgment as the arbiter rather than
  a mechanical line count. A comment that *paraphrases* the code is the bright-line failure;
  borderline cases default to deletion (smallest surviving footprint).
- Load-bearing comments must be maintained with the code they explain — a stale race comment
  is worse than none. Treat a change to a race/platform/contract behavior as also requiring
  its comment to change, the same way `components/README.md` is bound to its Konsist rule.

**Enforcement**

- Code review is the primary gate. A comment that restates code is a requested change; a
  missing contract note on a public seam is a requested change.
- A Konsist rule MAY later flag obvious narration (e.g. comments that are a verbatim echo of
  the next line) on the public API surface, mirroring the existing
  `ComponentShapeKonsistTest` / `CorePurityKonsistTest` pattern. Not required by this ADR.
