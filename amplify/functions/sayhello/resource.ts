import { defineFunction, secret } from '@aws-amplify/backend';

export const sayHello = defineFunction({
  name: 'say-hello',
  entry: './handler.ts',
    environment: {
      ABLY_API_KEY: secret('ABLY_API_KEY'),
    },
});