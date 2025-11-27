import SwiftUI
import Authenticator

struct ContentView: View {
    var body: some View {
        Authenticator { state in
            VStack {
                Button("Sign out") {
                    Task {
                        await state.signOut()
                    }
                }
            }
        }
    }
}

enum Tab {
    case home
    case chat
    case notifications
}

