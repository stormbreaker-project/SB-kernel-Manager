# Architecture

SB Kernel Manager is a companion app for Team StormBreaker's custom kernels.
It ships in four phases — companion hub, OTA updater, kernel monitor, kernel
manager — and the structure here exists so later phases can be added without
disturbing earlier ones.

## Module graph

```
                    :app
                     |
   +-----------------+------------------+
   |                                    |
:feature:*                        :core:designsystem
   |                                    |
:core:di  ---->  :core:data  ---->  :core:network
                  |      |               |
          :core:datastore |         :core:model
                          |
                    :core:common
```

Dependencies point one way only. A feature cannot reach the HTTP client, and
`:app` cannot either — both are compile errors, not conventions.

| Module | Holds | Depends on |
|---|---|---|
| `:app` | `Application`, `MainActivity`, nav host, nav bar | every feature, `:core:designsystem`, `:core:di` |
| `:core:model` | Domain types. Plain Kotlin. | — |
| `:core:common` | `DataResult`, `LoadError`. Plain Kotlin. | — |
| `:core:network` | Ktor client, wire DTOs, endpoints | `:core:model` |
| `:core:datastore` | Preference storage | `:core:model` |
| `:core:data` | Repositories | `:core:network`, `:core:datastore` |
| `:core:di` | `AppContainer`, `AppContainerOwner` | `:core:data` |
| `:core:designsystem` | Brand palette, type, shared components | `:design-system` (PvotLib) |
| `:feature:*` | One screen each | `:core:designsystem`, `:core:di`, `:core:model`, `:core:common` |

## Rules

**Features never depend on each other.** Each owns its route and a
`NavGraphBuilder` extension; `:app` composes them. Anything cross-feature —
More opening Licenses — is a callback passed in from `:app`.

```kotlin
// feature/licenses
@Serializable data object LicensesRoute
fun NavGraphBuilder.licensesScreen(contentPadding: PaddingValues)
fun NavController.navigateToLicenses(navOptions: NavOptions? = null)

// app
moreScreen(contentPadding, onOpenLicenses = { navController.navigateToLicenses() })
```

Adding a screen is: new module on `sbkm.android.feature`, declare its route,
add one line to `SBNavHost` and one to `settings.gradle.kts`.

**Errors are types, not strings.** Repositories return `DataResult`; turning
`LoadError.OFFLINE` into words is the UI's job, so it stays translatable.

**Wire types are not domain types.** DTOs live in `:core:network` and map to
`:core:model`. Decoding ignores unknown keys, because the website deploys
independently of installs in the field.

**Strings live in the module that shows them.** Cross-module resources are
reached by aliased import (`core.designsystem.R as DesignSystemR`).

## Dependency injection

Hand-rolled: `AppContainer` in `:core:di`, held by `SBApplication` through
`AppContainerOwner`. ViewModel factories resolve it with `appContainer()`.

The graph is a handful of lazy singletons with no scoping, which Hilt would
not simplify — it would add KSP to the build for no gain yet. It lives in
`:core:di` rather than `:app` because features resolve from it and cannot
depend on the application module. If the graph grows scopes or variants, this
is the seam to replace.

## Build

`build-logic` is an included build of convention plugins, so `android {}` is
written once:

| Plugin | For |
|---|---|
| `sbkm.android.application` | `:app` |
| `sbkm.android.library` | Android core modules |
| `sbkm.android.compose` | anything with UI |
| `sbkm.android.feature` | features — library + compose + the usual deps |
| `sbkm.jvm.library` | plain Kotlin modules |

Two things needed pinning: the daemon toolchain, because the system JVM ships
no compiler, and Kotlin's `jvmTarget` on JVM modules, which otherwise follows
the daemon and disagrees with Java.

## Versioning

The app is semantically versioned. `app` in `gradle/libs.versions.toml` is the
only place it is written:

```toml
app = "0.1.0"     # -> versionName "0.1.0", versionCode 100
```

`versionCode` is derived rather than maintained alongside it — two digits each
for minor and patch, so `1.2.3` becomes `10203` and integer ordering matches
semantic ordering. Keeping the two in step by hand is how a release ends up
unable to install over its predecessor.

Pre-release suffixes are rejected by the parser. `0.1.0-beta.1` and `0.1.0`
would derive the same `versionCode`, so the release could not be installed over
the beta. A malformed or out-of-range version fails the build with the reason
rather than producing a wrong code.

## Quality gates

