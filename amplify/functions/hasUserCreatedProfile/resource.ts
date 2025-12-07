import { defineFunction } from '@aws-amplify/backend';

export const hasUserCreatedProfile = defineFunction({
  name: 'hasUserCreatedProfile',
  entry: './handler.ts',
});