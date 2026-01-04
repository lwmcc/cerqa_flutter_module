import type { Schema } from '../../data/resource';
import { DynamoDBClient, PutItemCommand } from '@aws-sdk/client-dynamodb';

const ddbClient = new DynamoDBClient({});

export const handler: Schema["updatePresence"]["functionHandler"] = async (event) => {
  const { userId, isOnline, platform } = event.arguments;

  console.log(`Updating presence for user ${userId}: ${isOnline ? 'ONLINE' : 'OFFLINE'}`);

  try {
    await ddbClient.send(new PutItemCommand({
      TableName: 'UserPresence',
      Item: {
        'userId': { S: userId },
        'isOnline': { BOOL: isOnline },
        'lastSeen': { S: new Date().toISOString() },
        'platform': { S: platform || 'unknown' },
        'ttl': { N: String(Math.floor(Date.now() / 1000) + (24 * 60 * 60)) } // 24hr TTL
      }
    }));

    console.log(`✅ Presence updated successfully`);
    return true;

  } catch (error) {
    console.error('Error updating presence:', error);
    throw error;
  }
};
