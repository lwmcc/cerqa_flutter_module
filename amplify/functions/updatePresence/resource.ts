import { defineFunction } from '@aws-amplify/backend';

export const updatePresence = defineFunction({
  name: 'updatePresence',
  entry: './handler.ts',
});
