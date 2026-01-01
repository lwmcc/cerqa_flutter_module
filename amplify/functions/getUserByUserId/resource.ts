import { defineFunction } from '@aws-amplify/backend';

export const getUserByUserId = defineFunction({
  name: 'getUserByUserId',
  entry: './handler.ts',
  environment: {
    // Environment variables will be added by Amplify when connected to data
  },
  timeoutSeconds: 30
});
