import { defineFunction } from '@aws-amplify/backend';

export const fetchPendingSentInviteStatus = defineFunction({
  name: 'fetch-pending-sent-invite-status',
  entry: './handler.ts',
});