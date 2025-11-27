import Foundation
import Amplify
import AWSCognitoAuthPlugin

/// Swift helper class that bridges Kotlin with Amplify Swift SDK
@objc public class AmplifyHelper: NSObject {

    /// Configure Amplify with the amplify_outputs.json file
    @objc public static func configure() {
        do {
            // Load amplify_outputs.json from Resources
            guard let configURL = Bundle.main.url(forResource: "amplify_outputs", withExtension: "json") else {
                print("❌ Failed to find amplify_outputs.json in bundle")
                return
            }

            print("✅ Found amplify_outputs.json at: \(configURL.path)")

            guard let configData = try? Data(contentsOf: configURL) else {
                print("❌ Failed to read amplify_outputs.json")
                return
            }

            print("✅ Read amplify_outputs.json (\(configData.count) bytes)")

            // Add Cognito Auth plugin
            try Amplify.add(plugin: AWSCognitoAuthPlugin())

            // Configure with Gen 2 format (AmplifyOutputsData)
            let amplifyOutputs = try AmplifyOutputsData(configurationData: configData)
            try Amplify.configure(with: .amplifyOutputs(amplifyOutputs))

            print("✅ Amplify configured successfully")
        } catch {
            print("❌ Failed to configure Amplify: \(error)")
        }
    }

    /// Sign up a new user
    @objc public static func signUp(
        email: String,
        password: String,
        firstName: String?,
        lastName: String?,
        completion: @escaping (Bool, Bool, String?, String?) -> Void
    ) {
        Task {
            do {
                var userAttributes: [AuthUserAttribute] = [
                    AuthUserAttribute(.email, value: email)
                ]

                if let firstName = firstName {
                    userAttributes.append(AuthUserAttribute(.givenName, value: firstName))
                }
                if let lastName = lastName {
                    userAttributes.append(AuthUserAttribute(.familyName, value: lastName))
                }

                let signUpResult = try await Amplify.Auth.signUp(
                    username: email,
                    password: password,
                    options: .init(userAttributes: userAttributes)
                )

                let isConfirmed = signUpResult.isSignUpComplete
                let userId = signUpResult.userId

                completion(true, isConfirmed, userId, nil)
            } catch let error as AuthError {
                completion(false, false, nil, error.errorDescription)
            } catch {
                completion(false, false, nil, error.localizedDescription)
            }
        }
    }

    /// Confirm sign up with verification code
    @objc public static func confirmSignUp(
        email: String,
        code: String,
        completion: @escaping (Bool, String?) -> Void
    ) {
        Task {
            do {
                let confirmResult = try await Amplify.Auth.confirmSignUp(
                    for: email,
                    confirmationCode: code
                )

                if confirmResult.isSignUpComplete {
                    completion(true, nil)
                } else {
                    completion(false, "Sign up not complete")
                }
            } catch let error as AuthError {
                completion(false, error.errorDescription)
            } catch {
                completion(false, error.localizedDescription)
            }
        }
    }

    /// Sign in a user
    @objc public static func signIn(
        email: String,
        password: String,
        completion: @escaping (Bool, String?, String?, String?) -> Void
    ) {
        Task {
            do {
                let signInResult = try await Amplify.Auth.signIn(
                    username: email,
                    password: password
                )

                if signInResult.isSignedIn {
                    // Get user attributes
                    let userAttributes = try await Amplify.Auth.fetchUserAttributes()
                    let userEmail = userAttributes.first(where: { $0.key == .email })?.value

                    // Get user ID
                    let userId = try await Amplify.Auth.getCurrentUser().userId

                    completion(true, userId, userEmail ?? email, nil)
                } else {
                    completion(false, nil, nil, "Sign in incomplete")
                }
            } catch let error as AuthError {
                completion(false, nil, nil, error.errorDescription)
            } catch {
                completion(false, nil, nil, error.localizedDescription)
            }
        }
    }

    /// Sign out the current user
    @objc public static func signOut(completion: @escaping (Bool, String?) -> Void) {
        Task {
            do {
                _ = await Amplify.Auth.signOut()
                completion(true, nil)
            } catch let error as AuthError {
                completion(false, error.errorDescription)
            } catch {
                completion(false, error.localizedDescription)
            }
        }
    }

    /// Get the currently signed-in user
    @objc public static func getCurrentUser() -> (Bool, String?, String?, String?) {
        do {
            let user = try Amplify.Auth.getCurrentUser()
            // Note: This is synchronous, so we can't fetch attributes here
            // The user ID and username are available synchronously
            return (true, user.userId, nil, user.username)
        } catch {
            return (false, nil, nil, nil)
        }
    }

    /// Resend confirmation code
    @objc public static func resendConfirmationCode(
        email: String,
        completion: @escaping (Bool, String?) -> Void
    ) {
        Task {
            do {
                _ = try await Amplify.Auth.resendSignUpCode(for: email)
                completion(true, nil)
            } catch let error as AuthError {
                completion(false, error.errorDescription)
            } catch {
                completion(false, error.localizedDescription)
            }
        }
    }

    /// Reset password
    @objc public static func resetPassword(
        email: String,
        completion: @escaping (Bool, String?) -> Void
    ) {
        Task {
            do {
                _ = try await Amplify.Auth.resetPassword(for: email)
                completion(true, nil)
            } catch let error as AuthError {
                completion(false, error.errorDescription)
            } catch {
                completion(false, error.localizedDescription)
            }
        }
    }

    /// Confirm password reset
    @objc public static func confirmResetPassword(
        email: String,
        newPassword: String,
        code: String,
        completion: @escaping (Bool, String?) -> Void
    ) {
        Task {
            do {
                try await Amplify.Auth.confirmResetPassword(
                    for: email,
                    with: newPassword,
                    confirmationCode: code
                )
                completion(true, nil)
            } catch let error as AuthError {
                completion(false, error.errorDescription)
            } catch {
                completion(false, error.localizedDescription)
            }
        }
    }
}
