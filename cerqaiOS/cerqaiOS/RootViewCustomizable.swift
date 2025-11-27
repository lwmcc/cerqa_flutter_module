//
// RootViewCustomizable.swift
// cerqaiOS
//
// Example of customizing Amplify Authenticator UI
//

import SwiftUI
// TODO: Add Authenticator via SPM
// import Authenticator

struct RootViewCustomizable: View {
    @StateObject private var mainViewModel = MainViewModelWrapper()

    var body: some View {
        Authenticator(
            // Custom header content (optional)
            headerContent: {
                VStack(spacing: 16) {
                    Image(systemName: "car.fill")
                        .font(.system(size: 60))
                        .foregroundColor(.blue)

                    Text("Car Club")
                        .font(.largeTitle)
                        .fontWeight(.bold)
                }
                .padding(.top, 40)
            },

            // Custom footer content (optional)
            footerContent: {
                Text("By signing in, you agree to our Terms & Privacy Policy")
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)
                    .padding()
            }
        ) { state in
            // Signed-in content
            ContentView()
                .environmentObject(mainViewModel)
                .onAppear {
                    if let userId = state.user.userId,
                       let username = state.user.username {
                        mainViewModel.setUserData(
                            userId: userId,
                            userName: username
                        )
                    }
                }
        }
    }
}

