import SwiftUI
import LineSDK
import ComposeApp

@main
struct iOSApp: App {
    // สร้าง Helper ครั้งเดียวตอนแอปเปิด
    let lineHelper = LineLoginHelper()

    init() {
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
