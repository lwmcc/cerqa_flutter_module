import { defineBackend } from '@aws-amplify/backend';
import { auth } from './auth/resource';
import { data } from './data/resource';
import { aws_dynamodb } from "aws-cdk-lib";
import { fetchAblyJwt } from './functions/fetchAblyJwt/resource';
import { fetchUserWithContactInfo } from './functions/fetchUserWithContactInfo/resource';
import { fetchPendingSentInviteStatus } from './functions/fetchPendingSentInviteStatus/resource';
import { hasUserCreatedProfile } from './functions/hasUserCreatedProfile/resource';

const backend = defineBackend({
  auth,
  data,
  fetchAblyJwt,
  fetchUserWithContactInfo,
  fetchPendingSentInviteStatus,
  hasUserCreatedProfile,
});

