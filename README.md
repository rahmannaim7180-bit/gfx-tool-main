# FrameForge GFX — Gaming Performance Optimizer

FrameForge GFX is a production-quality native Android game performance and graphics diagnostics application built with Kotlin, Jetpack Compose, Material 3, and modern Android architecture. It is designed to provide sustainable, low-latency, and high-consistency gaming performance without false marketing claims or anti-cheat tampering.

---

## 🎮 Key Subsystems & Features

### 1. Target Game Management
- **Verified Package Catalog:** Targets PUBG Mobile Global (`com.tencent.ig`), Battlegrounds Mobile India (`com.pubg.imobile`), PUBG Mobile KR/JP (`com.pubg.krmobile`), PUBG Mobile Lite (`com.tencent.iglite`), PUBG Mobile VN (`com.vng.pubgmobile`), and Free Fire MAX (`com.dts.freefiremax`).
- **Live Status & Version:** Detects whether the game package is installed, queries package version names, and tracks runtime configuration state.
- **Affiliation Disclosure:** Independent gaming optimization tool; not affiliated with PUBG, Tencent, Krafton, or Garena.

### 2. GyroBoost: Gyroscope Enhancement Subsystem
- **Hardware Telemetry:** Direct subscription to hardware `Sensor.TYPE_GYROSCOPE` with `HIGH_SAMPLING_RATE_SENSORS`.
- **Live Sampling & Jitter:** Computes instantaneous and moving-average sampling frequency in Hz using nanosecond event timestamps.
- **Sensitivity Assistant:** 0–200% independent X-axis and Y-axis response multipliers.
- **Anti-Drift High-Pass Filter:** Eliminates sensor bias drift during stationary aiming.
- **Interactive Precision Test:** Real-time movement reticle with precision score calculation.

### 3. Performance Engine & RAW PERFORMANCE MODE (Beta)
- **Balanced Mode:** Default power management, preserves battery life and standard thermal boundaries.
- **Stable Competitive Mode:** Prioritizes frame-time consistency, 90/120Hz display modes, and smooth low-workload graphics.
- **Raw Performance Mode (Opt-In):** Flagship performance coordinator requesting:
  - CPU Performance Hints via Android ADPF (`PerformanceHintManager`)
  - Surface frame pacing & low-latency compositor hints
  - Background activity reduction & telemetry polling throttle
  - High refresh rate display locking (120Hz)
  - Elevated Game Manager performance mode (`cmd game set --mode 2`)
- **Reversible Safeguards:** Pre-modification snapshots saved in local Room database with one-tap emergency rollback.

### 4. Hardware Headroom Analyzer & Bottleneck Detector
- **Real Resource Diagnostics:** Inspects CPU utilization, GPU rendering workload, RAM pressure (`ActivityManager.MemoryInfo`), thermal status (`PowerManager.getThermalHeadroom`), and network latency.
- **Ranked Bottlenecks:** Categorizes bottlenecks (GPU Limited, CPU Limited, Thermal Rise) with confidence ratings and actionable in-game recommendations.

### 5. Frame Pacing Stabilizer & Diagnostics
- **Cadence Tracking:** Uses Android `Choreographer.FrameCallback` to calculate frame-time intervals, Average FPS, Median P50, P95, and P99 percentiles.
- **Micro-Stutter Counter:** Detects frames exceeding 1.5x nominal refresh intervals.
- **Interactive Timeline:** Real-time canvas line chart displaying frame-time variance over 1-minute and 5-minute sessions.

### 6. Safe ADB & Shizuku Command Suite
- **Strict Typed Allowlist:** Only executes pre-approved, safe system commands (`cmd game`, `settings put system peak_refresh_rate`, `pm trim-caches`).
- **Zero Anti-Cheat Tampering:** Rejects any arbitrary root execution, memory tampering, or file patching.
- **Audit Logging:** Every command execution and rollback is permanently recorded in the local Room audit log.

### 7. Benchmark & Comparison Suite
- **3-Column Comparative Matrix:** Side-by-side comparison of Balanced vs. Stable Competitive vs. Raw Performance Mode across Avg FPS, Avg Frame Time, P95 Stability, Stutter Rate, Battery Drain, and SoC Temperature.

---

## 🔒 Security & Anti-Cheat Policy
FrameForge GFX is engineered to comply strictly with fair-play rules and platform security:
- **No Memory Modification:** Never hooks, reads, or writes game memory or processes.
- **No Binary Patching:** Never modifies game APKs, `.obb` data, or Unreal Engine `.pak` files.
- **No False Anti-Ban Guarantees:** Employs standard public Android APIs and authorized ADB/Shizuku system settings.
- **Local Data Guarantee:** All telemetry, benchmarks, and settings backups remain strictly on-device.

---

## 🛠 Tech Stack
- **Language:** Kotlin 2.2
- **UI Framework:** Jetpack Compose with Material Design 3
- **Architecture:** MVVM + Clean Repositories
- **Local Database:** Room 2.7 (with KSP)
- **Preferences:** AndroidX DataStore Preferences
- **Diagnostics APIs:** `DisplayManager`, `PowerManager`, `BatteryManager`, `ActivityManager`, `SensorManager`, `Choreographer`
- **Testing:** JUnit 4 + Robolectric (JVM local testing)
