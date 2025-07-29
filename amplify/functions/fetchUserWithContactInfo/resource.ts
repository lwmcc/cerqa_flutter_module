import { defineFunction } from '@aws-amplify/backend';

export const fetchUserWithContactInfo = defineFunction({
  name: 'fetch-user-with-contact-info',
  entry: './handler.ts',
});