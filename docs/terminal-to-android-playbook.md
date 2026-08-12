# Terminal-to-Android Playbook

How to develop and ship this Android app from a terminal on an aarch64 Android
device, with no Android Studio and no local Android SDK.

## 1. The constraint that shapes everything

This machine is `aarch64` (a phone under PRoot). The official Android SDK
build-tools — `aapt2`, `d8`, `apksigner` — ship as **x86_64 Linux binaries
only**. There is no supported aarch64 build. So:

> **You cannot compile this app locally. GitHub Actions is your compiler.**

Gradle 8.7 and JDK are installed and usable, but any task that reaches the
Android plugin's native tooling will fail. Don't burn time trying to make
`./gradlew assembleDebug` work here.

The practical consequence: **every mistake costs a full CI round trip of 3–5
minutes.** The entire workflow below exists to reduce the number of round trips,
not to make any single one faster.

## 2. The loop

```
edit  →  bash scripts/preflight.sh  →  commit  →  push  →  gh run watch
                     ↑                                          |
                     └────────── read --log-failed ─────────────┘
```

Never skip preflight. It is free; CI is not.

## 3. The most expensive mistake: fixing a guessed problem

Real history from this repo, runs #3–#8:

| Run | Commit | Root error |
|-----|--------|-----------|
| 31581630657 | Training Forge UI | `google-services.json is missing` |
| 31582083979 | Rebrand to ColdForge | `google-services.json is missing` |
| 31582351654 | Remove SandboxScreen import | `google-services.json is missing` |
| 31582699736 | Add mock google-services.json | `AAPT: resource mipmap/ic_launcher not found` |
| 31583626623 | Remove launcher icons | `Unresolved reference 'compose'` (lifecycle) |
| 31584605326 | Add lifecycle dep + proguard | `Unresolved reference 'GeminiApiClient'` (stale test) |

**Three consecutive runs failed on the identical error.** Two commits in the
middle were fixing problems nobody had reported — the actual log said
`google-services.json is missing` the whole time.

The rule that follows:

> Read `gh run view <id> --log-failed` **before** writing a fix. Fix the error
> the log names. Not the one you assume is next.

## 4. Root cause vs. cascade

Kotlin reports one broken import as many errors. Run #7 produced five:

```
BeaconStreamScreen.kt:13:27 Unresolved reference 'compose'          ← ROOT
BeaconStreamScreen.kt:26:38 Unresolved reference 'collectAsStateWithLifecycle'
BeaconStreamScreen.kt:43:35 Unresolved reference 'id'
BeaconStreamScreen.kt:45:26 Argument type mismatch: Int vs BeaconSignalEntity
BeaconStreamScreen.kt:49:21 Overload resolution ambiguity for isEmpty()
```

Only the first was real. The import failed, so `signals` became an error type,
and everything touching it reported nonsense — including a type mismatch
against `Int` that appears nowhere in the source.

> Fix the **lowest line number** first, then re-read. Errors about a variable's
> *type* that make no sense are almost always downstream of a failed import.
> Never "fix" a cascade error; you will make correct code wrong.

## 5. Preflight: what you can check with no SDK

`scripts/preflight.sh` runs six checks, each derived from a failure this repo
actually hit. All six are validated against reproductions of those failures.

1. **Version-catalog aliases** — every `libs.*` used in a build file exists in
   `gradle/libs.versions.toml`.
2. **Build-referenced files exist** — catches `proguardFiles` naming a
   `proguard-rules.pro` that was never created, and the google-services plugin
   applied without `google-services.json`.
3. **Manifest resources resolve** — catches `AAPT: resource mipmap/ic_launcher
   not found` after an icon purge.
4. **Internal imports resolve** — catches stale imports left behind when a class
   is deleted. **Scans `test/` and `androidTest/`, not just `main/`**, because
   `./gradlew build` compiles unit tests and a stale test import fails the build
   exactly like a stale main one.
5. **androidx sub-package artifacts** — catches importing
   `androidx.lifecycle.compose.*` when only `lifecycle-runtime-ktx` is declared.
