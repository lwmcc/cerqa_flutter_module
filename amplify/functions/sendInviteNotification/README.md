# Send Invite Notification Lambda Function

This Lambda function sends push notifications to users when they receive an invite.

## How It Works

1. **User A (sender)** sends an invite to **User B (recipient)**
2. Client calls `sendInviteNotification` mutation with:
   - `recipientUserId` - User B's ID (who should receive notification)
   - `senderName` - User A's name (for notification message)
   - `inviteId` - The invite ID (for deep linking)
3. Lambda:
   - Verifies sender is authenticated (gets senderId from auth context)
   - Queries DynamoDB for recipient's FCM tokens
   - Sends push notification to all recipient's devices

## Security

✅ **Sender is authenticated** - Backend gets senderId from `event.identity.sub`
✅ **Recipient userId is passed** - Client specifies who receives notification
✅ **Can't fake sender** - Sender identity comes from Cognito auth token

This is **correct and secure** because:
- User A is authenticated (verified by Cognito)
- User A explicitly says "send notification to User B"
- Backend trusts User A's authentication, sends to User B

## Next Steps: Actually Send FCM Notifications

The current implementation **queries tokens but doesn't send notifications yet**.

You need to choose one of these options:

### Option 1: Firebase Admin SDK (Recommended)

1. **Install dependency:**
```bash
cd amplify/functions/sendInviteNotification
npm install firebase-admin
```

2. **Update `package.json`:**
```json
{
  "dependencies": {
    "@aws-sdk/client-dynamodb": "^3.0.0",
    "firebase-admin": "^12.0.0"
  }
}
```

3. **Get Firebase service account key:**
   - Go to Firebase Console → Project Settings → Service Accounts
   - Click "Generate New Private Key"
   - Download the JSON file

4. **Store in AWS Secrets Manager:**
```bash
aws secretsmanager create-secret \
  --name firebase-admin-key \
  --secret-string file://path/to/serviceAccountKey.json \
  --region us-east-2
```

5. **Update handler.ts** to use Firebase Admin:

```typescript
import admin from 'firebase-admin';

// Initialize Firebase Admin (once)
if (!admin.apps.length) {
  const serviceAccount = JSON.parse(
    process.env.FIREBASE_SERVICE_ACCOUNT_KEY || '{}'
  );

  admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
  });
}

// Send FCM notification
async function sendFCMNotification(token: string, notification: any) {
  try {
    const message = {
      token: token,
      notification: {
        title: notification.title,
        body: notification.body,
      },
      data: notification.data,
      android: {
        priority: 'high' as const,
      },
      apns: {
        headers: {
          'apns-priority': '10',
        },
        payload: {
          aps: {
            sound: 'default',
          },
        },
      },
    };

    const response = await admin.messaging().send(message);
    console.log('Successfully sent message:', response);
    return response;
  } catch (error) {
    console.error('Error sending FCM message:', error);
    throw error;
  }
}

// Then in the main handler, replace the TODO with:
for (const token of tokens) {
  await sendFCMNotification(token, notification);
}
```

6. **Grant Lambda access to secrets:**

Update `amplify/backend.ts`:
```typescript
import { Secret } from 'aws-cdk-lib/aws-secretsmanager';

const firebaseSecret = Secret.fromSecretNameV2(
  backend.sendInviteNotification.resources.lambda,
  'firebase-admin-key',
  'firebase-admin-key'
);

backend.sendInviteNotification.resources.lambda.addEnvironment(
  'FIREBASE_SERVICE_ACCOUNT_KEY',
  firebaseSecret.secretValue.toString()
);
```

### Option 2: FCM HTTP v1 API (No additional dependencies)

Use `fetch` to call FCM API directly:

```typescript
async function sendFCMNotification(token: string, notification: any) {
  const fcmEndpoint = `https://fcm.googleapis.com/v1/projects/${PROJECT_ID}/messages:send`;

  const message = {
    message: {
      token: token,
      notification: {
        title: notification.title,
        body: notification.body,
      },
      data: notification.data,
    }
  };

  // Get OAuth token from service account
  const accessToken = await getAccessToken();

  const response = await fetch(fcmEndpoint, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${accessToken}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(message),
  });

  if (!response.ok) {
    throw new Error(`FCM request failed: ${response.statusText}`);
  }

  return response.json();
}
```

## Client Usage

From your app (Kotlin):

```kotlin
// When User A sends an invite to User B
suspend fun sendInvite(recipientUserId: String, inviteData: InviteInput) {
    // 1. Create the invite in the database
    val invite = createInvite(recipientUserId, inviteData)

    // 2. Send notification to recipient
    val result = apolloClient.mutation(
        SendInviteNotificationMutation(
            recipientUserId = recipientUserId,
            senderName = currentUser.name,
            inviteId = invite.id
        )
    ).execute()

    if (result.data?.sendInviteNotification?.success == true) {
        println("Notification sent to ${result.data.sendInviteNotification.deviceCount} devices")
    }
}
```

## Testing

1. **Deploy the Lambda:**
```bash
npx ampx sandbox --once
```

2. **Store some FCM tokens** (from your devices)

3. **Call the mutation:**
```graphql
mutation {
  sendInviteNotification(
    recipientUserId: "user-b-id"
    senderName: "Alice"
    inviteId: "invite-123"
  ) {
    success
    message
    deviceCount
  }
}
```

4. **Check CloudWatch Logs** to see:
   - How many tokens were found
   - Whether notifications were sent
   - Any errors

## Notes

- Current implementation logs the notification payload but doesn't send yet
- Choose Option 1 (Firebase Admin SDK) or Option 2 (HTTP API)
- Remember to handle token expiration/invalid tokens
- Consider batching notifications for multiple recipients
