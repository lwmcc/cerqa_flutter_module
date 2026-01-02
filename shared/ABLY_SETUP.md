# Ably Realtime Integration - Setup Guide

This guide explains how to use the Ably Realtime integration in your KMP app.

## Architecture

The Ably integration is built using Kotlin Multiplatform with the following components:

```
┌─────────────────────────────────────────────────────────────┐
│                      Shared (KMP)                            │
│                                                               │
│  ┌──────────────┐      ┌──────────────┐      ┌────────────┐ │
│  │ AblyService  │─────▶│AblyRepository│─────▶│Apollo Client│ │
│  └──────────────┘      └──────────────┘      └────────────┘ │
│         │                                                     │
│         ▼                                                     │
│  ┌──────────────┐                                            │
│  │ AblyClient   │ (expect/actual)                            │
│  └──────────────┘                                            │
└───────┬───────────────────────────┬─────────────────────────┘
        │                           │
┌───────▼──────────┐      ┌─────────▼───────────┐
│   Android        │      │       iOS           │
│                  │      │                     │
│ AndroidAblyClient│      │  IOSAblyClient      │
│ (Ably Android)   │      │  (Ably Cocoa)       │
└──────────────────┘      └─────────────────────┘
```

## Components

### 1. **AblyRepository** (shared/commonMain)
- Fetches Ably authentication tokens from AppSync via GraphQL
- Uses Apollo Client to execute the `FetchAblyJwt` query
- Returns token data wrapped in `Result<T>`

### 2. **AblyClient** (shared/commonMain - expect/actual)
- Platform-agnostic interface for Ably operations
- Android implementation uses `io.ably:ably-android`
- iOS implementation uses `Ably` CocoaPod
- Handles connection, channels, pub/sub

### 3. **AblyService** (shared/commonMain)
- High-level service combining repository + client
- Manages token fetching and client initialization
- Provides easy-to-use API for ViewModels

## Setup Instructions

### Prerequisites

1. **Lambda Function**: Your `fetchAblyRealtimeToken` Lambda must be deployed with:
   - Ably package bundled
   - Environment variables: `ABLY_KEY`, `ABLY_SECRET`

2. **AppSync**: Connected to Lambda via data source and resolver

3. **Apollo Client**: Configured with correct AppSync endpoint

### Android Setup

The Android Ably SDK is already included in `app/build.gradle.kts`:

```kotlin
implementation("io.ably:ably-android:1.2.20")
```

No additional setup needed!

### iOS Setup

Add the Ably CocoaPod to `shared/build.gradle.kts`:

```kotlin
cocoapods {
    // ... existing config

    pod("Ably") {
        version = "1.2.33"
    }
}
```

Then run:
```bash
cd iosApp
pod install
```

## Usage

### Step 1: Create Dependencies

In your dependency injection setup (Koin, Dagger, etc.):

```kotlin
// Shared module
single { AblyRepositoryImpl(get()) as AblyRepository }
single { AblyService(get()) }
```

### Step 2: Initialize in ViewModel

```kotlin
class MainViewModel(
    private val ablyService: AblyService,
    // ... other dependencies
) {
    fun initializeRealtime(userId: String) {
        viewModelScope.launch {
            ablyService.initialize(userId)
                .onSuccess {
                    println("Ably connected!")

                    // Subscribe to channels
                    subscribeToChannels()
                }
                .onFailure { error ->
                    println("Ably failed: ${error.message}")
                }
        }
    }

    private fun subscribeToChannels() {
        viewModelScope.launch {
            // Subscribe to user-specific channel
            ablyService.subscribeToChannel("user:$userId")
                .collect { message ->
                    // Handle incoming message
                    handleMessage(message)
                }
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            ablyService.publishMessage("user:$userId", message)
        }
    }
}
```

### Step 3: Use in UI (Android Compose)

```kotlin
@Composable
fun ChatScreen(viewModel: MainViewModel) {
    val userId = "user-123"

    LaunchedEffect(userId) {
        viewModel.initializeRealtime(userId)
    }

    // Your UI...
    Button(onClick = { viewModel.sendMessage("Hello!") }) {
        Text("Send Message")
    }
}
```

### Step 4: Use in iOS (SwiftUI)

```swift
struct ChatView: View {
    @StateObject var viewModel: MainViewModel
    let userId: String

    var body: some View {
        VStack {
            // Your UI...
            Button("Send Message") {
                viewModel.sendMessage(message: "Hello!")
            }
        }
        .onAppear {
            viewModel.initializeRealtime(userId: userId)
        }
    }
}
```

## API Reference

### AblyService

#### `suspend fun initialize(userId: String): Result<Unit>`
Fetches token and connects to Ably.

#### `fun subscribeToChannel(channelName: String): Flow<String>`
Subscribe to a channel and receive messages as a Flow.

#### `suspend fun publishMessage(channelName: String, message: String): Result<Unit>`
Publish a message to a channel.

#### `fun getConnectionState(): Flow<String>`
Monitor connection state: "connecting", "connected", "disconnected", etc.

#### `fun disconnect()`
Clean up and disconnect.

## Channel Naming Conventions

- User-specific: `user:{userId}`
- Chat rooms: `chat:{chatId}`
- Groups: `group:{groupId}`
- Notifications: `notifications:{userId}`

## Error Handling

All operations return `Result<T>`:

```kotlin
ablyService.initialize(userId)
    .onSuccess { /* connected */ }
    .onFailure { error ->
        when {
            error.message?.contains("token") == true -> {
                // Token error - retry auth
            }
            error.message?.contains("network") == true -> {
                // Network error - show offline UI
            }
            else -> {
                // Generic error
            }
        }
    }
```

## Testing

Mock the `AblyRepository` for unit tests:

```kotlin
class FakeAblyRepository : AblyRepository {
    override suspend fun fetchAblyToken(userId: String) =
        Result.success(
            FetchAblyJwtQuery.FetchAblyJwt(
                keyName = "test-key",
                clientId = userId,
                timestamp = System.currentTimeMillis().toDouble(),
                nonce = "test-nonce",
                mac = "test-mac"
            )
        )
}
```

## Troubleshooting

### "No token data returned"
- Check Lambda function logs
- Verify environment variables are set
- Test Lambda directly in AWS Console

### "Connection failed" / "Authentication failed"
- Verify AppSync data source has correct IAM role
- Check API key is valid
- Ensure token timestamp is current

### iOS compilation errors
- Run `pod install` in iosApp directory
- Clean build: `./gradlew clean`
- Verify Ably pod version matches

## Performance Tips

1. **Reuse AblyService instance** - Don't create multiple instances
2. **Unsubscribe when done** - Cancel Flow collection when leaving screen
3. **Batch messages** - Don't publish every keystroke
4. **Monitor connection state** - Pause publishing when disconnected

## Next Steps

- Implement presence (see who's online)
- Add message history
- Implement typing indicators
- Add push notifications for offline users
