#!/usr/bin/env bash
# ──────────────────────────────────────────────────────────────
# Run the same checks CI performs, locally.
# Usage: bash scripts/ci-local.sh
# ──────────────────────────────────────────────────────────────
set -euo pipefail

echo "=== [1/2] Verify (compile + test + coverage) ==="
./mvnw -B -ntp clean verify

echo ""
echo "=== [2/2] Package (uber-JAR) ==="
./mvnw -B -ntp package -DskipTests

echo ""
echo "=== Done ==="
echo "Coverage report : target/site/jacoco/index.html"
echo "Packaged JAR    : $(ls target/structlab-*.jar 2>/dev/null || echo 'not found')"
