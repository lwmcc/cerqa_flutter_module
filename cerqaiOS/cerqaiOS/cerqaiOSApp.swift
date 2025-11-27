//
// cerqaiOSApp.swift
// cerqaiOS
//
// SwiftUI App entry point - matches Android MainActivity
//

import Amplify
import Authenticator
import AWSAPIPlugin
import AWSCognitoAuthPlugin
import SwiftUI
import Shared

@main
struct cerqaiOSApp: App {
    init() {
        KoinHelperKt.doInitKoin()
        do {
            try Amplify.add(plugin: AWSCognitoAuthPlugin())
            // try Amplify.add(plugin: AWSAPIPlugin(modelRegistration: AmplifyModels()))
            try Amplify.configure(with: .amplifyOutputs)
        } catch {
            print("Unable to configure Amplify \(error)")
        }
    }


    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
