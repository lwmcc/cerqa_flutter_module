import { type ClientSchema, a, defineData, defineFunction } from "@aws-amplify/backend";
//import { sayHello } from "../functions/sayhello/resource"
import { fetchAblyJwt } from "../functions/fetchAblyJwt/resource";
import { fetchUserWithContactInfo } from "../functions/fetchUserWithContactInfo/resource";
import { fetchPendingSentInviteStatus } from "../functions/fetchPendingSentInviteStatus/resource";
import { createInviteByPhoneNumber } from "../functions/createInviteByPhoneNumber/resource";

const AblyJwt = a.customType({
      keyName: a.string().required(),
      clientId: a.string().required(),
      timestamp: a.float().required(),
      nonce: a.string().required(),
      mac: a.string().required(),
    });

const UserWithContactInfoResponse = a.customType({
      id: a.string(),
      userName: a.string(),
});

const PendingInviteStatusResponse = a.customType({
    userName: a.string().required(),
    contacts: a.string(),
    invites: a.string(),
});

const ContactInfo = a.customType({
  id: a.string().required(),
  name: a.string(),
  phone: a.string(),
});

const InviteInfo = a.customType({
  id: a.string().required(),
  senderId: a.string(),
  receiverId: a.string(),
});

const UserContactData = a.customType({
     userName: a.string().required(),
     contacts: a.string().required(),
     invites: a.string().required(),
});

const UserByPhone = a.customType({
      userName: a.string(),
});

export const schema = a.schema({

     User: a
         .model({
           userId: a.id(), // TODO: will get rid of userId for id
           firstName: a.string().required(),
           lastName: a.string().required(),
           name: a.string(),
           phone: a.string(),
           userName: a.string(),
           email: a.email(),
           avatarUri: a.url(),
           contacts: a.hasMany('UserContact', 'userId'), // Contact owner
           asContact: a.hasMany('UserContact', 'contactId'), // contact user
           groups: a.hasMany('UserGroup', 'userId'),
           invites: a.hasMany('Invite','userId'),
           channels: a.hasMany('Channel', 'userId'),
         })
         .secondaryIndexes((index) => [
                   index("phone")
                   .sortKeys(["userName"])
                   .queryField("listByPhone")
                   .name("userIndexPhone")
               ])
         .authorization((allow) => [allow.publicApiKey()]),

   Group: a
     .model({
       groupId: a.id().required(),
       name: a.string().required(),
       users: a.hasMany('UserGroup', 'groupId'),
     })
     .authorization((allow) => [allow.publicApiKey()]),

    Invite: a
    .model({
      userId: a.id().required(),
      senderId: a.string(),
      receiverId: a.string(),
      user: a.belongsTo('User', 'userId'),
    })
    .authorization((allow) => [allow.publicApiKey()]),

   UserContact: a
     .model({
       userId: a.id().required(),
       contactId: a.id().required(),
       user: a.belongsTo('User', 'userId'),
       contact: a.belongsTo('User', 'contactId'),
     })
     .authorization((allow) => [allow.publicApiKey()]),

   UserGroup: a
     .model({
       userId: a.id().required(),
       groupId: a.id().required(),
       user: a.belongsTo('User', 'userId'),
       group: a.belongsTo('Group', 'groupId'),
     })
     .authorization((allow) => [allow.publicApiKey()]),

   Channel: a
   .model({
       name: a.string(),
       userId: a.id().required(),
       user: a.belongsTo('User', 'userId'),
       messages: a.hasMany('Message', 'channelId'),
   })
   .authorization((allow) => [allow.publicApiKey()]),

    Message: a
     .model({
         content: a.string().required(),
         channelId: a.id().required(),
         channel: a.belongsTo('Channel', 'channelId'),
     })
    .authorization((allow) => [allow.publicApiKey()]),

/*     sayHello: a
        .query()
        .arguments({ name: a.string() })
        .returns(a.string())
        .authorization((allow) => [allow.publicApiKey()])
        .handler(a.handler.function(sayHello)), */

    fetchAblyJwt: a
        .query()
        .arguments({userId: a.string()})
        .authorization((allow) => [allow.publicApiKey()])
        .returns(AblyJwt)
        .handler(a.handler.function(fetchAblyJwt)),

    fetchUserWithContactInfo: a
        .query()
        .arguments({userName: a.string()})
        .authorization((allow) => [allow.publicApiKey()])
        .returns(UserWithContactInfoResponse)
        .handler(a.handler.function(fetchUserWithContactInfo)),

     fetchPendingSentInviteStatus: a
        .query()
        .arguments({userName: a.string()})
        .authorization((allow) => [allow.publicApiKey()])
        .returns(a.string())
        .handler(a.handler.function(fetchPendingSentInviteStatus)),

});

export type Schema = ClientSchema<typeof schema>;

export const data = defineData({
    schema,
    secrets: ["ably_key", "ably_secret"],
    authorizationModes: {
    defaultAuthorizationMode: "apiKey",
        apiKeyAuthorizationMode: {
          expiresInDays: 30,
        },
    },
});
