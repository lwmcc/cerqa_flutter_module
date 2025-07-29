import type { Handler } from 'aws-lambda';
import { FunctionHandler } from 'aws-amplify-function-runtime-nodejs';
import { type Schema } from "../../schema";
import { generateClient } from "aws-amplify/data";

const client = generateClient<Schema>();

export const handler: Schema["fetch-user-with-contact-info"]["functionHandler"] = async (event) => {
    const { userName } = event.arguments

   /*  const users = await client.models.User.list({
        filter: { userName: { eq: userName } },
        limit: 1
      });

    const user = users.data?.[0];

    const contactsResult = await client.models.UserContact.list({
        filter: { userId: { eq: user.id } }
    });

    const invitesResult = await client.models.Invite.list({
        filter: { userId: { eq: user.id } }
    }); */

   return `USER NAME TEST ${userName}`;

/*     return {
        id: user.id,
        userName: user.userName,
        contacts: contactsResult.data,
        invites: invitesResult.data,
    }; */

};
