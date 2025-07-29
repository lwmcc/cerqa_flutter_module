import { defineFunction } from '@aws-amplify/backend';

export const searchUsersByPhone = defineFunction({
  name: 'search-users-by-phone',
  entry: './handler.ts',
});