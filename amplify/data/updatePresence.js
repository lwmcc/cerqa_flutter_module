import { util } from '@aws-appsync/utils';

export function request(ctx) {
  const { userId, isOnline, platform } = ctx.arguments;

  return {
    operation: 'PutItem',
    key: util.dynamodb.toMapValues({ userId }),
    attributeValues: util.dynamodb.toMapValues({
      userId,
      isOnline,
      lastSeen: util.time.nowISO8601(),
      platform: platform || 'unknown',
      ttl: Math.floor(Date.now() / 1000) + (24 * 60 * 60) // 24 hours TTL
    }),
  };
}

export function response(ctx) {
  if (ctx.error) {
    util.error(ctx.error.message, ctx.error.type);
  }
  return true;
}
