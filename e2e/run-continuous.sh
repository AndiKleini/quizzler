#!/usr/bin/env bash
#
# Continuous zero-downtime probe: run the quiz AND dashboard journeys over and
# over (Playwright discovers every *.spec.ts under tests/, so both run each pass).
# A failing run does NOT stop the loop — its full output is appended to the error
# log and the loop resumes with the next run. Stop with Ctrl-C.
#
# Usage:
#   ./run-continuous.sh
#   # KIND: each UI has its own ingress host, so set both targets
#   QUIZZLER_UI_BASE_URL=http://quizzler.localhost \
#     DASHBOARD_UI_BASE_URL=http://dashboard.localhost ./run-continuous.sh
#   ITERATIONS=100 ./run-continuous.sh          # bounded instead of infinite
#   ERROR_LOG=/tmp/probe.log ./run-continuous.sh # custom log location
#
set -u

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${SCRIPT_DIR}"

TARGET="${QUIZZLER_UI_BASE_URL:-http://localhost:4200}"
DASHBOARD_TARGET="${DASHBOARD_UI_BASE_URL:-http://localhost:4202}"
LOG_FILE="${ERROR_LOG:-${SCRIPT_DIR}/errors.log}"
ITERATIONS="${ITERATIONS:-0}"   # 0 = run forever

pass=0
fail=0
n=0

echo "Probing quiz UI:      ${TARGET}"
echo "Probing dashboard UI: ${DASHBOARD_TARGET}"
echo "Failures are appended to ${LOG_FILE} — the loop never stops on an error (Ctrl-C to quit)."
echo

while :; do
  n=$((n + 1))

  # Capture the run; a non-zero exit (failed assertions / broken step) is logged,
  # not propagated, so the loop continues.
  if output="$(npx playwright test --reporter=list 2>&1)"; then
    pass=$((pass + 1))
  else
    fail=$((fail + 1))
    {
      echo "==================================================================="
      echo "FAILURE  run #${n}  $(date -Is)  quiz=${TARGET}  dashboard=${DASHBOARD_TARGET}"
      echo "-------------------------------------------------------------------"
      echo "${output}"
      echo
    } >>"${LOG_FILE}"
  fi

  printf '\rruns: %d   passed: %d   failed: %d   ' "${n}" "${pass}" "${fail}"
  [ "${fail}" -gt 0 ] && printf '(last failure logged to %s)   ' "${LOG_FILE}"

  if [ "${ITERATIONS}" -ne 0 ] && [ "${n}" -ge "${ITERATIONS}" ]; then
    break
  fi
done

echo
echo "Done: ${pass} passed, ${fail} failed over ${n} runs."
[ "${fail}" -gt 0 ] && echo "See ${LOG_FILE} for the captured errors."
exit 0
