import type { Schema } from '../../data/resource';
import { DynamoDBClient, QueryCommand, GetItemCommand } from '@aws-sdk/client-dynamodb';
import admin from 'firebase-admin';
import Ably from 'ably';

const ddbClient = new DynamoDBClient({});

// Initialize Ably for real-time messaging (only if API key is set)
let ablyClient: Ably.Rest | null = null;
if (process.env.ABLY_API_KEY && process.env.ABLY_API_KEY.length > 0) {
  try {
    ablyClient = new Ably.Rest({
      key: process.env.ABLY_API_KEY
    });
    console.log('Ably client initialized successfully');
  } catch (error) {
    console.error('Failed to initialize Ably:', error);
  }
}

// Initialize Firebase Admin SDK (once)
if (!admin.apps.length) {
  // Firebase credentials from environment variable
  // Format: {"type":"service_account","project_id":"...","private_key":"...","client_email":"..."}
  const serviceAccountJson = process.env.FIREBASE_SERVICE_ACCOUNT;

  if (!serviceAccountJson) {
    console.error('FIREBASE_SERVICE_ACCOUNT environment variable not set');
  } else {
    try {
      const serviceAccount = JSON.parse(serviceAccountJson);
      admin.initializeApp({
        credential: admin.credential.cert(serviceAccount)
      });
      console.log('Firebase Admin initialized successfully');
    } catch (error) {
      console.error('Failed to initialize Firebase Admin:', error);
    }
  }
}

export const handler: Schema["sendInviteNotification"]["functionHandler"] = async (event) => {
  const { recipientUserId, senderName, inviteId } = event.arguments;
  const senderId = event.identity?.sub || 'anonymous-sender';

  console.log(`Smart Invite: Checking presence for user ${recipientUserId}`);

  try {
    // 1. CHECK USER PRESENCE FIRST
    const presenceResult = await ddbClient.send(new GetItemCommand({
      TableName: 'UserPresence',
      Key: {
        'userId': { S: recipientUserId }
      }
    }));

    const isOnline = presenceResult.Item?.isOnline?.BOOL || false;
    const lastSeen = presenceResult.Item?.lastSeen?.S;

    console.log(`User ${recipientUserId} presence: ${isOnline ? 'ONLINE' : 'OFFLINE'}, lastSeen: ${lastSeen}`);

    // 2. IF USER IS ONLINE → SEND VIA ABLY (if configured)
    if (isOnline && ablyClient) {
      console.log(`User is ONLINE - Sending via Ably channel`);

      const channelName = `notifications:invites:${recipientUserId}`;
      const channel = ablyClient.channels.get(channelName);

      const message = {
        type: 'INVITE_RECEIVED',
        inviteId,
        senderId,
        senderName,
        timestamp: new Date().toISOString()
      };

      await channel.publish('invite', message);

      console.log(`✅ Successfully published to Ably channel: ${channelName}`);

      return {
        success: true,
        message: `Sent via Ably (user is online)`,
        deviceCount: 1 // Ably message
      };
    } else if (isOnline && !ablyClient) {
      console.log(`User is ONLINE but Ably not configured - Falling back to FCM`);
    }

    // 3. IF USER IS OFFLINE → SEND VIA FCM
    console.log(`User is OFFLINE - Sending via FCM push notification`);

    // Look up recipient's FCM tokens
    const result = await ddbClient.send(new QueryCommand({
      TableName: 'FcmToken',
      KeyConditionExpression: 'userId = :userId',
      ExpressionAttributeValues: {
        ':userId': { S: recipientUserId }
      }
    }));

    if (!result.Items || result.Items.length === 0) {
      console.log(`No FCM tokens found for user ${recipientUserId}`);
      return {
        success: false,
        message: 'User is offline and has no registered devices',
        deviceCount: 0
      };
    }

    // Extract tokens
    const tokens = result.Items.map(item => item.token.S).filter(Boolean);
    console.log(`Found ${tokens.length} FCM token(s) for user ${recipientUserId}`);

    // Send push notifications
    const notification = {
      title: 'New Connection Request',
      body: `${senderName} sent you a connection request`,
      data: {
        type: 'invite',
        inviteId: inviteId,
        senderId: senderId
      }
    };

    console.log('FCM Notification payload:', JSON.stringify(notification));

    let successCount = 0;
    let failureCount = 0;

    for (const token of tokens) {
      try {
        await sendFCMNotification(token as string, notification);
        successCount++;
        console.log(`Successfully sent FCM to device ${successCount}/${tokens.length}`);
      } catch (error) {
        failureCount++;
        console.error(`Failed to send FCM:`, error);
      }
    }

    console.log(`✅ Sent FCM notifications: ${successCount} succeeded, ${failureCount} failed`);

    return {
      success: successCount > 0,
      message: `Sent via FCM to ${successCount}/${tokens.length} device(s)`,
      deviceCount: successCount
    };

  } catch (error) {
    console.error('Error sending smart invite notification:', error);
    throw error;
  }
};

/**
 * Send FCM notification to a specific device token
 */
async function sendFCMNotification(
  token: string,
  notification: { title: string; body: string; data: Record<string, string> }
) {
  if (!admin.apps.length) {
    throw new Error('Firebase Admin not initialized');
  }

  const message = {
    token: token,
    notification: {
      title: notification.title,
      body: notification.body,
    },
    data: notification.data,
    android: {
      priority: 'high' as const,
      notification: {
        sound: 'default',
        channelId: 'default',
      }
    },
    apns: {
      headers: {
        'apns-priority': '10',
      },
      payload: {
        aps: {
          sound: 'default',
          badge: 1,
        },
      },
    },
  };

  try {
    const response = await admin.messaging().send(message);
    console.log('FCM message sent successfully:', response);
    return response;
  } catch (error: any) {
    console.error('Error sending FCM message:', error);

    // If token is invalid/expired, you might want to remove it from DynamoDB
    if (error.code === 'messaging/invalid-registration-token' ||
        error.code === 'messaging/registration-token-not-registered') {
      console.log(`Invalid token, should be removed: ${token}`);
    }

    throw error;
  }
}
