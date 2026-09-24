import XCTest

/// Keeps the iOS workspace testable even when the simulator has no seeded
/// account data. Feature behavior is covered by the shared Kotlin tests; this
/// target verifies that the native test host can load on the simulator.
final class ArchitectureSmokeTests: XCTestCase {
    func testWorkspaceCanRunOnSimulator() {
        XCTAssertTrue(Bundle.main.bundleIdentifier?.isEmpty == false)
    }

    /// Launches the production application so every iOS XCTest run exercises
    /// the real Compose entry point instead of only loading the test bundle.
    func testProductionApplicationLaunches() {
        let app = XCUIApplication()
        app.launchArguments += ["-ui-testing"]
        app.launch()
        XCTAssertTrue(
            app.windows.firstMatch.waitForExistence(timeout: 15),
            "The production iOS app did not present a window after launch.",
        )
        app.terminate()
    }
}
