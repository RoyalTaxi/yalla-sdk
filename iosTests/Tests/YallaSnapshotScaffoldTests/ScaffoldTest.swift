import XCTest
import SnapshotTesting

/// Phase 3 scaffold. Confirms the swift-snapshot-testing SPM dependency resolves.
///
/// Real primitive + composite snapshots land in Phase 4 of the v1.0 launch,
/// paired with Roborazzi on Android. Tolerance per ADR (TBD Phase 4):
/// Android 0.1%, iOS 1%.
final class ScaffoldTest: XCTestCase {
    func testSnapshotTestingLoads() {
        // Sanity-check that the SnapshotTesting module was linked.
        let hostingBundle = Bundle(for: ScaffoldTest.self)
        XCTAssertNotNil(hostingBundle)
    }
}
