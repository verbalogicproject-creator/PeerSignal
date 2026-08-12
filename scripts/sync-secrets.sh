#!/usr/bin/env bash
#
# sync-secrets.sh -- push signing credentials to GitHub Actions from one file.
#
# ONE source of truth: keystore.properties. app/build.gradle.kts already reads
# it for local release builds, so it has to exist anyway. This pushes the same
# values to GitHub so local and CI can never drift apart.
#
#     edit keystore.properties  ->  bash scripts/sync-secrets.sh  ->  done
#
# Why a script rather than `gh secret set` by hand: the interactive prompt form
# fails silently in some shells on this device (no TTY), which cost four
# attempts and produced no error. Every write here is NON-INTERACTIVE stdin,
# which is the form already proven to work.
#
# Values are never printed, never passed as argv (argv is visible in `ps`), and
# never written to shell history.
#
# Usage:
#     bash scripts/sync-secrets.sh          push all secrets
#     bash scripts/sync-secrets.sh --check  report status only, change nothing
#
set -uo pipefail
cd "$(dirname "$0")/.." || exit 1

PROPS="keystore.properties"
MODE="${1:-push}"

red()  { printf '\033[31m%s\033[0m\n' "$1"; }
grn()  { printf '\033[32m%s\033[0m\n' "$1"; }

# --- resolve repo ---------------------------------------------------------
REPO=$(gh repo view --json nameWithOwner --jq .nameWithOwner 2>/dev/null)
if [ -z "$REPO" ]; then
    red "Cannot resolve the GitHub repo. Run this from inside the repo, with gh authenticated."
    exit 1
fi
echo "repo: $REPO"

# --- status mode ----------------------------------------------------------
report() {
    local names missing=0
    names=$(gh secret list -R "$REPO" --json name --jq '.[].name' 2>/dev/null)
    for s in SIGNING_KEY_BASE64 SIGNING_KEY_ALIAS SIGNING_KEYSTORE_PASSWORD; do
        if printf '%s\n' "$names" | grep -qx "$s"; then
            grn "  OK      $s"
        else
            red "  MISSING $s"; missing=1
        fi
    done
    return $missing
}

if [ "$MODE" = "--check" ]; then
    report && grn "READY" || red "NOT READY"
    exit 0
fi

# --- template if absent ---------------------------------------------------
if [ ! -f "$PROPS" ]; then
    red "$PROPS not found."
    cat <<'TEMPLATE'

Create it with these four lines (it is already gitignored):

    storeFile=/root/coldforge-release.jks
    storePassword=YOUR_KEYSTORE_PASSWORD
    keyAlias=coldforge
    keyPassword=YOUR_KEY_PASSWORD

keyPassword may be omitted if it matches storePassword -- which it does when
you accepted keytool's offer to reuse it. Then re-run this script.
TEMPLATE
    exit 1
fi

# --- parse (no eval; values may contain shell metacharacters) -------------
prop() { grep -E "^$1=" "$PROPS" | head -1 | cut -d= -f2- ; }

STORE_FILE=$(prop storeFile)
STORE_PASS=$(prop storePassword)
KEY_ALIAS=$(prop keyAlias)
KEY_PASS=$(prop keyPassword)

fail=0
[ -n "$STORE_FILE" ] || { red "storeFile missing from $PROPS"; fail=1; }
[ -n "$STORE_PASS" ] || { red "storePassword missing from $PROPS"; fail=1; }
[ -n "$KEY_ALIAS" ]  || { red "keyAlias missing from $PROPS"; fail=1; }
[ "$fail" -eq 0 ] || exit 1

if [ ! -f "$STORE_FILE" ]; then
    red "keystore not found at: $STORE_FILE"
    exit 1
fi
SZ=$(stat -c '%s' "$STORE_FILE")
if [ "$SZ" -lt 1024 ]; then
    red "keystore is only $SZ bytes -- release.yml rejects anything under 1024."
    exit 1
fi
echo "keystore: $STORE_FILE ($SZ bytes)"

# --- encode with a round-trip check --------------------------------------
TMP=$(mktemp -d) || exit 1
cleanup() { rm -rf "$TMP"; }
trap cleanup EXIT

base64 -w0 "$STORE_FILE" > "$TMP/ks.b64"
base64 -d "$TMP/ks.b64" > "$TMP/ks.rt"
if [ "$(sha256sum < "$STORE_FILE" | cut -d' ' -f1)" != "$(sha256sum < "$TMP/ks.rt" | cut -d' ' -f1)" ]; then
    red "base64 round-trip mismatch -- refusing to upload a corrupt keystore."
    exit 1
fi
grn "base64 round-trip verified"

# --- push (stdin only; never argv, never echoed) --------------------------
set_secret() {  # $1 name, value on stdin
    if gh secret set "$1" -R "$REPO" >/dev/null 2>&1; then
        grn "  set     $1"
    else
        red "  FAILED  $1"
        return 1
    fi
}

echo "pushing:"
set_secret SIGNING_KEY_BASE64 < "$TMP/ks.b64"
printf '%s' "$KEY_ALIAS"  | set_secret SIGNING_KEY_ALIAS
printf '%s' "$STORE_PASS" | set_secret SIGNING_KEYSTORE_PASSWORD

# Only needed when the key password genuinely differs; release.yml otherwise
# falls back to the store password.
if [ -n "$KEY_PASS" ] && [ "$KEY_PASS" != "$STORE_PASS" ]; then
    printf '%s' "$KEY_PASS" | set_secret SIGNING_KEY_PASSWORD
fi

echo
report && grn "READY -- tag v0.0.1 to cut a release." || red "NOT READY"
