#!/usr/bin/env bash
# Run the same checks CI performs, locally.
# Usage: bash scripts/ci-local.sh
set -euo pipefail

echo "=== Build + Test + Coverage ==="
mvn -B -ntp clean verify

echo ""
echo "=== Done ==="
echo "Coverage report: target/site/jacoco/index.html"
