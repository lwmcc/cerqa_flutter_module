import { defineFunction } from '@aws-amplify/backend';

export const cleanupUnknownData = defineFunction({
  name: 'cleanupUnknownData',
  timeoutSeconds: 300, // 5 minutes
});
