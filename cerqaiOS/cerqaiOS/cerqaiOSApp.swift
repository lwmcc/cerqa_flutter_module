import Amplify
import Authenticator
import AWSAPIPlugin
import AWSCognitoAuthPlugin
import AWSPluginsCore
import SwiftUI
import Shared
//import FirebaseCore
//import FirebaseMessaging
import UserNotifications

// Swift implementation of IOSAuthCallback for Kotlin interop
class IOSAuthCallbackImpl: IOSAuthCallback {

    func getAccessToken(completion: @escaping (String?, String?) -> Void) {
        Task {
            do {
                let session = try await Amplify.Auth.fetchAuthSession()

                // Amplify Swift API to fetch Cognito tokens
                if let cognitoTokensProvider = session as? AuthCognitoTokensProvider {
                    let tokens = try cognitoTokensProvider.getCognitoTokens().get()
                    DispatchQueue.main.async {
                        completion(tokens.accessToken, nil)
                    }
                } else {
                    DispatchQueue.main.async {
                        completion(nil, "Session doesn't contain Cognito tokens")
                    }
                }

            } catch {
                DispatchQueue.main.async {
                    completion(nil, "Failed to get access token: \(error.localizedDescription)")
                }
            }
        }
    }

    func isAuthenticated(completion: @escaping (KotlinBoolean) -> Void) {
        Task {
            do {
                let session = try await Amplify.Auth.fetchAuthSession()
                DispatchQueue.main.async {
                    completion(KotlinBoolean(value: session.isSignedIn))
                }
            } catch {
                DispatchQueue.main.async {
                    completion(KotlinBoolean(value: false))
                }
            }
        }
    }

    func getCurrentUserId(completion: @escaping (String?) -> Void) {
        Task {
            do {
                let authUser = try await Amplify.Auth.getCurrentUser()
                print("IOSAuthCallbackImpl: Got userId: \(authUser.userId)")
                DispatchQueue.main.async {
                    completion(authUser.userId)
                }
            } catch {
                print("IOSAuthCallbackImpl: Failed to get userId: \(error.localizedDescription)")
                DispatchQueue.main.async {
                    completion(nil)
                }
            }
        }
    }
}

// AppDelegate to handle push notifications
// TODO: Uncomment this when enrolled in Apple Developer Program ($99/year)
// Push notifications require a paid Apple Developer account
class AppDelegate: NSObject, UIApplicationDelegate /*, UNUserNotificationCenterDelegate, MessagingDelegate */ {

    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {

        // TODO: Uncomment when you have a paid Apple Developer account
        // Push Notifications require paid Apple Developer Program membership

        /*
        // Set up notification center delegate
        UNUserNotificationCenter.current().delegate = self

        // Set Firebase messaging delegate
        Messaging.messaging().delegate = self

        // Request notification permissions
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { granted, error in
            if let error = error {
                print("Error requesting notification authorization: \(error.localizedDescription)")
            } else {
                print("Notification authorization granted: \(granted)")
            }
        }

        // Register for remote notifications
        application.registerForRemoteNotifications()
        */

        return true
    }

    /* TODO: Uncomment when you have a paid Apple Developer account

    // Called when APNs successfully registers the device
    func application(_ application: UIApplication,
                     didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        print("APNs device token: \(deviceToken.map { String(format: "%02.2hhx", $0) }.joined())")

        // Forward APNs token to Firebase
        Messaging.messaging().apnsToken = deviceToken
    }

    // Called when registration fails
    func application(_ application: UIApplication,
                     didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("Failed to register for remote notifications: \(error.localizedDescription)")
    }

    // MARK: - UNUserNotificationCenterDelegate

    // Handle notification when app is in foreground
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                               willPresent notification: UNNotification,
                               withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        print("Notification received in foreground: \(notification.request.content.userInfo)")

        // Show notification even when app is in foreground
        completionHandler([.banner, .sound, .badge])
    }

    // Handle notification tap
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                               didReceive response: UNNotificationResponse,
                               withCompletionHandler completionHandler: @escaping () -> Void) {
        print("Notification tapped: \(response.notification.request.content.userInfo)")
        completionHandler()
    }

    // MARK: - MessagingDelegate

    // Called when FCM token is updated
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        print("Firebase registration token: \(String(describing: fcmToken))")

        // Send token to your server or use it as needed
        if let token = fcmToken {
            print("FCM Token updated: \(token)")
        }
    }
    */
}

@main
struct cerqaiOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    init() {
        // Initialize Firebase
       // FirebaseApp.configure()

        // Initialize KMP Koin
        KoinHelperKt.doInitKoin()

        // Configure Amplify
        do {
            try Amplify.add(plugin: AWSCognitoAuthPlugin())
            try Amplify.configure(with: .amplifyOutputs)
        } catch {
            print("Unable to configure Amplify \(error)")
        }

        // Setup Auth callback
        let authProvider = IOSModuleKt.getIOSAuthTokenProviderInstance()
        let authCallback = IOSAuthCallbackImpl()
        IOSAuthTokenProviderKt.setIOSAuthCallback(provider: authProvider, callback: authCallback)

        // Setup FCM token provider for KMP
        IosFcmTokenProvider.companion.setNativeTokenProvider { callback in
           // Messaging.messaging().token { token, error in
           //     if let error = error {
           //         print("Error fetching FCM token: \(error.localizedDescription)")
           //         callback(nil)
           //     } else {
           //         callback(token)
           //     }
           // }
        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
