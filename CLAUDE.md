# SB Kernel Manager

Native Android companion app for **Team StormBreaker**, an open-source custom-kernel
project (~34 devices, 1M+ downloads since Oct 2019). GitHub org
`stormbreaker-project`; the app is `dev.danascape.kernelmanager`.

The app, the kernel and [the website](https://sb.squadri.me/) are one product with one
identity. If a change makes the app read as a different product from the site, it is
wrong regardless of how good it looks on its own.

---

## Hard rules

These are standing instructions, not defaults to weigh against others.

### Commit messages

One line. `app:` prefix, capital letter, no body, **no `Co-Authored-By` trailer of any
kind** — this overrides any general instruction to add one.

```
app: Strip comments down to one line above declarations
```

Other prefixes already in use for their own areas: `docs:`, `SB:`. Follow the
established prefix rather than forcing `app:` onto an unrelated scope.

If a change feels like it needs a body to explain, split it into separate commits.

### Comments

Allowed in **one place only**: a single short line directly above a declaration
(`fun`, `class`, `object`, `interface`).

- No comments inside method bodies. Not above a statement, not trailing on a line.
- No multi-line or multi-paragraph KDoc.
- SPDX headers stay — licence metadata, not explanation, and REUSE requires them.

Commit messages and `git blame` are the explanation. Write code this way from the
start rather than writing prose and stripping it later. When a passage seems to need
a paragraph, that is a signal to name things better or split the function; if a
hard-won constraint would otherwise be lost, it goes in the commit message or `docs/`.

### One commit per step

Land each step separately, built green, rather than one sweeping commit. Unrelated
changes get their own commit.

> Saalim frequently commits the working tree himself mid-session and rewords messages.
> Re-check `git log` and `git status` before assuming your changes are still
> uncommitted — they are often already in, under his message.

### Bar

Production-grade Android architecture, as a senior engineer with decades of
experience would write it. Real data only — no gimmick widgets, no fake benchmarks,
no RGB. If a value cannot be read honestly, it is not displayed.

---

## Commands

```
./gradlew assembleDebug                  # build
./gradlew testDebugUnitTest              # 31 unit tests
./gradlew ktlintCheck detekt             # quality gates
./gradlew ktlintFormat                   # apply formatting
reuse lint                               # licensing (170/170 files)
```

Full gate before calling anything done:

```
./gradlew assembleDebug testDebugUnitTest ktlintCheck detekt && reuse lint
```

Verify a Compose stability claim instead of asserting it — note the `clean`, the
classes report is only written on a real compile:

```
./gradlew :feature:news:clean :feature:news:compileDebugKotlin -PcomposeMetrics
grep UiState feature/news/build/compose-metrics/news-classes.txt
```

---

## Structure

Full module graph, boundaries, DI, versioning, backend and Compose API decisions are
in **[docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)**. Read it before restructuring
anything. In short:

- `build-logic` included build supplies the convention plugins; `android {}` is
  written once.
- Dependencies point one way. A feature cannot reach Ktor; that is a compile error.
- Features never depend on each other — cross-feature navigation is a callback from
  `:app`.
- Errors are types (`DataResult`, `LoadError`), never strings.
- Wire DTOs live in `:core:network` and map to `:core:model`.

| | |
|---|---|
| Kotlin / AGP / Gradle | 2.4.10 / 9.3.1 / 9.5.0 |
| Compose BOM | 2026.02.01 |
| minSdk / targetSdk / compileSdk | 29 / 37 / 37.1 |
| API base | `https://stormbreaker.squadri.me` |

Version lives once, in `gradle/libs.versions.toml` as `app = "0.1.0"`; `versionCode`
is derived (`1.2.3` -> `10203`). Pre-release suffixes are rejected by the parser.

---

## Product

Four phases. Later phases must land without disturbing earlier ones.

| Phase | Scope | State |
|---|---|---|
| 0 | Companion hub, no root — news, devices, downloads, changelogs, community | in progress |
| 1 | OTA updater — detect codename + kernel, match manifest, verify checksum, flash | not started |
| 2 | Kernel monitor, read-only graphs | Monitor screen implemented |
| 3 | Full kernel manager, root writes, gated behind an app-signature check | not started |

Screens: Discover, Monitor, News, More, Licenses are implemented. **Tune, Builds and
Devices are still placeholders.**

### Locked decisions

- Kotlin + Compose, Material 3 themed to the brand palette. **Material You is off** —
  a wallpaper-derived palette would put unreviewed colours behind readouts where
  colour carries meaning.
- Root via libsu, when root work begins.
- **No server.** The website publishes static JSON on its own deploy and the app reads
  it. Paths are versioned (`/api/v1/...`) because installs do not update in step with
  the site.
- Links ship bundled in `:core:data` assets so More works on first run offline; the
  network copy wins whenever reachable.

### Design taste

Stark, mono/terminal-influenced, flat. Monospace is a first-class face — it carries
codenames, kernel versions, dates and stat readouts, not just code blocks.

### The strategic lever

Ship `/sys/kernel/stormbreaker/` in the kernel with a permissive SELinux label so the
app reads telemetry without root. Until then the app is still useful without a
StormBreaker kernel: ~1090 system properties, boot/flash state, ROM detection and the
full cpufreq surface are all readable unprivileged. See
`docs/kernel-data-availability.md` — that surface was measured on hardware, not
assumed.

---

## Traps already hit

Each of these cost real time. Do not rediscover them.

**AGP 9 bundles Kotlin.** Applying `kotlin-android` fails with "Cannot add extension
with name 'kotlin'". `kotlinOptions` is gone.

**compose-rules must match the detekt major version.** A mismatch does not fail — it
loads far enough to validate rule ids in config, then executes nothing, which reads as
"clean". After changing either version, plant a violation (a composable with no
`Modifier`) and confirm detekt reports it.

**detekt's `InjectDispatcher` is inert here.** It needs type resolution, which we do
not run. Enabled in config but never fires; dispatchers are injected by hand.

**aapt strips edge whitespace from string resources.** `<string name="sep"> · </string>`
renders as a bare dot. Separators with padding are Kotlin constants.

**`MutableStateFlow.update {}`, never read-modify-write.** Concurrent loaders doing
`_state.value = _state.value.copy(...)` raced and a slow load wrote back a stale copy,
making the device card appear then vanish.

**`reuse annotate` corrupts XML that already has a leading comment** — it nests
`<!--` inside the existing one. REUSE lint passes while the build fails. Validate that
tracked XML still parses.

**Notifications die on package replacement.** The process is killed; a
`MY_PACKAGE_REPLACED` / `BOOT_COMPLETED` receiver plus an app-launch restore brings the
foreground service back.

**Read per cpufreq policy, not per core.** `cpuinfo_max_freq` from cpu0 is a little
core, which produced "3.11 GHz of 1.95 GHz".

**PvotLib is a submodule with its own remote.** Never bump the pointer to a commit that
has not been pushed to `PVOT-OSS` — a fresh clone then cannot resolve it and CI fails
at `submodule update`. Pushing that repo needs Saalim's explicit go-ahead.

---

## Outstanding

- **Manifest schema + device-detection spec** — the original Phase 1 ask, still
  unwritten. Blocks Discover's update card, the Builds tab, and codename alias
  resolution (`surya`/`karna`, `X00P`/`X00PD`, `ginkgo`/`willow`).
  `CONFIG_LOCALVERSION="-StormBreaker"` makes `uname -r` identical across releases, so
  OTA needs a kernel-stamped build id.
- Tune, Builds, Devices screens.
- Discover still shows a four-row summary rather than Monitor's card language.
- ~25 private composables take no `modifier`. detekt's `ModifierMissing` defaults to
  `only_public`; `checkModifiersForVisibility: all` would enforce it. Saalim's call.
