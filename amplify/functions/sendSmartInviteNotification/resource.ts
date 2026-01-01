import { defineFunction, secret } from '@aws-amplify/backend';

export const sendSmartInviteNotification = defineFunction({
  name: 'sendSmartInviteNotification',
  entry: './handler.ts',
  environment: {
    FIREBASE_SERVICE_ACCOUNT: secret('FIREBASE_SERVICE_ACCOUNT'),
    ABLY_API_KEY: secret('ABLY_API_KEY'),
  },
  timeoutSeconds: 30,
  bundling: {
    externalModules: ['@aws-sdk/client-dynamodb', 'firebase-admin', 'ably']
  }
});
