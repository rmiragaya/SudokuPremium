Sudoku Premium - Agent Instructions
Project Context
Sudoku Premium is an Android Sudoku app focused on calm, logic-first solving. The product direction is: "pensado para resolver, no adivinar". Hints should explain the reasoning behind each move, and the app should feel polished, quiet, and professional.
The long-term goal is to make this app good enough to publish on the Play Store and strong enough to show as a serious portfolio project. Treat the codebase as a reflection of the author's engineering standards: stable behavior, thoughtful UX, clear architecture, maintainable Kotlin, and visible attention to detail matter more than quick demos. Monetization may be considered later, but the current priority is quality, credibility, and polish.
The project is a single Android app module:
Root project: `SudokuPremium`
App module: `:app`
Namespace/code package: `ropa.miragaya.sudokupremium`
Published applicationId: `ropa.miragaya.sudokumentor`
Main UI: Jetpack Compose + Material 3
Navigation: Navigation Compose with typed serializable routes
DI: Hilt
Persistence: Room
Serialization/helpers: kotlinx.serialization and Gson
Lint/format: ktlint Gradle plugin
Commands
Run Gradle with the local project cache:
```powershell
$env:GRADLE_USER_HOME = Join-Path (Get-Location) ".gradle"; .\gradlew.bat ktlintCheck
$env:GRADLE_USER_HOME = Join-Path (Get-Location) ".gradle"; .\gradlew.bat testProdDebugUnitTest
$env:GRADLE_USER_HOME = Join-Path (Get-Location) ".gradle"; .\gradlew.bat assembleProdDebug
```
Preferred validation order after meaningful changes:
`ktlintCheck`
`testProdDebugUnitTest`
`assembleProdDebug`
For UI work, install/run the debug build on an emulator or device when practical and capture screenshots if visual behavior matters.
Release Identity
The Play Store/Firebase/AdMob/Billing identity is the published `applicationId`, currently `ropa.miragaya.sudokumentor`.
Do not change `applicationId` casually: after internal testing/publication it represents the app identity in Play Console and related Google services.
The Kotlin namespace/package remains `ropa.miragaya.sudokupremium`; this difference is intentional.
Firebase environments use product flavors:
- `dev`: `applicationId` = `ropa.miragaya.sudokumentor.dev`, app name `Sudoku Mentor Dev`, Firebase Dev.
- `prod`: `applicationId` = `ropa.miragaya.sudokumentor`, app name `Sudoku Mentor`, Firebase/Play production.
Use `prodRelease` for Play Console builds.
Architecture Notes
Domain models and solver logic live under `app/src/main/java/ropa/miragaya/sudokupremium/domain`.
Sudoku solving strategies live under `domain/solver/strategies`.
Hint text and reasoning context live around `domain/model/StrategyContext.kt` and `domain/model/SudokuHint.kt`.
Technique tutorial fixtures live under `domain/techniques`.
Game UI and state live under `ui/game`.
Home UI lives under `ui/home`.
Technique library UI lives under `ui/techniques`.
Navigation routes live in `ui/navigation/Routes.kt`.
Keep solver/domain behavior testable and deterministic where possible. Avoid hiding solver failures with fallbacks that produce invalid game state.
Product/UI Guidelines
The app should feel calm, deliberate, and logic-oriented.
Prefer clear explanations over terse technical wording.
Hint text should refer to highlighted cells when possible instead of repeating long "fila X, columna Y" descriptions.
Use Spanish copy with correct accents.
Do not add busy, marketing-like UI to the game experience.
Keep the home screen simple, centered, and polished.
For the board, protect readability first: hints, overlays, and controls must not cover important cells.
Technique tutorial boards must have clear 3x3 box separation and examples must match the represented strategy.
Kotlin/Android Guidelines
Follow existing Compose patterns before introducing new abstractions.
Prefer immutable state updates through `copy`.
Keep ViewModels free of UI-only formatting when practical.
Avoid `!!`; prefer safe lookups and explicit fallback behavior.
Avoid production-only side effects in domain code. Debug logs should be gated behind debug behavior.
Do not use destructive Room migrations in release builds.
Do not leave debug boards enabled by default.
Keep tests focused around solver correctness, generation, hint behavior, and tutorial fixture validity.
Git And Safety
Do not revert user changes unless explicitly asked.
Do not run destructive git commands such as `git reset --hard` or `git checkout --` without explicit approval.
Do not commit, push, or open a PR unless the user asks.
Before a final answer after code changes, mention which validations were run and whether they passed.
