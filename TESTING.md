# Testing the Shared Auth & Contacts Implementation

This document explains how to test the newly implemented shared authentication and contacts functionality on both iOS and Android.

## Architecture Overview

### What Was Built

1. **Shared Kotlin Code** (`shared/` module):
   - `AuthTokenProvider` interface with platform-specific implementations
   - `ContactsRepository` - GraphQL API calls to AWS AppSync
   - `ContactsViewModel` - Manages contacts state with Kotlin Flows
   - `HttpClientFactory` - Ktor client with JWT authentication
   - **Models**: `User`, `Contact`, `UserContact`

2. **iOS Integration**:
   - `IOSAuthTokenProvider` - Bridges Kotlin to Swift Amplify SDK
   - `IOSAuthCallbackImpl` - Swift implementation using Amplify
   - `ContactsTestView` - SwiftUI test screen
   - Proper Koin DI wiring in `cerqaiOSApp.swift`

3. **Android Integration**:
   - `AndroidAuthTokenProvider` - Uses Amplify Android SDK directly
   - Koin DI setup in `androidModule`

## Testing on iOS

### Prerequisites
1. Make sure you're authenticated with Amplify (the app uses `Authenticator` wrapper)
2. Build completed successfully
3. Xcode project is synced

### Steps to Test

1. **Open the iOS project**:
   ```bash
   open cerqaiOS/cerqaiOS.xcodeproj
   ```

2. **Build the Shared framework**:
   - In Xcode, select the `Shared` scheme
   - Build (Cmd+B)
   - OR run: `./gradlew :shared:linkDebugFrameworkIosSimulatorArm64`

3. **Run the iOS app**:
   - Select a simulator or your device
   - Run the app (Cmd+R)

4. **Access the test screen**:
   - After authenticating with Amplify
   - You'll see a blue button "Test Shared Contacts" at the bottom
   - Tap it to open `ContactsTestView`

5. **What to verify**:
   - ✅ The view should fetch contacts from your AppSync backend
   - ✅ Loading indicator should appear while fetching
   - ✅ Contacts list should display with proper data
   - ✅ Search functionality should work
   - ✅ Error handling should show alerts if something fails
   - ✅ Pull to refresh should work (use the refresh button in toolbar)

### Expected Behavior

**On Success**:
- Contacts load from your AWS AppSync backend
- Each contact shows: name, phone/email, username
- Search filters contacts in real-time

**On First Load (No Contacts)**:
- Shows "No Contacts" empty state
- Retry button available

**On Error**:
- Alert dialog with error message
- Usually indicates:
  - Not authenticated
  - Network issues
  - AppSync endpoint misconfiguration
  - GraphQL query errors

### Debugging iOS

Check Xcode console for:
```
// Auth callback setup
IOSAuthTokenProviderWrapper initialized

// Koin initialization
Koin started

// API calls
HTTP Client configured for: https://wsfjluq6hnaszkwcciqxao7i6m.appsync-api.us-east-2.amazonaws.com/graphql

// Errors
AuthenticationException: <error details>
GraphQL errors: <error details>
```

## Testing on Android

### Prerequisites
1. Amplify Android SDK configured
2. User authenticated
3. Koin initialized with `androidModule`

### Steps to Test

Since this project uses Compose Multiplatform, you can create a similar test composable:

1. **Create a test composable** in your Android/Compose code:
   ```kotlin
   @Composable
   fun ContactsTestScreen() {
       val viewModel: ContactsViewModel = koinInject()
       val contacts by viewModel.contacts.collectAsState()
       val isLoading by viewModel.isLoading.collectAsState()
       val error by viewModel.error.collectAsState()

       LaunchedEffect(Unit) {
           viewModel.fetchContacts()
       }

       // UI implementation similar to iOS
   }
   ```

2. **Add to navigation**:
   - Add a test button or navigation item
   - Navigate to `ContactsTestScreen`

3. **Verify**:
   - Same checks as iOS above
   - Android-specific: Check Logcat for debug output

