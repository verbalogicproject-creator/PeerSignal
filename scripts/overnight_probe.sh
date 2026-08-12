#!/data/data/com.termux/files/usr/bin/bash
#
# overnight_probe.sh -- does a long job survive a night on this phone?
#
# This is acceptance criterion #16 stripped of everything interesting. It runs
# a sleep-and-count loop for N hours and records whether the OS let it. No
# model, no daemon, no Android app -- if THIS cannot survive until morning,
# nothing built on top of it can, and we learn that for the cost of one night.
#
# The measurement that matters is DRIFT. The loop intends to tick every
# INTERVAL seconds. If wall-clock elapsed runs ahead of expected elapsed, the
# process was frozen -- that is Doze, or the OOM killer's quieter cousin,
# suspending us. Drift is therefore a direct measure of how much of the night
# Android took away.
#
# Run it FROM TERMUX (not PRoot), on charge, screen off:
#     bash overnight_probe.sh 6          # 6 hours
#     bash overnight_probe.sh 6 nolock   # 6 hours, no wake lock (control arm)
#
# Then in the morning:
#     bash overnight_probe.sh --report
#
set -uo pipefail

HOURS="${1:-6}"
MODE="${2:-lock}"
INTERVAL=10

OUTDIR="$HOME/.coldforge"
CSV="$OUTDIR/overnight.csv"
SUMMARY="$OUTDIR/overnight_summary.txt"
mkdir -p "$OUTDIR"

# --- reporting mode -------------------------------------------------------
if [ "${1:-}" = "--report" ]; then
    [ -f "$CSV" ] || { echo "no probe data at $CSV"; exit 1; }
    awk -F, 'NR>1 {
        n++
        if ($5+0 > maxdrift) { maxdrift = $5+0 }
        if ($5+0 - prev > 30) { freezes++; frozen += ($5+0 - prev) }
        prev = $5+0
        if ($8+0 > maxtemp) maxtemp = $8+0
        lastbatt = $6; lasttemp = $8; lastiter = $2
    } END {
        printf "ticks recorded   : %d\n", n
        printf "final iteration  : %s\n", lastiter
        printf "max drift        : %.1f s\n", maxdrift
        printf "freeze events    : %d  (gaps > 30s)\n", freezes+0
        printf "total frozen     : %.1f s  (%.1f min)\n", frozen+0, (frozen+0)/60
        printf "peak CPU temp    : %d C\n", maxtemp
        printf "final battery    : %s%%\n", lastbatt
    }' "$CSV"
    echo
    echo "verdict:"
    tail -1 "$SUMMARY" 2>/dev/null || echo "  (run did not write a completion line -- it was killed)"
    exit 0
fi

# --- preflight ------------------------------------------------------------
# Refuse to run under conditions that would produce a misleading result. A
# probe that quietly measures the wrong thing is worse than no probe.
preflight() {
    local fail=0

    # Running under PRoot means we would be measuring PRoot's survival, not
    # Android's -- if the PRoot session dies the child goes with it.
    if [ ! -d /data/data/com.termux/files/usr/bin ] || [ -z "${PREFIX:-}" ]; then
        echo "FAIL  not a Termux shell."
        echo "      Run this from Termux directly, not from inside proot-distro."
        echo "      Under PRoot you would measure PRoot's supervision, not Android's."
        fail=1
    fi

    local batt chg
    batt=$(read_batt); chg=$(read_chg)
    echo "      battery: ${batt}%  (${chg})"

    if [ "$MODE" = "lock" ]; then
        case "$chg" in
            Charging|Full) ;;
            *) echo "FAIL  night 1 is the on-charger run; plug the phone in."
               echo "      (for the unplugged control arm use: $0 $HOURS nolock)"
               fail=1 ;;
        esac
    else
        if [ "$batt" -lt 80 ] 2>/dev/null; then
            echo "FAIL  unplugged run needs >=80% to be meaningful (have ${batt}%)."
            fail=1
        fi
        case "$chg" in
            Charging|Full)
                echo "FAIL  this is the UNPLUGGED arm -- disconnect the charger."
                fail=1 ;;
        esac
    fi

    [ "$fail" -eq 0 ] || { echo; echo "aborted."; exit 1; }
}

# --- thermal zone selection ----------------------------------------------
# Most of this device's 99 zones read 0. Pick the first that reports a
# plausible temperature so the log is meaningful rather than a column of zeros.
ZONE=""
for z in /sys/class/thermal/thermal_zone*; do
    t=$(cat "$z/temp" 2>/dev/null) || continue
    if [ "$t" -gt 15000 ] 2>/dev/null && [ "$t" -lt 95000 ] 2>/dev/null; then
        ZONE="$z"; break
    fi
done

read_temp()  { [ -n "$ZONE" ] && echo $(( $(cat "$ZONE/temp" 2>/dev/null || echo 0) / 1000 )) || echo 0; }
read_batt()  { cat /sys/class/power_supply/battery/capacity 2>/dev/null || echo -1; }
read_chg()   { cat /sys/class/power_supply/battery/status  2>/dev/null || echo unknown; }

echo "ColdForge overnight probe -- acceptance criterion #16"
echo "      mode: ${MODE}   duration: ${HOURS}h"
preflight

# --- wake lock ------------------------------------------------------------
# The control arm exists on purpose: if the run survives WITHOUT the lock, the
# lock is not what saved it and we should know that before depending on it.
LOCKED=no
if [ "$MODE" = "lock" ] && command -v termux-wake-lock >/dev/null 2>&1; then
    termux-wake-lock && LOCKED=yes
fi
cleanup() {
    [ "$LOCKED" = yes ] && termux-wake-unlock >/dev/null 2>&1
}
trap cleanup EXIT

TOTAL=$(awk -v h="$HOURS" 'BEGIN{printf "%d", h*3600}')
TICKS=$(( TOTAL / INTERVAL ))
START=$(date +%s)

echo "ts_iso,iter,expected_s,actual_s,drift_s,batt_pct,charging,temp_c" > "$CSV"
{
    echo "started   : $(date -Is)"
    echo "duration  : ${HOURS}h  (${TICKS} ticks @ ${INTERVAL}s)"
    echo "wake lock : $LOCKED"
    echo "thermal   : ${ZONE:-none} ($(cat "${ZONE:-/dev/null}/type" 2>/dev/null || echo n/a))"
    echo "battery   : $(read_batt)%  $(read_chg)"
} > "$SUMMARY"

echo "probe running: ${HOURS}h, wake_lock=$LOCKED. Leave the phone on charge, screen off."
echo "log: $CSV"

for i in $(seq 1 "$TICKS"); do
    sleep "$INTERVAL"
    now=$(date +%s)
    actual=$(( now - START ))
    expected=$(( i * INTERVAL ))
    drift=$(( actual - expected ))
    printf '%s,%d,%d,%d,%d,%s,%s,%s\n' \
        "$(date -Is)" "$i" "$expected" "$actual" "$drift" \
        "$(read_batt)" "$(read_chg)" "$(read_temp)" >> "$CSV"
done

END=$(date +%s)
{
    echo "ended     : $(date -Is)"
    echo "wall clock: $(( (END - START) / 60 )) min  (intended $(( TOTAL / 60 )) min)"
    echo "SURVIVED: the loop reached its final tick without being killed."
} >> "$SUMMARY"

echo "done. morning report:  bash $0 --report"
