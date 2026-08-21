---
name: android-frontend-design
description: Guidance for distinctive, intentional visual design when building or reshaping Jetpack Compose / Material 3 UI. Use for any screen design, component styling, or visual-identity decision — not just first builds.
---

# Android Frontend Design — Tour Cost Tracker

Approach every screen as the design lead at a small studio, not a template-filler. This is a **money app used by groups of friends mid-trip** — the design has one job above all others: make people trust the numbers instantly and never hesitate about what an action will do to their balance. Distinctiveness serves that trust, it isn't decoration on top of it.

## Ground it in the subject

Before styling anything, name what this specific screen's single job is (e.g. "the Balance screen's job is to make net-owed amounts scannable in under 2 seconds"). Don't reach for the same card-with-shadow, same rounded-corner button, same default Material 3 baseline theme you'd produce for any other app. The subject here — shared costs, IOUs between named people, a currency symbol (৳) — is where distinctive choices come from. Build with real expense data (hotel bills, restaurant splits, ৳ amounts) in every mockup, never lorem ipsum or "Item 1 / Item 2."

## Design principles for this app

**Money is typography's job.** Amounts are the most-read element on every screen. Give them their own type role — a numeric/tabular-figure face, distinct weight, distinct size — so a ৳ figure is recognizable at a glance without reading the label next to it. Never let an amount share visual weight with a caption or timestamp.

**Positive and negative balance need instant, colorblind-safe distinction.** "You'll receive ৳3,000" and "You owe ৳1,500" must be told apart by more than red/green alone — pair color with a directional glyph, a sign, or distinct iconography. This is not optional polish; it's the app's core comprehension task.

**Personal vs. shared expenses need a visual tag, not just a details-screen mention.** Since personal expenses don't affect settlement, the expense list needs a lightweight, consistent badge (not a full-color card change that competes with the amount) so a member scanning the list never mistakes a personal purchase for a shared debt.

**Structure encodes real state, not decoration.** A tour's lifecycle (Active / Settled / Archived) is real state that changes what a user can do — surface it as a persistent, unmissable status affordance, not a subtle text label. When a tour is Archived, every disabled control should *look* disabled at a glance (not just fail silently on tap), because trust in a money app depends on the UI matching reality exactly.

**Motion serves confirmation, not delight.** The moments that deserve real motion: marking a settlement paid, an expense successfully added, a balance updating live as a split is edited. These are trust moments — a satisfying, brief confirmation reduces "did that actually save?" anxiety. Skip motion everywhere else; a group-finance app that feels playful in the wrong places reads as untrustworthy with people's money.

**Match Material 3 restraint to the subject.** This isn't a maximalist consumer app — lean toward a calm, legible, slightly editorial system (clear type hierarchy, generous spacing around amounts, restrained color used purposefully for balance state) over heavy Material defaults (default purple dynamic color, default elevation shadows everywhere, default rounded-everything). One signature element — e.g. a distinctive way the Settlement screen visualizes "who pays whom" — can be the memorable part; keep the rest quiet and disciplined.

## Process: plan, critique, build, critique again

Before writing Compose code for a new screen or component:

1. **Token pass** — define (or reuse from `ui/theme/`) a compact system: color (named roles: balance-positive, balance-negative, personal-tag, shared-tag, archived-state — not raw hex scattered in composables), type (a numeric/amount style, a body style, a label/caption style), spacing scale, and one signature element for this screen.
2. **Check against the app, not the platform default** — would this composable be indistinguishable from a generic Material 3 sample app? If yes, revise: what does *this* screen need to say about money that a generic list/card doesn't?
3. **Build to the plan**, deriving every color/type/spacing decision from the token pass rather than ad-hoc values inline.
4. **Self-critique before showing it** — screenshot or describe the render, check: is the amount the most legible thing on screen? Is positive/negative balance unambiguous? Does disabled (Archived) state look disabled? Is this accessible (content descriptions on icons, sufficient contrast, touch targets ≥48dp, TalkBack-friendly ordering)? Does it degrade gracefully on a small phone screen (test at a narrow width, not just a big preview)?

## Restraint

Spend boldness in one place per screen. Let the signature element (e.g. the Settlement screen's payment-flow visualization) be the memorable thing; keep list rows, form fields, and navigation quiet and consistent across the whole app so the app feels like one coherent product, not a set of individually-styled screens. Build to a quality floor without announcing it: works at default and large font scale (Android accessibility text sizing), visible focus states for external keyboard/D-pad navigation, reduced-motion respected if the system setting is on.

## Writing in this app

Words here are financial instructions, not marketing copy — treat them with the same care as amounts.

- Name actions by what the person is doing, not the system mechanism: "Mark as paid," not "Submit settlement." "Split equally," not "Apply equal allocation."
- Keep a button's label consistent through the whole flow: a button that says "Add Expense" should produce a confirmation that says "Expense added," not "Success."
- Never soften or hedge a money statement. "You owe ৳1,500" — not "You may owe around ৳1,500." Precision is what makes people trust the app with shared money.
- Empty states are an invitation to act: an empty expense list says "No expenses yet — add the first one," not just "No data."
- Errors state what happened and what to do, in plain terms: "Couldn't save — check your connection and try again," not a stack trace or generic "Something went wrong."
