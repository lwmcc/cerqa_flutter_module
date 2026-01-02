import { util } from '@aws-appsync/utils';

export function request(ctx) {
  const { userId } = ctx.arguments;

  return {
    operation: 'GetItem',
    key: util.dynamodb.toMapValues({ userId }),
  };
}

export function response(ctx) {
  if (ctx.error) {
    util.error(ctx.error.message, ctx.error.type);
  }

  const item = ctx.result;

  if (!item) {
    return {
      userId: ctx.arguments.userId,
      isOnline: false,
      lastSeen: null,
      platform: null
    };
  }

  return {
    userId: item.userId,
    isOnline: item.isOnline || false,
    lastSeen: item.lastSeen,
    platform: item.platform
  };
}
