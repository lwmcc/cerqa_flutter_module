import SwiftUI
import Authenticator
import Shared

struct ContentView: View {
    var body: some View {
        Authenticator { state in
            VStack {
                // Compose Multiplatform UI
                ComposeAppView()
                    .frame(maxWidth: .infinity, maxHeight: .infinity)

               /*  Button("Sign out") {
                    Task {
                        await state.signOut()
                    }
                }
                .padding() */
            }
        }
    }
}

// SwiftUI wrapper for Compose Multiplatform App
struct ComposeAppView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return MainViewControllerKt.createComposeViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // No updates needed
    }
}

enum Tab {
    case home
    case chat
    case notifications
}

