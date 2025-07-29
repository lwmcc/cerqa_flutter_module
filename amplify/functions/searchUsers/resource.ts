import { defineFunction } from '@aws-amplify/backend';

export const searchUsers = defineFunction({
  name: 'search-users',
  entry: './handler.ts',
});