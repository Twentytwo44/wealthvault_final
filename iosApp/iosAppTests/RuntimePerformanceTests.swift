import XCTest

/// Keeps the iOS runtime budget wired to XCTest's measured result bundle.
/// The CI exporter reads the five-run metrics from `xcresulttool`; no timing
/// values are hard-coded in source. The test intentionally uses the same
/// production app target that the smoke test builds.
final class RuntimePerformanceTests: XCTestCase {
    func testLaunchAndScrollMetrics() {
        let app = XCUIApplication()
        app.launchArguments += ["-wealthvault-performance"]
        let options = XCTMeasureOptions()
        options.iterationCount = 5

        measure(
            metrics: [
                XCTApplicationLaunchMetric(),
                XCTMemoryMetric(),
                XCTOSSignpostMetric.scrollingAndDecelerationMetric,
                XCTOSSignpostMetric(
                    subsystem: "com.wealthvault",
                    category: "Performance",
                    name: "WealthVaultScroll",
                ),
            ],
            options: options,
        ) {
            app.launch()
            defer { app.terminate() }
            let window = app.windows.firstMatch
            guard window.waitForExistence(timeout: 10) else {
                XCTFail("The production app did not present a window")
                return
            }
            let scrollView = app.scrollViews["wealthvault-performance-scroll"]
            XCTAssertTrue(
                scrollView.waitForExistence(timeout: 5),
                "The performance scroll probe did not appear.",
            )
            for _ in 0..<3 {
                scrollView.swipeUp()
            }
        }
    }
}
