import type { Schema } from './amplify/data/resource';
import type { Handler } from 'aws-lambda';
import { FunctionHandler } from 'aws-amplify-function-runtime-nodejs';

export const handler: Schema["fetchUsersByUserName"]["functionHandler"] = async (event) => {
  const { userName, loggedInUserId } = event.arguments;

 const users = await event.context.db.User.findMany({
    where: (user, { beginsWith, not, equals }) =>
      beginsWith(user.userName, userName) &&
      not(equals(user.userId, loggedInUserId)),
  });

  return JSON.stringify(users);
};
