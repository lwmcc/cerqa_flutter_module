import { defineBackend } from '@aws-amplify/backend';
import { auth } from './auth/resource.js';
import { data } from './data/resource';

export const backend = defineBackend({
  auth,
  data,
});

// Grant the Lambda function access to query the User table
backend.hasUserCreatedProfile.resources.lambda.addEnvironment(
  'API_ENDPOINT',
  backend.data.resources.graphqlApi.graphqlUrl
);
