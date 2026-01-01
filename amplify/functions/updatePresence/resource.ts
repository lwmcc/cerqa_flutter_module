import { defineFunction } from '@aws-amplify/backend';

export const updatePresence = defineFunction({
  name: 'updatePresence',
  entry: './handler.ts',
  bundling: {
    externalModules: ['@aws-sdk/client-dynamodb']
  }
});
