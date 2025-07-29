import { defineFunction } from '@aws-amplify/backend';

export const fetchUsersByUserName = defineFunction({
  name: 'fetch-users-by-user-name',
  entry: './handler.ts',
});