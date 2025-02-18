import { type ClientSchema, a, defineData } from '@aws-amplify/backend';
import { defineAuth } from "@aws-amplify/backend"

const schema = a.schema({
    User: a
     .model({
       id: a.id.required(),
       avatarUri: a.url(),
       email: a.email(),
       firstName: a.string(),
       lastName: a.string(),
       name: a.string(),
       phone: a.phone(),
       groups: a.hasMany('Group', 'users'),
       contacts: a.hasMany('Contact', 'userId'),
     })
     .authorization((allow) => [allow.guest()]),

    Group: a
      .model({
        id: a.id.required(),
        userId: a.id.required(),
        name: a.string().required(),
        users: a.hasMany('User', 'groups'),
      }),

    Contact: a
      .model({
        userId: a.id,
        name: a.string().required(),
        phone: a.phone(),
        email: a.email(),
        user: a.belongsTo('User', 'userId'),
      }),

    UserGroup: a
        .model({
            userId: a.id.required(),
            groupId: a.id.required(),
            user: a.belongsTo('User', 'userId'),
            group: a.belongsTo('Group', groupId),
        })
});

export type Schema = ClientSchema<typeof schema>;

const data = defineData({
  schema,
  authorizationModes: {
    defaultAuthorizationMode: 'iam',
  },
});

// generate your data client using the Schema from your backend
const client = generateClient<Schema>();

const { data } = await client.models.Users.list();

/*== STEP 2 ===============================================================
Go to your frontend source code. From your client-side code, generate a
Data client to make CRUDL requests to your table. (THIS SNIPPET WILL ONLY
WORK IN THE FRONTEND CODE FILE.)

Using JavaScript or Next.js React Server Components, Middleware, Server 
Actions or Pages Router? Review how to generate Data clients for those use
cases: https://docs.amplify.aws/gen2/build-a-backend/data/connect-to-API/
=========================================================================*/

/*
"use client"
import { generateClient } from "aws-amplify/data";
import type { Schema } from "@/amplify/data/resource";

const client = generateClient<Schema>() // use this Data client for CRUDL requests
*/

/*== STEP 3 ===============================================================
Fetch records from the database and use them in your frontend component.
(THIS SNIPPET WILL ONLY WORK IN THE FRONTEND CODE FILE.)
=========================================================================*/

/* For example, in a React component, you can use this snippet in your
  function's RETURN statement */
// const { data: todos } = await client.models.Todo.list()

// return <ul>{todos.map(todo => <li key={todo.id}>{todo.content}</li>)}</ul>
