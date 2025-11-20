import { defineBackend } from '@aws-amplify/backend';
import { auth } from './auth/resource';
import { data } from './data/resource';
import { aws_dynamodb } from "aws-cdk-lib";
// import { sayHello } from './functions/sayhello/resource';
import { fetchAblyJwt } from './functions/fetchAblyJwt/resource';
import { fetchUserWithContactInfo } from './functions/fetchUserWithContactInfo/resource';
import { fetchPendingSentInviteStatus } from './functions/fetchPendingSentInviteStatus/resource';

const backend = defineBackend({
  auth,
  data,
  fetchAblyJwt,
  fetchUserWithContactInfo,
  fetchPendingSentInviteStatus,
});

/* const externalDataSourcesStack = backend.createStack("AppExternalDataSources");

const externalTable = aws_dynamodb.Table.fromTableName(
  externalDataSourcesStack,
  "AppExternalUserTable",
  "User-xfwkmbuuk5bupdrr53pdbt5czi-NONE",
); */

/*
backend.data.addDynamoDbDataSource(
  "ExternalUserTableDataSource",
  externalTable
 ); */
