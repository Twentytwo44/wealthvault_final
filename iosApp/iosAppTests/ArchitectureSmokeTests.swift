import XCTest

/// Keeps the iOS workspace testable even when the simulator has no seeded
/// account data. Feature behavior is covered by the shared Kotlin tests; this
/// target verifies that the native test host can load on the simulator.
final class ArchitectureSmokeTests: XCTestCase {
    func testWorkspaceCanRunOnSimulator() {
        XCTAssertTrue(Bundle.main.bundleIdentifier?.isEmpty == false)
    }
}
