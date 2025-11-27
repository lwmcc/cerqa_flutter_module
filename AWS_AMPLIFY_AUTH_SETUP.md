# AWS Amplify Authentication Setup Guide

## Overview

This guide explains how to complete the setup of AWS Amplify authentication for iOS using Kotlin Multiplatform (KMP) with the expect/actual pattern. The authentication works using Compose Multiplatform UI that's shared between iOS and Android.

## What Was Created

### 1. Common Code (Kotlin Multiplatform - shared module)

- **`shared/src/commonMain/kotlin/com/cerqa/auth/`**
  - `AuthModels.kt` - Data models for authentication (AuthUser, AuthResult, AuthState, etc.)
  - `AuthService.kt` - `expect` interface for authentication service
  - `AuthViewModel.kt` - ViewModel for managing authentication UI state
  - `ui/AuthScreen.kt` - Compose UI screens for Sign In, Sign Up, and Confirmation

### 2. iOS Implementation (Kotlin/Native)

- **`shared/src/iosMain/kotlin/com/cerqa/auth/`**
  - `AuthService.ios.kt` - `actual` implementation for iOS
  - `AmplifyAuthBridge.kt` - Bridge between Kotlin and Swift Amplify SDK

### 3. Swift Bridge Code

- **`cerqaiOS/AmplifyHelper.swift`** - Swift class that calls the Amplify Swift SDK

### 4. Configuration Files

- **`cerqaiOS/cerqaiOS/Resources/amplify_outputs.json`** - Amplify configuration (copied from Android)

## Setup Steps for iOS

### Step 1: Add Amplify Swift SDK to Xcode Project

1. Open the Xcode project:
   ```bash
   open cerqaiOS/cerqaiOS.xcodeproj
   ```

2. In Xcode, select the project in the navigator (cerqaiOS)

3. Select the **cerqaiOS** target

4. Go to the **Package Dependencies** tab

5. Click the **+** button to add a package

6. Enter the Amplify Swift SDK URL:
   ```
   https://github.com/aws-amplify/amplify-swift
   ```

7. Select version **2.0.0** or later

8. Add the following packages:
   - **Amplify**
   - **AWSCognitoAuthPlugin**

9. Click **Add Package**

### Step 2: Add Files to Xcode Project

1. In Xcode, right-click on the **cerqaiOS** folder in the project navigator

2. Select **Add Files to "cerqaiOS"**

3. Navigate to and add:
   - `cerqaiOS/AmplifyHelper.swift`
   - `cerqaiOS/cerqaiOS/Resources/amplify_outputs.json`

4. Make sure to:
   - ✅ Copy items if needed
   - ✅ Create groups
   - ✅ Add to target: cerqaiOS

### Step 3: Link the Shared KMP Framework

1. In Xcode, select the cerqaiOS target

2. Go to **General** tab

3. Under **Frameworks, Libraries, and Embedded Content**, click **+**

4. Click **Add Other** → **Add Files**

5. Navigate to:
   ```
   shared/build/bin/iosSimulatorArm64/debugFramework/Shared.framework
   ```

6. Add it and set to **Embed & Sign**

### Step 4: Build the Shared KMP Module

Before building the iOS app, build the shared module:

```bash
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
```

### Step 5: Initialize Amplify in Your iOS App

Update `cerqaiOSApp.swift` to initialize Amplify:

```swift
import SwiftUI
import Shared

@main
struct cerqaiOSApp: App {
    init() {
        // Initialize Amplify
        AmplifyHelper.configure()

        // Initialize KMP DI (if using Koin)
        KoinHelperKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```

### Step 6: Use Authentication in Your UI

You can now use the authentication in your Compose UI. Here's an example:

