This is a Kotlin Multiplatform project targeting Android, iOS.

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
    folder is the appropriate location.

* [/iosApp](./iosApp/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :androidApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :androidApp:assembleDebug
  ```

The Android application shell lives in `/androidApp`; `/composeApp` is now the
shared Compose library. Keep the Firebase configuration file at
`androidApp/google-services.json` for local Android builds. Release signing is
configured with the `releaseStoreFile`, `releaseStorePassword`,
`releaseKeyAlias`, and `releaseKeyPassword` Gradle properties (or their
`RELEASE_*` environment variable equivalents).

### Build and Run iOS Application

Prepare the generated ComposeApp pod and workspace, then open the workspace in Xcode:

```shell
./gradlew :composeApp:generateDummyFramework :composeApp:podspec
pod install --project-directory=iosApp
open iosApp/iosApp.xcworkspace
```

The CocoaPods workspace owns framework integration; the Xcode project should not
invoke a separate Kotlin `embedAndSign` build phase.

### Verification and measured gates

For the normal usability-first checkpoint, run the deterministic gate below.
It checks dependency boundaries, database migrations, Android debug/release
builds, host tests, and active device-test contracts without requiring a
connected device or runtime benchmark:

```shell
./gradlew verifyUsability
./gradlew verifyUsabilityIos   # macOS/Xcode runner
```

The architecture ratchet and Android Kover exporter run with:

```shell
./gradlew verifyArchitecture -PstrictArchitecture=true
./gradlew verifyDatabaseMigrations
./gradlew koverXmlReportAndroidAll
python3 tools/export_kover_coverage.py \
  --output build/coverage/inputs/android-kover.properties
```

The iOS workspace contains a shared `iosAppTests` XCTest target. Run it with
coverage on a simulator and export the measured `xccov` result with:

```shell
xcodebuild -workspace iosApp/iosApp.xcworkspace -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 16 Pro' \
  -resultBundlePath /tmp/wealthvault-ios-tests.xcresult \
  -enableCodeCoverage YES test
python3 tools/export_ios_xctest_coverage.py \
  --result-bundle /tmp/wealthvault-ios-tests.xcresult \
  --output build/coverage/inputs/ios-xctest.properties
```

Coverage and performance collectors reject missing, conflicting, or placeholder
measurements. Normal pull requests use the usability and strict coverage gates;
the full performance budget gate runs from the manual `run_performance`
workflow input once fixed-device measurements are available.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…# wealthvault_final
