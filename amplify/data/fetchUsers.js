import * as ddb from "@aws-appsync/utils/dynamodb";

/*
export function request(ctx) {
  const result = ddb.get({ key: { userName: ctx.args.userName } });
}

export const response = (ctx) => ctx.result;*/


export function request(ctx) {
  try {
    console.log("Request received. userName: ", ctx.args.userName);
    const result = ddb.get({ key: { userName: ctx.args.userName } });
    console.log("DynamoDB result: ", result);
    return result;  // Return the result to AppSync
  } catch (error) {
    console.error("Error request:", error);
    throw new Error(`Failed to fetch user data: ${error.message}`);
  }
}

export const response = (ctx) => {
  try {
    console.log("DynamoDB: ", ctx.result);
    return ctx.result;  // Return the response to AppSync
  } catch (error) {
    console.error("Error FETCH USER response:", error);
    throw new Error(`Failed to handle the response: ${error.message}`);
  }
};
