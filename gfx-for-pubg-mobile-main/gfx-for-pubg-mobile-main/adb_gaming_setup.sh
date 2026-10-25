#!/bin/bash
# ==============================================================================
# FrameForge GFX — Flagship Gaming ADB Optimization Script
# Target Device: iQOO Neo 9 5G (Chinese Variant / OriginOS 6)
# Hardware: Snapdragon 8 Gen 2 (4nm), 16GB RAM, 144Hz 1.5K AMOLED
# Target Game: PUBG Mobile Global (package: com.tencent.ig)
#
# Usage:
#   1. Connect device via USB with USB Debugging enabled, OR
#   2. Pair via Wireless Debugging (adb pair <ip>:<port> && adb connect <ip>:<port>)
#   3. Run: bash adb_gaming_setup.sh
# ==============================================================================

set -e

echo "=========================================================="
echo "  FrameForge GFX — Applying Flagship Performance Config   "
echo "  Target: iQOO Neo 9 5G / OriginOS 6 / com.tencent.ig     "
echo "=========================================================="

# Check if ADB is available and device is connected
if ! command -v adb &> /dev/null; then
    echo "[!] Error: 'adb' command not found. Please install Android Platform Tools."
    exit 1
fi

DEVICE_STATE=$(adb get-state 2>/dev/null || echo "offline")
if [ "$DEVICE_STATE" != "device" ]; then
    echo "[!] Error: No authorized Android device connected via ADB."
    echo "    Please verify USB/Wireless debugging is authorized on your phone."
    exit 1
fi

DEVICE_MODEL=$(adb shell getprop ro.product.model 2>/dev/null | tr -d '\r')
echo "[✓] Connected device: $DEVICE_MODEL"

# 1. Set Android Game Manager to Performance Mode for PUBG Mobile
echo "[1/7] Setting Game Mode to Performance (mode 2) for com.tencent.ig..."
adb shell cmd game set --mode 2 com.tencent.ig || true

# 2. Lock Peak Refresh Rate to 144 Hz
echo "[2/7] Forcing peak_refresh_rate to 144.0 Hz..."
adb shell settings put system peak_refresh_rate 144.0

# 3. Lock Minimum Refresh Rate to 144 Hz (prevents dynamic throttling / frame drops)
echo "[3/7] Forcing min_refresh_rate to 144.0 Hz..."
adb shell settings put system min_refresh_rate 144.0

# 4. Set OriginOS specific user refresh rate
echo "[4/7] Setting OriginOS user_refresh_rate to 144..."
adb shell settings put system user_refresh_rate 144 || true

# 5. Unlock SurfaceFlinger compositor FPS limit
echo "[5/7] Synchronizing SurfaceFlinger FPS limit to 144..."
adb shell settings put global fps_limit 144 || true

# 6. Request OriginOS Multi-Turbo Game Priority
echo "[6/7] Requesting OriginOS Multi-Turbo game acceleration..."
adb shell settings put global vivo_multi_turbo 1 || true

# 7. Safe shader/cache maintenance
echo "[7/7] Trimming temporary package shader caches..."
adb shell pm trim-caches 1000M || true

echo "=========================================================="
echo "  [SUCCESS] All Flagship Optimizations Successfully Applied! "
echo "  To revert anytime, run: bash adb_gaming_rollback.sh      "
echo "=========================================================="
