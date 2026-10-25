#!/bin/bash
# ==============================================================================
# FrameForge GFX — Reversible Rollback Script
# Reverts device settings to original system factory defaults
# ==============================================================================

set -e

echo "=========================================================="
echo "  FrameForge GFX — Restoring Factory Defaults             "
echo "=========================================================="

if ! command -v adb &> /dev/null; then
    echo "[!] Error: 'adb' command not found."
    exit 1
fi

echo "[1/6] Resetting Android Game Manager for com.tencent.ig..."
adb shell cmd game reset com.tencent.ig || true

echo "[2/6] Deleting forced peak_refresh_rate..."
adb shell settings delete system peak_refresh_rate || true

echo "[3/6] Deleting forced min_refresh_rate..."
adb shell settings delete system min_refresh_rate || true

echo "[4/6] Deleting user_refresh_rate..."
adb shell settings delete system user_refresh_rate || true

echo "[5/6] Deleting global fps_limit..."
adb shell settings delete global fps_limit || true

echo "[6/6] Resetting OriginOS vivo_multi_turbo..."
adb shell settings delete global vivo_multi_turbo || true

echo "=========================================================="
echo "  [SUCCESS] All Settings Restored to Factory Defaults!   "
echo "=========================================================="
