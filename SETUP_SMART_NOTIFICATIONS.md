# Smart Notification Setup Guide
## Using Your Existing AppSync API with Presence-Based Delivery

✅ **Your existing API**: `https://qtjqzunv4rgbtkphmdz2f2ybrq.appsync-api.us-east-2.amazonaws.com/graphql`

---

## What Was Done

### ✅ 1. DynamoDB Table Created
- **Table**: `UserPresence`
- **Purpose**: Track which users are online/offline
- **Status**: Already created in `us-east-2`

### ✅ 2. Modified `sendInviteNotification` Lambda
**Location**: `amplify/functions/sendInviteNotification/handler.ts`

**New Logic**:
1. ✅ Check user presence in DynamoDB
2. ✅ If ONLINE → Publish to Ably channel `notifications:invites:{userId}`
3. ✅ If OFFLINE → Send FCM push notification

### ✅ 3. Created `updatePresence` Lambda
**Location**: `amplify/functions/updatePresence/`

**Purpose**: Update user online/offline status

---

## Setup Steps

### Step 1: Set Ably API Key Secret

```bash
cd /Users/larrymccarty/AndroidStudioProjects/carclub
npx ampx sandbox secret set ABLY_API_KEY
```

When prompted, enter your Ably API key from: https://ably.com/dashboard

### Step 2: Install Dependencies

```bash
cd amplify/functions/sendInviteNotification
npm install

cd ../updatePresence
npm install
```

### Step 3: Deploy to Sandbox

```bash
cd /Users/larrymccarty/AndroidStudioProjects/carclub
npx ampx sandbox
```

This will:
- Deploy the updated `sendInviteNotification` Lambda
- Deploy the new `updatePresence` Lambda
- Update your AppSync API schema
- Keep using your existing endpoint

### Step 4: Update Client Code

#### A. Track Presence in `MainViewModel.kt`

Add presence tracking when user status changes:

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
        ).execute().also { response ->
            println("Presence updated: ${response.data?.updatePresence}")
        }
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

#### B. Add Lifecycle Hooks in `MainActivity.kt`

```kotlin
override fun onResume() {
    super.onResume()
    mainViewModel.updatePresenceOnline()
}

override fun onPause() {
    super.onPause()
    mainViewModel.updatePresenceOffline()
}

override fun onDestroy() {
    super.onDestroy()
    mainViewModel.updatePresenceOffline()
}
```

#### C. No Changes Needed for Sending Invites!

Your existing code already uses `sendInviteNotification` mutation - it will now automatically choose FCM or Ably based on presence!

```kotlin
// This code stays the same - magic happens server-side!
apolloClient.mutation(
    SendInviteNotificationMutation(
        recipientUserId = receiverUserId,
        senderName = senderName,
        inviteId = inviteId
    )
).execute()
```

### Step 5: Rebuild App to Get New GraphQL Types

```bash
cd /Users/larrymccarty/AndroidStudioProjects/carclub
./gradlew :shared:build
```

This generates the `UpdatePresenceMutation` type.

---

## How It Works

### Flow Diagram

```
User A sends invite
       ↓
sendInviteNotification Lambda
       ↓
Check User B presence in DynamoDB
       ↓
   ┌───────────┬───────────┐
   ↓           ↓           ↓
ONLINE      OFFLINE    No devices
   ↓           ↓           ↓
Publish    Send FCM    Return error
to Ably    Push
channel    Notification
```

### Example Log Output

**User ONLINE:**
```
Smart Invite: Checking presence for user abc123
User abc123 presence: ONLINE, lastSeen: 2025-12-28T17:30:00Z
User is ONLINE - Sending via Ably channel
✅ Successfully published to Ably channel: notifications:invites:abc123
```

**User OFFLINE:**
```
Smart Invite: Checking presence for user abc123
User abc123 presence: OFFLINE, lastSeen: 2025-12-28T15:00:00Z
User is OFFLINE - Sending via FCM push notification
Found 2 FCM token(s) for user abc123
✅ Sent FCM notifications: 2 succeeded, 0 failed
```

---

## API Reference

### Mutations

**updatePresence** - Track user online/offline status
```graphql
mutation UpdatePresence {
  updatePresence(
    userId: "user-id"
    isOnline: true
    platform: "android"
  )
}
```

**sendInviteNotification** - Send invite (auto-chooses FCM or Ably)
```graphql
mutation SendInviteNotification {
  sendInviteNotification(
    recipientUserId: "recipient-id"
    senderName: "John Doe"
    inviteId: "invite-123"
  ) {
    success
    message
    deviceCount
  }
}
```

---

## Benefits

✅ **Uses your existing AppSync API** - No new API created
✅ **Server-side logic** - Client just sends invites, server decides delivery
✅ **Reduced FCM costs** - Only send push when user offline
✅ **Better UX** - Instant in-app notifications via Ably
✅ **API Key auth** - Works with your existing authentication
✅ **Automatic** - No client changes to sending invites

---

## Testing

### 1. Test Presence Tracking

```bash
# Check if presence is being updated
aws dynamodb scan --table-name UserPresence --region us-east-2
```

### 2. Test Ably Subscription

Your existing subscription code works:
```kotlin
// Already subscribed in MainViewModel
subscribeToDmChannel() // Listens on notifications:invites:{userId}
```

### 3. Monitor Logs

```bash
# Watch Lambda logs
aws logs tail /aws/lambda/sendInviteNotification-<env> --follow --region us-east-2
```

---

## Troubleshooting

**"Ably not publishing"**
- Check ABLY_API_KEY is set: `npx ampx sandbox secret list`
- Verify Ably account quota

**"User always shows offline"**
- Ensure `updatePresence` is being called on app resume/pause
- Check DynamoDB table exists
- Verify Lambda has DynamoDB permissions

**"FCM still sending when user online"**
- Check presence table has correct userId
- Verify `isOnline` is set to `true`
- Check Lambda logs for presence check results

---

## Next Steps

After deployment works:

1. Test with two devices
2. Monitor CloudWatch logs
3. Check Ably dashboard for published messages
4. Verify FCM only sent when users offline
