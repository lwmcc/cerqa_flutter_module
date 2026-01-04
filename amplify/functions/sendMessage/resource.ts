import { defineFunction, secret } from "@aws-amplify/backend";

export const sendMessage = defineFunction({
  name: "sendMessage",
  entry: "./handler.ts",
  environment: {
    ABLY_API_KEY: secret('ABLY_API_KEY'),
  },
  bundling: {
    externalModules: ['@aws-sdk/client-dynamodb', 'ably']
  }
});