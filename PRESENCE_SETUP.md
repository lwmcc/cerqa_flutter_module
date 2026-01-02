# Presence-Based Smart Notifications Setup

This system automatically chooses between FCM (push notifications) and Ably (real-time channel) based on user presence.

## Architecture

```
User A sends invite
        ↓
  sendSmartInviteNotification (Lambda)
        ↓
  Check User B presence in DynamoDB
        ↓
    ┌─────────────┬─────────────┐
    ↓             ↓             ↓
User ONLINE   User OFFLINE   No tokens
    ↓             ↓             ↓
Send to Ably  Send FCM      Return error
```

## Setup Steps

### 1. Create DynamoDB Table for Presence

```bash
aws dynamodb create-table \
    --table-name UserPresence \
    --attribute-definitions \
        AttributeName=userId,AttributeType=S \
    --key-schema \
        AttributeName=userId,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST \
    --time-to-live-specification \
        Enabled=true,AttributeName=ttl \
    --region us-east-2
```

### 2. Add Ably API Key Secret

```bash
# Get your Ably API key from https://ably.com/dashboard
npx ampx sandbox secret set ABLY_API_KEY
# Enter your Ably API key when prompted
```

### 3. Deploy the Backend

```bash
npx ampx sandbox
```

### 4. Update Client Code

#### In `SearchViewModel.kt` - Change from old to new mutation:

**OLD (Client-side logic):**
```kotlin
// Don't use this anymore
notifications.sendConnectionInviteNotification(...)
```

**NEW (Server-side logic):**
```kotlin
apolloClient.mutation(
    SendSmartInviteNotificationMutation(
        recipientUserId = receiverUserId,
        senderName = senderName,
        inviteId = inviteId
    )
).execute()
```

#### Add Presence Tracking to `MainViewModel.kt`:

```kotlin
// Call when app comes to foreground
fun updatePresenceOnline() {
    viewModelScope.launch {
        val userId = preferences.getUserData().userId ?: return@launch
        apolloClient.mutation(
            UpdatePresenceMutation(
                userId = userId,
                isOnline = true,
                platform = "android" // or "ios"
            )
        ).execute()
    }
}

// Call when app goes to background or user logs out
fun updatePresenceOffline() {
    viewModelScope.launch {
        val userId = preferences.getUserData().userId ?: return@launch
        apolloClient.mutation(
            UpdatePresenceMutation(
                userId = userId,
                isOnline = false,
                platform = "android"
            )
        ).execute()
    }
}
```

#### Add Lifecycle Hooks in `MainActivity.kt`:

```kotlin
override fun onResume() {
    super.onResume()
    mainViewModel.updatePresenceOnline()
}

override fun onPause() {
    super.onPause()
    mainViewModel.updatePresenceOffline()
}
```

### 5. Update GraphQL Schema (Generate Code)

```bash
cd shared
./gradlew :shared:build
```

This will generate:
- `SendSmartInviteNotificationMutation`
- `UpdatePresenceMutation`
- `GetPresenceQuery`

## Usage

### Sending an Invite (Server decides FCM vs Ably)

```kotlin
apolloClient.mutation(
    SendSmartInviteNotificationMutation(
        recipientUserId = "user-id",
        senderName = "John Doe",
        inviteId = "invite-123"
    )
).execute().also { response ->
    val result = response.data?.sendSmartInviteNotification
    println("Delivery method: ${result?.deliveryMethod}") // "FCM" or "ABLY"
    println("Message: ${result?.message}")
}
```

### Checking User Presence

```kotlin
apolloClient.query(
    GetPresenceQuery(userId = "user-id")
).execute().also { response ->
    val presence = response.data?.getPresence
    println("Is online: ${presence?.isOnline}")
    println("Last seen: ${presence?.lastSeen}")
}
```

## How It Works

1. **User A sends invite** → Calls `sendSmartInviteNotification` mutation
2. **Lambda checks DynamoDB** → Looks up User B's presence status
3. **Decision based on presence:**
   - **User B is ONLINE** (in app) → Publish to Ably channel `notifications:invites:userId`
   - **User B is OFFLINE** → Send FCM push notification to all their devices
4. **Client receives notification:**
   - **Via Ably** → Real-time message appears instantly in app
   - **Via FCM** → Push notification shows in system tray

## Benefits

✅ **Reduced FCM costs** - Only send push notifications when needed
✅ **Better UX** - Instant in-app notifications for active users
✅ **Battery efficient** - No unnecessary push notifications
✅ **Single API** - All logic handled server-side
✅ **Automatic** - No client-side decision logic needed

## Monitoring

View logs:
```bash
# Monitor Lambda execution
aws logs tail /aws/lambda/sendSmartInviteNotification --follow

# Check presence updates
aws dynamodb scan --table-name UserPresence --region us-east-2
```

## Troubleshooting

**Issue: "User is offline and has no registered devices"**
- User hasn't registered FCM token
- Solution: Ensure `storeFcmToken` is called on app startup

**Issue: Ably publish fails**
- Check ABLY_API_KEY secret is set correctly
- Verify Ably account has sufficient quota

**Issue: Presence not updating**
- Ensure `updatePresence` mutations are being called
- Check app lifecycle hooks are implemented
