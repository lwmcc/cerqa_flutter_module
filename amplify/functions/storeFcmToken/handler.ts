import type { Schema } from '../../data/resource';
import { DynamoDBClient, PutItemCommand } from '@aws-sdk/client-dynamodb';

const ddbClient = new DynamoDBClient({});

export const handler: Schema["storeFcmToken"]["functionHandler"] = async (event) => {
  const { userId, token, platform } = event.arguments;

  console.log(`Storing FCM token for user ${userId} on platform ${platform}`);
  console.log(`Token: ${token.substring(0, 20)}...`);

  // Use platform as deviceId since the table schema requires userId (HASH) and deviceId (RANGE)
  const deviceId = platform;
  const now = new Date().toISOString();

  try {
    await ddbClient.send(new PutItemCommand({
      TableName: 'FcmToken',
      Item: {
        userId: { S: userId },
        deviceId: { S: deviceId },
        token: { S: token },
        platform: { S: platform },
        createdAt: { S: now },
        updatedAt: { S: now },
      },
    }));

    console.log('FCM token stored successfully');
    return true;
  } catch (error) {
    console.error('Error storing FCM token:', error);
    throw error;
  }
};