```kotlin
// In your main Compose screen
@Composable
fun App() {
    val authViewModel: AuthViewModel = koinInject()
    val authState by authViewModel.authState.collectAsState()

    when (authState) {
        is AuthState.Loading -> {
            // Show loading
            CircularProgressIndicator()
        }
        is AuthState.Unauthenticated -> {
            // Show auth screen
            AuthScreen(
                viewModel = authViewModel,
                onAuthSuccess = {
                    // Navigate to main app
                }
            )
        }
        is AuthState.Authenticated -> {
            // Show main app
            MainAppContent()
        }
        is AuthState.Error -> {
            // Show error
        }
    }
}
```

## How It Works

### Architecture Overview

```
┌─────────────────────────────────────────┐
│         Compose UI (Common)             │
│    AuthScreen.kt + AuthViewModel.kt     │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│    AuthService (expect/actual)          │
│                                          │
│  ┌────────────┐      ┌───────────────┐ │
│  │  Android   │      │     iOS       │ │
│  │  (actual)  │      │   (actual)    │ │
│  └─────┬──────┘      └───────┬───────┘ │
└────────┼─────────────────────┼─────────┘
         │                     │
         ▼                     ▼
┌────────────────┐    ┌──────────────────┐
│ Amplify        │    │ Swift Bridge     │
│ Android SDK    │    │ AmplifyHelper.kt │
└────────────────┘    └────────┬─────────┘
                               │
                               ▼
                      ┌──────────────────┐
                      │ Amplify Swift SDK│
                      │ (Native)         │
                      └──────────────────┘
```

### Data Flow

1. **User interacts** with Compose UI (`AuthScreen.kt`)
2. **UI calls** `AuthViewModel` methods
3. **ViewModel calls** `AuthService` (expect interface)
4. **iOS actual** implementation (`AuthService.ios.kt`) is invoked
5. **AmplifyAuthBridge** converts Kotlin calls to Swift-compatible calls
6. **AmplifyHelper.swift** calls the native Amplify Swift SDK
7. **Results flow back** through the chain to update UI

## Available Auth Methods

The `AuthService` provides:

- `signUp(data: SignUpData)` - Create new user account
- `confirmSignUp(data: ConfirmationData)` - Verify email with code
- `signIn(data: SignInData)` - Sign in existing user
- `signOut()` - Sign out current user
- `getCurrentUser()` - Get currently authenticated user
- `resendConfirmationCode(email: String)` - Resend verification code
- `resetPassword(email: String)` - Initiate password reset
- `confirmResetPassword(...)` - Complete password reset

## Testing

1. Build the iOS app in Xcode
2. Run on the iOS Simulator
3. The auth screens should appear
4. Try creating an account:
   - Enter email, password, first/last name
   - You'll receive a verification code via email
   - Enter the code to confirm
   - Sign in with your credentials

## Troubleshooting

### Build Errors

**"No such module 'Shared'"**
- Make sure you've built the shared framework: `./gradlew :shared:linkDebugFrameworkIosSimulatorArm64`
- Check that Shared.framework is properly linked in Xcode

**"No such module 'Amplify'"**
- Verify Amplify packages were added via Swift Package Manager
- Clean build folder: Product → Clean Build Folder
- Restart Xcode

**"amplify_outputs.json not found"**
- Make sure the file is added to the Xcode project
- Check it's in the target membership for cerqaiOS

### Runtime Errors

**"Failed to configure Amplify"**
- Check that `AmplifyHelper.configure()` is called in `init()`
- Verify amplify_outputs.json is valid JSON
- Check Xcode console for detailed error messages

**Authentication fails**
- Verify your AWS Cognito User Pool is configured correctly
- Check that the user pool allows email sign-up
- Ensure password meets requirements (8+ chars, uppercase, lowercase, numbers, symbols)

## Next Steps

1. **Add more auth features:**
   - Social sign-in (Google, Facebook, etc.)
   - Multi-factor authentication
   - Biometric authentication

2. **Integrate with your app:**
   - Use `authState` to control navigation
   - Store auth tokens for API calls
   - Sync user data with backend

3. **Add user profile management:**
   - Update user attributes
   - Change password
   - Delete account

## Resources

- [AWS Amplify Swift SDK Documentation](https://docs.amplify.aws/swift/)
- [Kotlin Multiplatform Documentation](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
