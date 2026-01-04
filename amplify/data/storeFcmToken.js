import { util } from '@aws-appsync/utils';

/**
 * Stores FCM token for the authenticated user in the FcmToken DynamoDB table
 * @param {Object} ctx - The context object
 * @param {Object} ctx.args - The arguments (token, platform)
 * @param {Object} ctx.identity - The identity of the authenticated user
 * @returns {Object} DynamoDB request to store FCM token
 */
export function request(ctx) {
  const { token, platform } = ctx.args;
  const userId = ctx.identity.sub; // Get authenticated user's ID

  // Create composite key: userId#platform to support multiple devices per user
  const id = `${userId}#${platform}`;
  const now = util.time.nowISO8601();

  console.log(`Storing FCM token for user ${userId} on platform ${platform}`);

  return {
    operation: 'PutItem',
    key: util.dynamodb.toMapValues({ id }),
    attributeValues: util.dynamodb.toMapValues({
      id,
      userId,
      token,
      platform,
      createdAt: now,
      updatedAt: now,
    }),
  };
}

/**
 * Process the response from DynamoDB
 * @param {Object} ctx - The context object
 * @returns {boolean} True if successful
 */
export function response(ctx) {
  if (ctx.error) {
    console.error('Error storing FCM token:', ctx.error);
    util.error(ctx.error.message, ctx.error.type);
  }

  console.log('FCM token stored successfully');
  return true;
}