| Tool | Owns |
|---|---|
| ktlint | formatting, from `.editorconfig` |
| detekt | Kotlin static analysis, `config/detekt/detekt.yml` |
| compose-rules | Compose conventions, as a detekt ruleset |
| Android Lint | platform correctness, via `build` |
| REUSE | licensing, `REUSE.toml` |

detekt runs on 2.0 (`dev.detekt`), which requires Kotlin 2.4. The two versions
are coupled and must move together:

```toml
detekt        = "2.0.0-alpha.6"   # dev.detekt namespace
detektCompose = "0.6.4"           # built against detekt 2.0.0-alpha.6
```

**compose-rules must match the detekt major version.** Releases before 0.5.9
target detekt 1.x; from 0.5.9 they target `dev.detekt` 2.x. A mismatched pair
does not fail — it loads far enough to validate rule ids in config and then
never executes a rule, which reads as "clean" and is not. When changing either
version, verify by planting a violation:

```kotlin
@Composable
fun Probe(label: String) { Text(label) }   // ModifierMissing
```

If detekt does not report it, the ruleset is not running.

Module boundaries are still enforced by the compiler and the dependency graph
rather than by a rule: `:app` cannot see Ktor, and no feature can see
`:core:network`. Nothing yet stops one feature depending on another.

## Backend

There is no server. The website publishes static JSON with its own deploy and
the app reads it:

| Endpoint | Feeds |
|---|---|
| `/api/v1/news.json` | the newsroom |
| `/api/v1/links.json` | More — community, contribute, project, legal |

Paths are versioned because installed apps are not updated in step with the
site. Breaking changes go to `v2` while `v1` keeps serving old installs.
`BASE_URL` is a `BuildConfig` field so a debug build can point at staging.

Links also ship bundled in `:core:data` assets, so More works on first run with
no network. That copy is allowed to go stale; the network copy wins whenever it
is reachable.

## Design system

`PvotLib` is vendored as a git submodule and included as `:design-system`. It
holds what any Pvot app would want — the nav bar, motion tokens, `pvotReveal`,
`pvotPressScale`. `:core:designsystem` holds what is StormBreaker's: the
palette mirroring the website's `tokens.css`, the type scale, and components
still settling.

Material You is deliberately off. The kernel, the site and the app are one
product with one identity, and a wallpaper-derived palette would put unreviewed
colours behind readouts where colour carries meaning.

## Compose API conventions

The app follows the [Compose API guidelines][compose-api]. Three decisions there
are worth recording, because each looks like a violation until the reasoning is
attached.

**Modifiers are extension functions.** `pvotReveal` and `pvotPressScale` are
declared as `fun Modifier.x(): Modifier`, not as standalone functions returning a
`Modifier`. The guideline requires the factory form, and it is the difference
between `modifier.pvotReveal(0).pvotPressScale(source)` and a call site wrapping
every effect in `.then(...)`. A conditional effect still needs `.then()`, because
there is nothing to chain onto when the condition is false:

```kotlin
.then(if (onClick != null) Modifier.pvotPressScale(interactionSource) else Modifier)
```

**Stability is declared in a file, not with annotations.** Every domain model
carries a `List`, so the compiler infers it unstable. Under strong skipping an
unstable parameter is compared with `===` rather than `==`, and since view models
rebuild their state object on every emission, reference comparison always misses
and the subtree recomposes even when nothing changed. `config/compose/stability.conf`
declares the affected classes stable; `@Immutable` would have forced a Compose
dependency into `:core:model`, which is a `sbkm.jvm.library` with none.

Do not take a stability claim on trust — the compiler will tell you what it
actually inferred:

```
./gradlew :feature:news:clean :feature:news:compileDebugKotlin -PcomposeMetrics
grep UiState feature/news/build/compose-metrics/news-classes.txt
```

The classes report is only written on a real compile, so clean the module first
or an up-to-date task will leave an empty file behind and read as a pass.

**Enum values stay `UPPER_SNAKE`.** The guideline asks for PascalCase, but it
binds "library development targeting or extending Jetpack Compose". Our enums
live in `:core:model` and `:core:common`, which are pure-Kotlin modules with no
Compose dependency; the modules that do target Compose expose no enums or public
constants at all. Renaming is also not free: `SettingsStore` persists
`ThemePreference.name` into DataStore, so `SYSTEM` -> `System` would silently
reset the saved theme for every existing install on upgrade.

[compose-api]: https://github.com/androidx/androidx/blob/androidx-main/compose/docs/compose-api-guidelines.md
