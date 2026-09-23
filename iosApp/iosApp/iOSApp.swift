import SwiftUI
import LineSDK
import ComposeApp
import os.signpost

@main
struct iOSApp: App {
    // สร้าง Helper ครั้งเดียวตอนแอปเปิด
    let lineHelper = LineLoginHelper()

    init() {
        StartupSignpost.shared.begin()
        // ตั้งค่า LINE SDK
        LoginManager.shared.setup(channelID: "2009343103", universalLinkURL: nil)
    }

    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    var body: some Scene {
        WindowGroup {
            // ส่ง Helper เข้าไปใน ContentView
            ContentView(lineAuth: lineHelper)
        }
    }
}

/// Emits a stable Instruments/XCTest startup interval without logging user
/// data. The interval ends when the first Compose root is visible.
final class StartupSignpost {
    static let shared = StartupSignpost()

    private let log = OSLog(
        subsystem: Bundle.main.bundleIdentifier ?? "com.wealthvault.app",
        category: .pointsOfInterest,
    )
    private var signpostID: OSSignpostID?

    private init() {}

    func begin() {
        guard signpostID == nil else { return }
        let id = OSSignpostID(log: log)
        signpostID = id
        os_signpost(.begin, log: log, name: "AppStartup", signpostID: id)
    }

    func end() {
        guard let id = signpostID else { return }
        os_signpost(.end, log: log, name: "AppStartup", signpostID: id)
        signpostID = nil
    }
}
