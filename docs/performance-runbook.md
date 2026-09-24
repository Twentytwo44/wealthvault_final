# Fixed-runner performance runbook

This runbook is the reproducible path for the remaining runtime acceptance
metrics. It must run on the configured self-hosted macOS ARM64 runner; local
host-test success is not a substitute for these measurements.

## Preconditions

- Java 21, the repository Gradle wrapper, Xcode, CocoaPods, `adb`, and Python 3
  are installed.
- `adb devices` reports at least one entry whose state is exactly `device`.
- `xcrun simctl list devices available` exposes the simulator named by
  `IOS_SIMULATOR_DESTINATION` (the workflow default is iPhone 16 Pro).
- The runner has no stale test process holding the app package or simulator
  test bundle. Reboot/reset the test device if a prior run left one behind.

The run is invalid if a precondition fails. Do not create a properties file
with a guessed value to get past the collector.

## Local reproduction

```shell
./gradlew verifyArchitecture verifyDatabaseMigrations -PstrictArchitecture=true --no-daemon
./gradlew verifyUsability :benchmarks:assembleBenchmarkBenchmark --no-daemon
adb devices
./gradlew :androidApp:connectedDebugAndroidTest --no-daemon
./gradlew :benchmarks:connectedBenchmarkAndroidTest --no-daemon
```

Copy the generated AndroidX `benchmarkData.json` (including a prefixed
`*-benchmarkData.json` name) into an input directory and export the measured
Android rows:

```shell
mkdir -p build/performance/inputs build/performance/android-benchmark
python3 tools/collect_android_performance.py \
  --input build/performance/android-benchmark \
  --output build/performance/inputs/android-benchmark.properties
python3 tools/measure_android_navigation_memory.py \
  --package com.wealthvault.wealthvault_final \
  --component com.wealthvault.wealthvault_final/com.wealthvault.app.BenchmarkDashboardActivity \
  --runs 5 --loops 10 \
  --output build/performance/inputs/android-navigation.properties
```

The iOS job builds and tests the CocoaPods workspace. When the explicit
performance flag is enabled, the XCTest result bundle is parsed by the iOS
hitch exporter:

```shell
./gradlew :composeApp:generateDummyFramework :composeApp:podspec --no-daemon
pod install --project-directory=iosApp
export IOS_SIMULATOR_DESTINATION='platform=iOS Simulator,name=iPhone 16 Pro'
export WEALTHVAULT_RUN_PERFORMANCE=1
xcodebuild -workspace iosApp/iosApp.xcworkspace -scheme iosApp \
  -configuration Debug -sdk iphonesimulator \
  -destination "$IOS_SIMULATOR_DESTINATION" \
  -resultBundlePath build/performance/ios-tests.xcresult \
  -enableCodeCoverage YES test
python3 tools/export_ios_runtime_performance.py \
  --result-bundle build/performance/ios-tests.xcresult \
  --minimum-samples 5 \
  --output build/performance/inputs/ios-runtime.properties
```

Add the five-run framework-size and Gradle timing exports produced by the
workflow, then aggregate and verify all metrics:

```shell
python3 tools/measure_ios_framework_size.py \
  --framework composeApp/build/bin/iosSimulatorArm64/podDebugFramework/ComposeApp.framework \
  --runs 5 --output build/performance/inputs/ios-framework.properties
python3 tools/measure_gradle_performance.py --runs 5 \
  --measurement gradle_configuration_ms help \
  --measurement warm_incremental_build_ms :androidApp:compileDebugKotlin \
  --measurement clean_android_build_ms clean :androidApp:assembleRelease \
  --gradle-arg=--no-daemon \
  --output build/performance/inputs/android-build.properties
python3 tools/measure_gradle_performance.py --runs 5 \
  --measurement ios_simulator_link_ms :composeApp:linkPodDebugFrameworkIosSimulatorArm64 \
  --gradle-arg=--no-daemon \
  --output build/performance/inputs/ios-build.properties
./gradlew collectPerformanceMetrics verifyPerformanceBudgets \
  -PperformanceInputs=build/performance/inputs \
  -PperformanceMetrics=build/performance/metrics.properties \
  -PstrictPerformance=true --no-daemon
```

The collector requires every key in
`performance/performance-budgets.properties`, at least five samples, and
finite non-negative values. The first successful `main` run bootstraps the iOS
framework-size baseline; later runs pass the downloaded main metrics file as
`-PperformanceBaselineMetrics=...` to enable the ten-percent regression
ratchet. A suspected regression must be confirmed with a second five-run file.

The GitHub Actions workflow performs the same steps when
`workflow_dispatch` is started with `run_performance=true`, and uploads the
resulting `performance-metrics` artifact for subsequent main-branch runs.
