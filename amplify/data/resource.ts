import { type ClientSchema, a, defineData } from '@aws-amplify/backend';
import { generateClient } from 'aws-amplify/data';
import { defineAuth } from "@aws-amplify/backend";

const schema = a.schema({
  User: a
    .model({
      id: a.id(),
      avatarUri: a.url(),
      email: a.email(),
      firstName: a.string().required(),
      lastName: a.string().required(),
      name: a.string(),
      phone: a.phone(),
      groups: a.hasMany('UserGroup', 'userId'),
      contacts: a.hasMany('Contact', 'id'),
    })
    .authorization((allow) => [allow.guest()]),

  Group: a
    .model({
      name: a.string().required(),
      users: a.hasMany('UserGroup', 'groupId'),
    }).authorization((allow) => [allow.guest()]),

  Contact: a
    .model({
      id: a.id(),
      name: a.string(),
      phone: a.phone(),
      email: a.email(),
      user: a.belongsTo('User', 'id'),
    }).authorization(allow => [allow.guest()]),

  UserGroup: a
    .model({
      userId: a.id(),
      groupId: a.id(),
      user: a.belongsTo('User', 'userId'),
      group: a.belongsTo('Group', 'groupId'),
    }).authorization(allow => [allow.guest()]),
});

export type Schema = ClientSchema<typeof schema>;

export const data = defineData({
  schema,
  authorizationModes: {
    defaultAuthorizationMode: 'iam',
  },
});

// Generate your data client using the schema
const client = generateClient<Schema>();

async function fetchUsers() {
  // Fetch users correctly using the plural 'User' (as per the schema definition)
  const { data } = await client.models.User.list();

  // Example of using the fetched data
  console.log(data);
}

// Call the function to fetch data
fetchUsers().catch(console.error);