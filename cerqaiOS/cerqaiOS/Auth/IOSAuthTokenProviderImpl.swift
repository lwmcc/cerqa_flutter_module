//
// IOSAuthTokenProviderImpl.swift
// cerqaiOS
//
// iOS implementation of AuthTokenProvider using Amplify Swift
//

import Foundation
import Amplify
import Shared

/**
 * iOS implementation of AuthTokenProvider using Amplify Swift SDK.
 * This bridges your existing native Amplify Auth to the shared KMP code.
 */
@objc public class IOSAuthTokenProviderImpl: NSObject, AuthTokenProvider {

    public func getAccessToken() async throws -> String {
        do {
            let session = try await Amplify.Auth.fetchAuthSession()

            guard let cognitoTokenProvider = session as? AuthCognitoTokensProvider else {
                throw AuthenticationException(
                    message: "Session doesn't contain Cognito tokens",
                    cause: nil
                )
            }

            let tokens = try cognitoTokenProvider.getCognitoTokens().get()
            return tokens.accessToken

        } catch {
            throw AuthenticationException(
                message: "Failed to get access token: \(error.localizedDescription)",
                cause: KotlinThrowable(message: error.localizedDescription)
            )
        }
    }

    public func isAuthenticated() async throws -> KotlinBoolean {
        do {
            let session = try await Amplify.Auth.fetchAuthSession()
            return KotlinBoolean(value: session.isSignedIn)
        } catch {
            return KotlinBoolean(value: false)
        }
    }

    public func getCurrentUserId() async throws -> String? {
        do {
            let authUser = try await Amplify.Auth.getCurrentUser()
            return authUser.userId
        } catch {
            return nil
        }
    }
}

// Swift implementation of IOSAuthCallback for Kotlin interop
public class IOSAuthCallbackImpl: IOSAuthCallback {

    public func getAccessToken(completion: @escaping (String?, String?) -> Void) {
        Task {
            do {
                let session = try await Amplify.Auth.fetchAuthSession()

                guard let cognitoTokenProvider = session as? AuthCognitoTokensProvider else {
                    completion(nil, "Session doesn't contain Cognito tokens")
                    return
                }

                let tokens = try cognitoTokenProvider.getCognitoTokens().get()
                completion(tokens.accessToken, nil)

            } catch {
                completion(nil, "Failed to get access token: \(error.localizedDescription)")
            }
        }
    }

    public func isAuthenticated(completion: @escaping (KotlinBoolean) -> Void) {
        Task {
            do {
                let session = try await Amplify.Auth.fetchAuthSession()
                completion(KotlinBoolean(value: session.isSignedIn))
            } catch {
                completion(KotlinBoolean(value: false))
            }
        }
    }

    public func getCurrentUserId(completion: @escaping (String?) -> Void) {
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
