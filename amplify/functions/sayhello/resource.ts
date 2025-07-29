import { defineFunction, secret } from '@aws-amplify/backend';

export const sayHello = defineFunction({
  name: 'say-hello',
  entry: './handler.ts',
    environment: {
      ABLY_KEY: secret('ably_key'),
      ABLY_SECRET: secret('ably_secret'),
    },
});