import type { Schema } from '../../data/resource';
import { DynamoDBClient, GetItemCommand } from '@aws-sdk/client-dynamodb';
import admin from 'firebase-admin';
import Ably from 'ably';

const ddbClient = new DynamoDBClient({});

// Initialize Firebase Admin SDK (once)
if (!admin.apps.length) {
  const serviceAccountJson = process.env.FIREBASE_SERVICE_ACCOUNT;
  if (serviceAccountJson) {
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

// Initialize Ably
const ablyClient = new Ably.Rest({
  key: process.env.ABLY_API_KEY || ''
});

export const handler: Schema["sendSmartInviteNotification"]["functionHandler"] = async (event) => {
  const { recipientUserId, senderName, inviteId } = event.arguments;
  const senderId = event.identity?.sub || 'anonymous-sender';

  console.log(`Smart Invite: Checking presence for user ${recipientUserId}`);

  try {
    // 1. Check user presence in DynamoDB
    const presenceResult = await ddbClient.send(new GetItemCommand({
      TableName: 'UserPresence',
      Key: {
        'userId': { S: recipientUserId }
      }
    }));

    const isOnline = presenceResult.Item?.isOnline?.BOOL || false;
    const lastSeen = presenceResult.Item?.lastSeen?.S;

    console.log(`User ${recipientUserId} presence: ${isOnline ? 'ONLINE' : 'OFFLINE'}, lastSeen: ${lastSeen}`);

    // 2. Determine delivery method based on presence
    if (isOnline) {
      // User is online - Send via Ably
      console.log(`User is ONLINE - Sending via Ably`);

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

      console.log(`Successfully published to Ably channel: ${channelName}`);

      return {
        success: true,
        message: 'Invite sent via Ably (user is online)',
        deliveryMethod: 'ABLY',
        channelName
      };

    } else {
      // User is offline - Send via FCM
      console.log(`User is OFFLINE - Sending via FCM`);

      // Get FCM tokens
      const tokensResult = await ddbClient.send(new GetItemCommand({
        TableName: 'FcmToken',
        Key: {
          'userId': { S: recipientUserId }
        }
      }));

      const tokens = tokensResult.Item?.tokens?.L?.map(t => t.S).filter(Boolean) || [];

      if (tokens.length === 0) {
        console.log(`No FCM tokens found for user ${recipientUserId}`);
        return {
          success: false,
          message: 'User is offline and has no registered devices',
          deliveryMethod: 'NONE',
          channelName: null
        };
      }

      // Send FCM notification
      const notification = {
        title: 'New Connection Request',
        body: `${senderName} sent you a connection request`,
        data: {
          type: 'invite',
          inviteId,
          senderId
        }
      };

      let successCount = 0;
      for (const token of tokens) {
        try {
          await admin.messaging().send({
            token: token as string,
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
              headers: { 'apns-priority': '10' },
              payload: {
                aps: { sound: 'default', badge: 1 }
              }
            }
          });
          successCount++;
        } catch (error) {
          console.error(`Failed to send FCM to token:`, error);
        }
      }

      console.log(`Sent FCM notifications to ${successCount}/${tokens.length} devices`);

      return {
        success: successCount > 0,
        message: `Sent FCM to ${successCount}/${tokens.length} device(s)`,
        deliveryMethod: 'FCM',
        channelName: null
      };
    }

  } catch (error) {
    console.error('Error sending smart invite notification:', error);
    throw error;
  }
};
