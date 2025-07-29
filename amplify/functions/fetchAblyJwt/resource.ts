import { defineFunction, secret } from '@aws-amplify/backend';

export const fetchAblyJwt = defineFunction({
  name: 'fetch-ably-jwt',
  entry: './handler.ts',
  environment: {
    ABLY_KEY: secret('ably_key'),
    ABLY_SECRET: secret('ably_secret'),
  },
});