6. **Hilt wiring** — catches `@AndroidEntryPoint` with no `@HiltAndroidApp`, the
   annotation on a class the manifest does not instantiate, and an
   `<application>` with no `android:name`. All three crash at launch and all
   three compile cleanly.

What preflight **cannot** catch: type errors, Compose compiler issues, KSP/Hilt
graph failures, R8 output problems, lint. Those still need CI. And nothing here
proves the app runs — see below.

## 5a. A green build is not a working app

Nine consecutive green builds shipped an app that **crashed instantly on
launch**. `PeerSignalApp` was a plain `Application` carrying a comment reading
"Initialization logic for Firebase and Hilt will go here", while `MainActivity`
was `@AndroidEntryPoint` and the beacon screen called `hiltViewModel()`. With no
`@HiltAndroidApp` there is no `SingletonComponent`, so `onCreate` throws:

```
IllegalStateException: Hilt Activity must be attached to an @HiltAndroidApp Application
```

Nothing in a compile can see this. Check 6 exists because of it.

The same period also shipped an APK **nobody could install**. There is no
`signingConfig`, so the release output is `app-release-unsigned.apk`, and
Android refuses unsigned packages. The debug APK was being built the whole time
and simply never uploaded.

> Two questions a green tick does not answer: *does it launch*, and *can it be
> installed*. Publish the debug APK (auto-signed with the debug keystore) and
> put it on a device. Until then "CI is green" means "it compiles", nothing more.

### Two traps when writing checks like these

Both bit during development of this script, and both produced a check that
silently passed while the bug was present:

- `grep` is **line-oriented**. `proguardFiles(` spans multiple lines in
  `build.gradle.kts`, so a `grep -P` pattern for it never matches. Use
  `perl -0777` to slurp the file.
- A loose alternation gives a **false negative**. Checking for `lifecycle-compose`
  OR bare `compose` passes on any project that declares `activity-compose`.
  Require both segments tied together: `lifecycle-[a-z-]*compose`.

> Always test a check by reproducing the bug it targets and confirming it fails.
> An untested check is worse than none — it grants false confidence.

## 6. Command crib sheet

```bash
# Watch the run just pushed, blocking until it finishes
gh run watch --exit-status --interval 15

# Only the failed step's log — the first thing to read, always
gh run view <run-id> --log-failed

# Just the compiler errors, root cause first
gh run view <run-id> --log-failed | grep -E "e: file|What went wrong" | head

# Step-by-step status, to see how far it got
gh run view <run-id> --job=<job-id>

# Recent runs with IDs and conclusions
gh run list --limit 5

# The run for the commit you just pushed -- ALWAYS pin to the SHA
gh run list --commit $(git rev-parse HEAD)
```

### `gh run list --limit 1` after a push is a race

GitHub takes a few seconds to register a new run. Listing "the latest run"
immediately after `git push` frequently returns the **previous** commit's run,
which is still in progress. Watch that and you get a green result that verified
none of your changes.

This happened here: the `@HiltAndroidApp` fix was pushed, `--limit 1` returned
the earlier docs-commit run, and it went green — appearing to confirm a fix it
never compiled. The tell was that the uploaded artifacts were byte-for-byte
identical to the previous run despite a source change.

```bash
# Wrong: races the push
gh run list --limit 1 --json databaseId --jq '.[0].databaseId'

# Right: cannot resolve to another commit's run
gh run list --commit $(git rev-parse HEAD) --json databaseId --jq '.[0].databaseId'
```

> Always confirm what a run actually built before trusting its colour:
> `gh run view <id> --json headSha`. A green tick attached to the wrong commit
> is worse than a red one — it ends the investigation instead of starting it.
> Identical artifact sizes across a source change mean nothing was rebuilt.

**Progress is measurable even when the build stays red.** Compare step
durations: run #7 failed at 2m51s in `compileDebugKotlin`; run #8 failed at
4m11s having passed `compileDebugKotlin` *and* `minifyReleaseWithR8`. A longer
red run that dies later is real progress.

