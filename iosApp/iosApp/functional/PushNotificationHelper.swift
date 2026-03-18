//import Foundation
//import FirebaseCore
//import FirebaseMessaging
//import ComposeApp // เปลี่ยนเป็นชื่อ Shared Module ของคุณ
//
//class IosPushNotificationHelper: NSObject, PushNotificationHelper {
//    func getFcmToken(onSuccess: @escaping (String) -> Void, onError: @escaping (String) -> Void) {
//        Messaging.messaging().token { token, error in
//            if let error = error {
//                onError(error.localizedDescription)
//            } else if let token = token {
//                onSuccess(token)
//            }
//        }
//    }
//}
