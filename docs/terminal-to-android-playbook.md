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

`scripts/preflight.sh` runs five checks, each derived from a failure this repo
actually hit. All five are validated against reproductions of those failures.

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

What preflight **cannot** catch: type errors, Compose compiler issues, KSP/Hilt
graph failures, R8 output problems, lint. Those still need CI.

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
```

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

## 8. Workflow tuning

`.github/workflows/android_build.yml` currently runs:

```yaml
- run: ./gradlew build          # already assembles debug + release, runs lint + tests
- run: ./gradlew bundleRelease
- run: ./gradlew assembleRelease  # redundant, `build` did this
```

`./gradlew build` already performs `assembleRelease`, so the third step is a
no-op re-run. Collapsing to `./gradlew bundleRelease assembleDebug testDebugUnitTest`
would cut wall-clock meaningfully. Adding `--build-cache` and a Gradle cache
action helps more.

For faster feedback while iterating, put the cheapest failing task first — a
compile-only step ahead of the full build fails in ~90s instead of ~4m.
