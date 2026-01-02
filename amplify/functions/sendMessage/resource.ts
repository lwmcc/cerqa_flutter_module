import { defineFunction } from "@aws-amplify/backend";

export const sendMessage = defineFunction({
  name: "sendMessage",
  environment: {
    ABLY_API_KEY: secret('ABLY_API_KEY'),
  },
});