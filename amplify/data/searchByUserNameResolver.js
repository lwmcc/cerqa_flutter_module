import { util } from "@aws-appsync/utils";

// TODO: used for OpenSearch which I have turned off because of the COST!!!
export function request(ctx) {
  const { userName, loggedInUserId } = ctx.args;

  return {
    operation: "POST",
    path: "/user/_search",
    params: {
      body: {
        query: {
          bool: {
            must: {
              wildcard: {
                "userName": `*${userName}*`
              }
            },
            must_not: {
              term: {
                "userId": loggedInUserId
              }
            }
          }
        },
        size: 50,
      },
    },
  };
}

export function response(ctx) {
  if (ctx.error) {
    util.error(ctx.error.message, ctx.error.type);
  }
  return ctx.result.hits.hits.map((hit) => hit._source);
}