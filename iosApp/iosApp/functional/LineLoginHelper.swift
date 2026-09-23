import Foundation
import LineSDK
import ComposeApp

class LineLoginHelper: SwiftLineAuth {
    
    func login(onSuccess: @escaping (LineUser) -> Void, onError: @escaping (String) -> Void) {
        DispatchQueue.main.async {

            guard let rootVC = UIApplication.shared.connectedScenes
                .compactMap({ $0 as? UIWindowScene })
                .flatMap(\.windows)
                .first(where: { $0.isKeyWindow })?
                .rootViewController else {
                onError("Cannot find root view controller")
                return
            }

            var parameters = LoginManager.Parameters()
            parameters.botPromptStyle = .aggressive
            LoginManager.shared.login(
                permissions: [.profile, .openID, .email],
                in: rootVC,
                parameters: parameters
            ) { result in
                switch result {
                case .success(let loginResult):
                    let profile = loginResult.userProfile
                    let user = LineUser(
                        userId: profile?.userID ?? "",
                        displayName: profile?.displayName ?? "",
                        pictureUrl: profile?.pictureURL?.absoluteString,
                        accessToken: loginResult.accessToken.value,
                        statusMessage: nil,
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
