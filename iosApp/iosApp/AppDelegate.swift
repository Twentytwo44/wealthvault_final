import UIKit
import ComposeApp
import FirebaseCore
import FirebaseMessaging
import UserNotifications

class AppDelegate: NSObject, UIApplicationDelegate, MessagingDelegate, UNUserNotificationCenterDelegate {
    /// The shared KMP push helper reads this value from the host boundary.
    /// Keep the key stable so a token refresh does not require a second
    /// platform-specific storage implementation.
    static let sharedTokenDefaultsKey = "wealthvault.push.fcm_token"

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil,
    ) -> Bool {
        // Firebase must not be configured without a project plist. This keeps
        // simulator/debug builds usable while still enabling push in a signed
        // build that provides GoogleService-Info.plist.
        guard let plistPath = Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist"),
              let options = FirebaseOptions(contentsOfFile: plistPath) else {
            return true
        }

        if FirebaseApp.app() == nil {
            FirebaseApp.configure(options: options)
        }
        Messaging.messaging().delegate = self
        UNUserNotificationCenter.current().delegate = self
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { _, _ in }
        application.registerForRemoteNotifications()
        return true
    }

    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Messaging.messaging().apnsToken = deviceToken
    }

    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        guard let fcmToken, !fcmToken.isEmpty else {
            UserDefaults.standard.removeObject(forKey: Self.sharedTokenDefaultsKey)
            return
        }
        UserDefaults.standard.set(fcmToken, forKey: Self.sharedTokenDefaultsKey)

        // If shared composition is ready, persist and submit the rotated token
        // immediately. When this callback arrives earlier, the UserDefaults
        // value above is consumed by the authenticated startup registrar.
        KoinHelperKt.notifyIosPushTokenChanged(token: fcmToken)
    }
}