## 7. Repo-specific gotchas

- **`google-services.json`** is required at `app/` for the Firebase plugin, even
  to compile. The committed file is a **mock** for CI. It is not valid for
  actual Firebase calls at runtime.
- **`isMinifyEnabled = true`** on release means R8 runs in CI. `kotlinx.serialization`
  DTOs in `CompanionPythonEngineImpl` need the keep rules in
  `app/proguard-rules.pro` — without them R8 strips field names and JSON parsing
  breaks *at runtime*, with a green build.
- **`minSdk = 34`** restricts installs to Android 14+. Verify this is deliberate.
- **Integration tests do not belong in `app/src/test/`.** `ParserApiClientTest`
  called a live local Python proxy; it could never pass on a GitHub runner and
  blocked the build. Keep CI tests hermetic, or move them to a source set the CI
  job does not compile.

## 8. Workflow shape, and why

`.github/workflows/android_build.yml` previously ran three Gradle invocations:

```yaml
- run: ./gradlew build           # assembles debug AND release, runs lint + tests
- run: ./gradlew bundleRelease
- run: ./gradlew assembleRelease # no-op: `build` already did this
```

It now runs one:

```yaml
- run: ./gradlew build bundleRelease --build-cache
```

The reasoning, which generalizes to any Gradle CI:

- **`build` = `assemble` + `check`**, and `assemble` covers *every* variant,
  release included. The old third step was therefore a pure no-op. This is not
  a guess: run `31583626623` logs `> Task :app:minifyReleaseWithR8` inside the
  `./gradlew build` step. Read the task list in a real log before assuming what
  a lifecycle task covers.
- **`bundleRelease` is the one genuine gap** — bundle tasks are not part of
  `assemble`, so it must be requested explicitly.
- **Same invocation, not a second one.** Gradle then shares
  `compileReleaseKotlin` and `minifyReleaseWithR8` between the assemble and
  bundle paths, and pays configuration cost once instead of three times.

### What that consolidation actually bought: about 4 seconds

Measured, not estimated. Per-step timings, run #31585174325 (before) against
run #31585618705 (after):

| Step | Before | After |
|------|--------|-------|
| main Gradle step | 4m35s | 2m28s |
| `bundleRelease`, separate step | 3s | — |
| `assembleRelease`, separate step | 1s | — |
| **job total** | **4m51s** | **2m44s** |

The redundant steps cost **four seconds between them**. Gradle's up-to-date
checking had already made them nearly free — `assembleRelease` genuinely had
nothing to do, so it did nothing, quickly.

The 2m07s that actually disappeared came out of the *same* `./gradlew build`
task, which the consolidation cannot explain. The likely cause is cache
warmth: the run before spent 6s in `Post set up JDK 17` **saving** the Gradle
cache, the run after spent 8s **restoring** it and 0s saving. One data point
cannot separate that from the `--build-cache` flag, so no confident attribution
is available here.

> A redundant step is not automatically a slow step. Before optimizing CI, pull
> per-step timings from
> `gh api repos/<owner>/<repo>/actions/jobs/<job-id> --jq '.steps[]'`
> and find out where the minutes actually are. The obvious redundancy was 0.1%
> of this job; the real cost was elsewhere.

The consolidation is still worth keeping — fewer moving parts, one
configuration pass, and no misleading green "Build Release APK" step implying
work that never happened. Just not as a speed fix.

A `concurrency` group with `cancel-in-progress: true` was also added, so a
newer push kills the in-flight run instead of paying for both and leaving you
waiting on a stale answer. This matters most here, where pushes come in quick
succession during a debug loop.

### Residual redundancy, deliberately left

`check` runs unit tests for *both* variants (`testDebugUnitTest` and
`testReleaseUnitTest` — run #31584605326 shows both compiling and both
failing). Replacing `build` with an explicit task list would drop the duplicate,
but hard-codes a variant list that silently goes stale when variants change, and
a task-name typo costs a full CI round trip to discover. With a small test suite
the duplicate is cheap; the explicit list is worth it only once tests dominate
the wall clock.
