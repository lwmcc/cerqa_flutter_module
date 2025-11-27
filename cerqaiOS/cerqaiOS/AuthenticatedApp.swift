//
// AuthenticatedApp.swift
// cerqaiOS
//
// Wrapper that shows auth screen or main app based on authentication state
//

import SwiftUI
import Shared

struct AuthenticatedApp: View {
    @StateObject private var authObserver = AuthStateObserver()

    var body: some View {
        Group {
            switch authObserver.authState {
            case .loading:
                VStack {
                    ProgressView()
                    Text("Loading...")
                        .padding(.top, 8)
                }

            case .unauthenticated:
                // Show Compose auth screen
                ComposeAuthView()

            case .authenticated:
                // Show main app
                RootView()

            case .error(let message):
                VStack {
                    Text("Error")
                        .font(.headline)
                    Text(message)
                        .font(.caption)
                        .foregroundColor(.red)
                        .padding()
                }
            }
        }
    }
}

// SwiftUI wrapper for the Compose auth screen
struct ComposeAuthView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        // Get the AuthViewModel from Koin
        let koin = KoinHelperKt.koin
        let authViewModel = koin.get(objCClass: AuthViewModel.self) as! AuthViewModel

        // Create the Compose auth screen
        return MainViewControllerKt.createAuthViewController(
            authViewModel: authViewModel,
            onAuthSuccess: {
                // This will be handled by the state observer
                print("Auth success!")
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // No updates needed
    }
}

// Observable object that watches the auth state from Kotlin
class AuthStateObserver: ObservableObject {
    @Published var authState: AuthStateWrapper = .loading

    private var stateWatcher: Closeable?

    init() {
        startObserving()
    }

    private func startObserving() {
        let koin = KoinHelperKt.koin
        let authService = koin.get(objCClass: AuthService.self) as! AuthService

        // Watch the auth state flow
        stateWatcher = authService.authState.watch { [weak self] state in
            DispatchQueue.main.async {
                self?.authState = AuthStateWrapper.from(state: state)
            }
        }
    }

    deinit {
        stateWatcher?.close()
    }
}

// Swift wrapper for Kotlin AuthState
enum AuthStateWrapper {
    case loading
    case unauthenticated
    case authenticated(user: AuthUser)
    case error(message: String)

    static func from(state: AuthState?) -> AuthStateWrapper {
        guard let state = state else { return .loading }

        if state is AuthState.Loading {
            return .loading
        } else if state is AuthState.Unauthenticated {
            return .unauthenticated
        } else if let authState = state as? AuthState.Authenticated {
            return .authenticated(user: authState.user)
        } else if let errorState = state as? AuthState.Error {
            return .error(message: errorState.message)
        }

        return .loading
    }
}
