import { defineFunction, secret } from '@aws-amplify/backend';

export const hasUserCreatedProfile = defineFunction({
  name: 'hasUserCreatedProfile',
  entry: './handler.ts',
  environment: {
    // Environment variables will be added by Amplify when connected to data
  },
  timeoutSeconds: 30,
});