import Amplify
import Authenticator
import AWSAPIPlugin
import AWSCognitoAuthPlugin
import AWSPluginsCore
import SwiftUI
import Shared

// Swift implementation of IOSAuthCallback for Kotlin interop
class IOSAuthCallbackImpl: IOSAuthCallback {

    func getAccessToken(completion: @escaping (String?, String?) -> Void) {
        Task {
            do {
                let session = try await Amplify.Auth.fetchAuthSession()

                // Amplify Swift API to fetch Cognito tokens
                if let cognitoTokensProvider = session as? AuthCognitoTokensProvider {
                    let tokens = try cognitoTokensProvider.getCognitoTokens().get()
                    completion(tokens.accessToken, nil)
                } else {
                    completion(nil, "Session doesn't contain Cognito tokens")
                }

            } catch {
                completion(nil, "Failed to get access token: \(error.localizedDescription)")
            }
        }
    }

    func isAuthenticated(completion: @escaping (KotlinBoolean) -> Void) {
        Task {
            do {
                let session = try await Amplify.Auth.fetchAuthSession()
                completion(KotlinBoolean(value: session.isSignedIn))
            } catch {
                completion(KotlinBoolean(value: false))
            }
        }
    }

    func getCurrentUserId(completion: @escaping (String?) -> Void) {
        Task {
            do {
                let authUser = try await Amplify.Auth.getCurrentUser()
                completion(authUser.userId)
            } catch {
                completion(nil)
            }
        }
    }
}

@main
struct cerqaiOSApp: App {
    init() {
        KoinHelperKt.doInitKoin()

        do {
            try Amplify.add(plugin: AWSCognitoAuthPlugin())
            try Amplify.configure(with: .amplifyOutputs)
        } catch {
            print("Unable to configure Amplify \(error)")
        }

        let authProvider = IOSModuleKt.getIOSAuthTokenProviderInstance()
        let authCallback = IOSAuthCallbackImpl()
        IOSAuthTokenProviderKt.setIOSAuthCallback(provider: authProvider, callback: authCallback)
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
