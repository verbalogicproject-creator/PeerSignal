# Termux-to-APK Pipeline

Getting a running app onto this phone, from a terminal on the same phone.

This is the end-to-end path: edit in Termux → build in CI → APK in Downloads →
installed → crash diagnosed. For *why* CI is the compiler and how to read its
failures, see [terminal-to-android-playbook.md](terminal-to-android-playbook.md).
This document is the mechanics.

## The shape of it

```
  Termux (aarch64, no Android SDK)
        │  edit
        ▼
  bash scripts/preflight.sh          ← free, ~1s, catches 6 failure classes
        │  git push
        ▼
  GitHub Actions (ubuntu-latest)     ← the actual compiler, ~2-4 min
        │  gh run download
        ▼
  /storage/emulated/0/Download/      ← reachable by the phone's Files app
        │  tap to install
        ▼
  Running app
        │  crash?
        ▼
  Android/data/<pkg>/files/crash.txt ← because logcat is unreadable here
```

Nothing in stages 1–2 proves the app runs. Only stage 4 does.

## Stage 1 — Preflight, locally

```bash
bash scripts/preflight.sh
```

Six checks, no SDK required, about a second. A CI round trip is 2–4 minutes, so
this is worth running every single time. It cannot type-check; it catches the
structural failures this repo has actually hit.

## Stage 2 — Build in CI

```bash
git push origin main

# Pin to the SHA. `gh run list --limit 1` races the push and will happily
# return the PREVIOUS commit's run, which then goes green and tells you nothing.
SHA=$(git rev-parse HEAD)
RUN=$(gh run list --commit "$SHA" --json databaseId --jq '.[0].databaseId')

gh run watch "$RUN" --exit-status --interval 20

# Always confirm what was actually built before trusting the colour.
gh run view "$RUN" --json headSha,conclusion --jq '"\(.headSha[0:8]) -> \(.conclusion)"'
```

If a run is already in flight, note that `concurrency.cancel-in-progress` means
your next push **cancels it**. Finish verifying before pushing again, or you
cancel your own answer.

## Stage 3 — Get the APK onto the phone

Two environment quirks make the obvious command fail. Both are worked around in
the snippet below.

```bash
# `gh` resolves the repo from git context, so either cd into the repo or pass -R.
# TMPDIR matters: gh defaults to /data/local/tmp, which does not exist inside
# PRoot, and fails with "error initializing temporary file".
cd ~/adroid-app-brainstorm
export TMPDIR=$HOME/.tmp && mkdir -p "$TMPDIR"

gh run download "$RUN" \
  -R verbalogicproject-creator/PeerSignal \
  -n app-debug-apk \
  -D /tmp/apk

cp /tmp/apk/app-debug.apk /storage/emulated/0/Download/ColdForge-debug.apk
```

**Download `app-debug-apk`, not the release one.** There is no `signingConfig`,
so the release artifact is `app-release-unsigned.apk` and Android refuses to
install unsigned packages. The debug APK is signed with the auto-generated debug
keystore.

Verify before trusting it:

```bash
cd /tmp/apk
unzip -l app-debug.apk | grep -E "AndroidManifest.xml|classes.dex|resources.arsc"
python3 -c "print('signed:', b'APK Sig Block 42' in open('app-debug.apk','rb').read())"
```

> Artifact byte-size from the API is the **compressed zip**, not the APK. A
> 9.6 MB artifact unpacked to a 27.4 MB APK here. Don't quote one as the other.

## Stage 4 — Install

Files app → **Download** → tap `ColdForge-debug.apk`. First time, Android asks
to allow "install unknown apps" for the Files app.

`/storage/emulated/0/Download` is writable from PRoot, which is what makes this
whole hand-off work without a cable.

## Stage 5 — Diagnose a crash

**logcat is a dead end here.** Termux has no `READ_LOGS`, so reading another
app's buffer returns nothing at all — not an error, just silence:

```bash
/system/bin/logcat -d -b crash -t 200    # → empty
/system/bin/logcat -d -t 5               # → empty
```

So the app reports its own crashes. `CrashLog` installs an uncaught-exception
handler from `Application.attachBaseContext` — deliberately not `onCreate`,
because Hilt builds its component inside `super.onCreate()` and a handler
installed there would miss graph-construction failures.

```bash
cat /storage/emulated/0/Android/data/com.peersignal.app/files/crash.txt
```

`/storage/emulated/0/Android/data/` is **not listable** from PRoot, but direct
paths into it resolve fine. `ls` on the parent fails while `cat` on the full
path works — do not conclude the file is missing because the directory listing
was denied.

If the file does not exist, the app died before `attachBaseContext` ran, which
points at installation, the manifest, or resource loading rather than app code.

## Environment quirks, collected

| Symptom | Cause | Fix |
|---|---|---|
| `failed to run git: not a git repository` | `gh` needs repo context | `cd` into repo, or `-R owner/repo` |
| `error initializing temporary file: /data/local/tmp/...` | `gh` default TMPDIR absent in PRoot | `export TMPDIR=$HOME/.tmp` |
| `logcat` prints nothing, exit 0 | no `READ_LOGS` for unprivileged apps | read `crash.txt` instead |
| `ls: Android/data: Permission denied` | directory not listable | use the full path directly |
| Green run verified nothing | `--limit 1` raced the push | `gh run list --commit $(git rev-parse HEAD)` |
| Artifact far smaller than expected | API reports compressed size | unzip, then measure |
| Release APK will not install | no `signingConfig`, output unsigned | install the debug APK |

## A worked failure

The first successfully installed APK crashed instantly with *"ColdForge -
PeerSignal has stopped"*, and logcat gave nothing. Diagnosis came from reading
the code instead:

`res/values/font_certs.xml` held a certificate blob that was not a certificate.
Its DER header declared a 1095-byte structure over a 1041-byte body, and the
`dev` and `prod` entries were byte-identical where the genuine generated file
carries two different certs. `GoogleFont.Provider` verifies Play Services
against that array, so verification failed and Compose threw resolving the first
glyph — which, because `PeerSignalTheme` applies the typography, is the first
composition.

Checking the claim rather than assuming it:

```bash
python3 -c "
import base64,re
b=re.findall(r'<item>\s*([A-Za-z0-9+/=\s]{100,})\s*</item>',
             open('app/src/main/res/values/font_certs.xml').read())[0]
d=base64.b64decode(re.sub(r'\s+','',b))
print('declared', int.from_bytes(d[2:4],'big')+4, 'actual', len(d))"
```

The fix was to drop downloadable fonts for system families — no provider, no
Play Services, no network, which suits an app whose engine is a local loopback
process. `CrashLog` was added in the same change so the next crash produces
evidence instead of another code-reading exercise.

> A resource can be perfectly valid to AAPT and still be semantic garbage.
> `font_certs.xml` compiled, packaged, and shipped through eleven green builds.
