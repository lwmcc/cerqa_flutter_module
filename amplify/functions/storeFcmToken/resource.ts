import { defineFunction } from '@aws-amplify/backend';

export const storeFcmToken = defineFunction({
  name: 'storeFcmToken',
  entry: './handler.ts',
  bundling: {
    externalModules: ['@aws-sdk/client-dynamodb']
  }
});