## Common Issues & Solutions

### Issue: "IOSAuthCallback not configured"
**Solution**: The auth callback needs to be set during app initialization. Check `cerqaiOSApp.swift` line 22-25.

### Issue: "GraphQL errors: Unauthorized"
**Solution**:
- Verify user is authenticated with Amplify
- Check JWT token is being passed correctly
- Verify AppSync API allows the operation

### Issue: "Failed to fetch contacts"
**Solutions**:
- Check network connectivity
- Verify AppSync endpoint URL in `HttpClientFactory.kt:59`
- Ensure GraphQL schema matches (UserContact, User models)
- Check Amplify configuration files

### Issue: No contacts returned (empty list)
**Expected**: If this is your first time running, you won't have contacts yet.
**Next step**: Implement `addContact()` functionality to test adding contacts.

## API Endpoints Being Called

The `ContactsRepository` makes these GraphQL calls to AppSync:

1. **Fetch Contacts**:
   ```graphql
   query ListUserContacts($userId: ID!) {
     listUserContacts(filter: { userId: { eq: $userId } }) {
       items {
         contact { id, firstName, lastName, phone, email, ... }
       }
     }
   }
   ```

2. **Find User by Phone**:
   ```graphql
   query ListByPhone($phone: String!) {
     listByPhone(phone: $phone) {
       items { id, firstName, lastName, phone, ... }
     }
   }
   ```

3. **Add Contact**:
   ```graphql
   mutation CreateUserContact($userId: ID!, $contactId: ID!) {
     createUserContact(input: { userId: $userId, contactId: $contactId }) {
       id, userId, contactId, contact { ... }
     }
   }
   ```

4. **Delete Contact**:
   ```graphql
   mutation DeleteUserContact($id: ID!) {
     deleteUserContact(input: { id: $id }) { id }
   }
   ```

## Next Steps

After verifying the test screens work:

1. **Integrate into production UI**:
   - Replace test views with production screens
   - Add proper error handling and retry logic
   - Implement add/delete contact flows

2. **Add more features**:
   - Contact search by phone
   - Add contact from phone book
   - Contact details view
   - Delete/block contacts

3. **Testing**:
   - Write unit tests for `ContactsRepository`
   - Test error scenarios (network errors, auth failures)
   - Test with real backend data

## Files Modified

### Shared Module
- `shared/src/commonMain/kotlin/com/cerqa/auth/AuthTokenProvider.kt` (new)
- `shared/src/commonMain/kotlin/com/cerqa/network/*.kt` (new)
- `shared/src/commonMain/kotlin/com/cerqa/repository/ContactsRepository.kt` (new)
- `shared/src/commonMain/kotlin/com/cerqa/viewmodels/ContactsViewModel.kt` (modified)
- `shared/src/commonMain/kotlin/com/cerqa/models/Models.kt` (modified)
- `shared/src/androidMain/kotlin/com/cerqa/auth/AndroidAuthTokenProvider.kt` (new)
- `shared/src/iosMain/kotlin/com/cerqa/auth/IOSAuthTokenProvider.kt` (new)
- `shared/src/iosMain/kotlin/com/cerqa/di/IOSModule.kt` (modified)
- `shared/build.gradle.kts` (modified - added Ktor dependencies)

### iOS App
- `cerqaiOS/cerqaiOS/Auth/IOSAuthTokenProviderImpl.swift` (modified)
- `cerqaiOS/cerqaiOS/Views/ContactsTestView.swift` (new)
- `cerqaiOS/cerqaiOS/ContentView.swift` (modified - added test button)
- `cerqaiOS/cerqaiOS/cerqaiOSApp.swift` (modified - wired auth callback)

## Support

If you encounter issues:
1. Check the console/Logcat for detailed error messages
2. Verify Amplify configuration
3. Ensure AppSync schema matches the models
4. Check network/GraphQL permissions in AWS

The architecture is now ready for both platforms to share authentication and make authenticated API calls to your Amplify backend!
