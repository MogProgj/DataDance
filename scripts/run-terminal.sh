#!/usr/bin/env bash
# ── StructLab Terminal Launcher ────────────────────────────
# Launches the StructLab interactive terminal REPL.
# Requires JDK 17+ on your PATH.
# ────────────────────────────────────────────────────────────
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
JAR="$SCRIPT_DIR/structlab.jar"
if [ ! -f "$JAR" ]; then
  JAR="$(ls "$SCRIPT_DIR"/structlab-*.jar 2>/dev/null | head -1)"
fi
exec java -jar "$JAR" --terminal "$@"
