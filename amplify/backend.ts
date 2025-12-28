import { defineBackend } from '@aws-amplify/backend';
import { auth } from './auth/resource.js';
import { data } from './data/resource';
import { fetchAblyJwt } from './functions/fetchAblyJwt/resource';
import { fetchUserWithContactInfo } from './functions/fetchUserWithContactInfo/resource';
import { fetchPendingSentInviteStatus } from './functions/fetchPendingSentInviteStatus/resource';
import { hasUserCreatedProfile } from './functions/hasUserCreatedProfile/resource';
import { getUserByUserId } from './functions/getUserByUserId/resource';
import { storeFcmToken } from './functions/storeFcmToken/resource';
import { sendInviteNotification } from './functions/sendInviteNotification/resource';
import { PolicyStatement } from 'aws-cdk-lib/aws-iam';

export const backend = defineBackend({
  auth,
  data,
  fetchAblyJwt,
  fetchUserWithContactInfo,
  fetchPendingSentInviteStatus,
  hasUserCreatedProfile,
  getUserByUserId,
  storeFcmToken,
  sendInviteNotification,
});

// Grant the Lambda function access to query the User table
backend.hasUserCreatedProfile.resources.lambda.addEnvironment(
  'API_ENDPOINT',
  backend.data.resources.graphqlApi.graphqlUrl
);

// Grant storeFcmToken Lambda permission to access existing FcmToken DynamoDB table
backend.storeFcmToken.resources.lambda.addToRolePolicy(
  new PolicyStatement({
    actions: ['dynamodb:PutItem', 'dynamodb:GetItem', 'dynamodb:UpdateItem'],
    resources: ['arn:aws:dynamodb:us-east-2:*:table/FcmToken']
  })
);

// Grant sendInviteNotification Lambda permission to query FcmToken table
backend.sendInviteNotification.resources.lambda.addToRolePolicy(
  new PolicyStatement({
    actions: ['dynamodb:Query', 'dynamodb:GetItem'],
    resources: ['arn:aws:dynamodb:us-east-2:*:table/FcmToken']
  })
);


