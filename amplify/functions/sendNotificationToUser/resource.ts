import { defineFunction } from '@aws-amplify/backend';

export const sendNotificationToUser = defineFunction({
  name: 'send-notification-to-user',
  entry: './handler.ts',
});