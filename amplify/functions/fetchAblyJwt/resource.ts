import { defineFunction, secret } from '@aws-amplify/backend';

export const fetchAblyJwt = defineFunction({
  name: 'fetch-ably-jwt',
  entry: './handler.ts',
  environment: {
    ABLY_API_KEY: secret('ABLY_API_KEY'),
  },
});