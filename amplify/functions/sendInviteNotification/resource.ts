import { defineFunction, secret } from '@aws-amplify/backend';

export const sendInviteNotification = defineFunction({
  name: 'sendInviteNotification',
  entry: './handler.ts',
  environment: {
    ABLY_API_KEY: secret('ABLY_API_KEY'),
  },
  bundling: {
    externalModules: ['@aws-sdk/client-dynamodb', 'firebase-admin', 'ably']
  }
});
