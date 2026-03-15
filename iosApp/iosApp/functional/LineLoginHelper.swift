import Foundation
import LineSDK
import ComposeApp // 🟢 มองเห็น Kotlin แน่นอน

// 🟢 เปลี่ยนมารับ Interface ที่เราสร้างไว้ใน iosMain
class LineLoginHelper: SwiftLineAuth {
    
    func login(onSuccess: @escaping (LineUser) -> Void, onError: @escaping (String) -> Void) {
        DispatchQueue.main.async {
            
            guard let rootVC = UIApplication.shared.windows.first(where: { $0.isKeyWindow })?.rootViewController else {
                onError("Cannot find root view controller")
                return
            }
            
            // 🟢 เรียก LoginManager แล้วใส่ options: [.botPromptAggressive] เข้าไปตรงๆ เลยครับ
            LoginManager.shared.login(
                permissions: [.profile, .openID, .email],
                in: rootVC,
                options: [.botPromptAggressive] // 👈 ใส่การตั้งค่าแอดเพื่อนตรงนี้จบเลย!
            ) { result in
                switch result {
                case .success(let loginResult):
                    let profile = loginResult.userProfile
                    let user = LineUser(
                        userId: profile?.userID ?? "",
                        displayName: profile?.displayName ?? "",
                        pictureUrl: profile?.pictureURL?.absoluteString,
                        accessToken: loginResult.accessToken.value,
                        idToken: loginResult.accessToken.IDTokenRaw
                    )
                    onSuccess(user)
                    
                case .failure(let error):
                    onError(error.localizedDescription)
                }
            }
        }
    }
}
