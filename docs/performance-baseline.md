# Performance baseline

Runtime measurement is intentionally deferred while functional migration and
end-to-end usability are being stabilized. The collection/export tooling stays
available, but pending runtime values do not block this usability checkpoint.

This file is the measurement contract for the staged architecture migration. A
release build, a warmed device/simulator, and five repetitions are required
before changing a budget. The `benchmarks` module now provides five-iteration
Android cold/warm Macrobenchmarks (startup, frame timing, and peak memory) and
a Baseline Profile target; a runner must still execute them
on a fixed device and pass the resulting properties to
`verifyPerformanceBudgets -PstrictPerformance=true` before runtime values are
marked as measured. The verifier also supports a ten-percent regression
ratchet against a main-branch metrics export; a suspected regression must be
confirmed by a second fresh run before CI fails.

## Current checkpoint

- Branch: `optimize-v1`
- Android debug APK: 52,735,415 bytes (50.3 MiB)
- Android release APK: 26,086,488 bytes (24.9 MiB), unsigned, R8 + resource shrinking enabled
- Android benchmark APK uses a separate non-debuggable, debug-signed `benchmark`
  build type so Macrobenchmark can install it without requiring production
  signing material; it is not used for release distribution.
- `androidApp:verifyReleaseApkSize` exports the measured size to
  `androidApp/build/performance/artifacts.properties` for the CI aggregator.
- Cold start P95: 890.119 ms (five runs on the fixed API 35 emulator; provisional)
- Warm start P95: 394.922 ms (five runs on the fixed API 35 emulator; provisional)
- Peak heap medians: cold 7,874 KiB, warm 13,295 KiB (same provisional run)
- Dashboard time-to-content: pending on-device trace
- Android scrolling benchmark is implemented (including frame timing and peak
  memory); fixed-runner export and iOS hitch signposts remain pending
- Local five-run probes measured the Gradle configuration phase at 4,133 ms
  (under the 5 s budget) and iOS simulator framework linking at 12,144.115 ms
  (under the 120 s budget). Clean/incremental builds and runtime metrics still
  require the fixed CI runner.

The pull-request workflow keeps measurement collection separate from the
fixed-device jobs. Android and iOS upload their exporter properties as
artifacts, then one Ubuntu aggregation job downloads both platforms, runs
`collectPerformanceMetrics`/`collectCoverageMetrics`, and applies the strict
gates. This prevents either platform job from passing before the other
platform's measurements have been included. The aggregation job is expected to
fail until the self-hosted runners publish real five-run properties; the
release APK size file alone is intentionally insufficient.

The Android job enables AndroidX Macrobenchmark's `benchmarkData.json` output
and converts its cold-start, warm-start, peak-memory, and startup frame-overrun
samples with `tools/collect_android_performance.py`. A prefixed report name such as
`com.example-benchmarkData.json` is accepted as well as the bare filename.
Frame overrun from the startup scenario is a diagnostic proxy, not the final
scrolling-jank gate; metrics that require an app trace, a scrolling scenario,
or a navigation loop must be supplied as a measured supplement properties file.
The converter refuses placeholders and the aggregator refuses missing keys.

The fixed runner also executes `tools/measure_gradle_performance.py` five times
for Gradle configuration, warm incremental compilation, and a clean Android
release build. The iOS job uses the same exporter for five CocoaPods framework
link runs (`:composeApp:linkPodDebugFrameworkIosSimulatorArm64`). These timings
are uploaded as separate measured property files and are merged only after both
platform jobs finish. The iOS app workspace is built through the generated
`composeApp` podspec before the shared framework tests run.

To validate the contract locally without device metrics:

```shell
./gradlew :benchmarks:compileBenchmarkSources :benchmarks:assembleBenchmark
./gradlew verifyPerformanceBudgets
```

The second command intentionally reports `pending` until
`build/performance/metrics.properties` exists. CI should run the connected
launch smoke test and benchmark on the fixed runner, then invoke the strict
form of the task. The workflow fails fast when no usable Android device is
attached instead of waiting for the connected-test timeout.

Measured runners can normalize their exports with:

```shell
./gradlew collectPerformanceMetrics \
  -PperformanceInputs=build/performance/inputs
```

Each input must be a `.properties` file containing `sample_count` (at least
five) and the canonical keys in `performance/performance-budgets.properties`.
The collector rejects missing keys and conflicting duplicate values and never
creates placeholder metrics. The resulting file is then consumed by the
strict verifier.

Coverage uses the same report-driven policy. `verifyCoverage` checks line
coverage for domain/data/reducers at 85% and branch coverage for money, auth,
session refresh, and cache migration at 95%. Android host-test coverage is
measured with Kover 0.9.9 by `koverXmlReportAndroidAll` and exported with
`tools/export_kover_coverage.py`; the local strict gate passes for the migrated
scope. The shared iOS XCTest target produces an `xccov` result consumed by
`tools/export_ios_xctest_coverage.py`; its Kotlin/Native application coverage
remains informational until the framework is built with LLVM instrumentation.

Combine the Android Kover and iOS XCTest exporter properties with:

```shell
./gradlew collectCoverageMetrics \
  -PcoverageInputs=build/coverage/inputs
```

Coverage values must be percentages and every key in
`performance/coverage-budgets.properties` must be present; the collector
rejects missing or conflicting values instead of averaging incomparable
reports.

For a measured regression check, pass both the main-branch baseline and a
second run when the first run exceeds the tolerance:

```shell
./gradlew verifyPerformanceBudgets \
  -PstrictPerformance=true \
  -PperformanceMetrics=build/performance/metrics.properties \
  -PperformanceBaselineMetrics=performance/main-metrics.properties \
  -PperformanceConfirmationMetrics=build/performance/metrics-confirmation.properties
```

## Required benchmark rows

| Metric | Budget | Baseline (5-run median/P95) | Status |
| --- | ---: | ---: | --- |
| Cold start P95 | ≤ 2.0 s | 890.119 ms (emulator) | provisional pass |
| Warm start P95 | ≤ 0.8 s | 394.922 ms (emulator) | provisional pass |
| Cached dashboard content | ≤ 300 ms | pending | pending |
| Android jank | < 5% | dedicated scroll benchmark implemented; fixed-runner export pending | pending |
| iOS hitch time | < 5 ms/s | pending | pending |
| Retained memory after 10 navigation loops | ≤ +10 MB | pending | pending |
| Android release APK | ≤ 35 MB | 26,086,488 bytes | pass (local release build) |
| iOS framework growth | ≤ +10% | pending | pending |
| Gradle configuration | ≤ 5 s | 4.133 s | pass (local five-run probe) |
| Warm incremental build | ≤ 20 s | pending | pending |
| Clean Android build | ≤ 90 s | pending | pending |
| iOS simulator link | ≤ 120 s | 12.144 s | pass (local five-run probe) |
