import { util } from '@aws-appsync/utils';
import * as ddb from "@aws-appsync/utils/dynamodb";

export function request(ctx) {
    const { phone } = ctx.args;

    if (!phoneNumber) {
        util.error("Missing phoneNumber", "BadRequest");
    }

    // return ddb.get({ key: { id: phoneNumber } });
    return ddb.query({
        index: 'userIndexPhone',
        query: {
            phone: { eq: phone }
        }
    });
}

export const response = (ctx) => ctx.result;
//export const response = (ctx) => ctx.result?.items ?? [];

// id 4254ae6d-f887-4dab-b2b0-1d2b5a0b4789
// From dynamoDB console
// +14808104545 and +14805254545



// phone +14805454545 +14804541111