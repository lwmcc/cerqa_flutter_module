import { type ClientSchema, a, defineData, defineFunction } from "@aws-amplify/backend";
import { fetchAblyJwt } from "../functions/fetchAblyJwt/resource";
import { fetchUserWithContactInfo } from "../functions/fetchUserWithContactInfo/resource";
import { fetchPendingSentInviteStatus } from "../functions/fetchPendingSentInviteStatus/resource";
import { hasUserCreatedProfile } from "../functions/hasUserCreatedProfile/resource";
import { getUserByUserId } from "../functions/getUserByUserId/resource";
import { sendInviteNotification } from "../functions/sendInviteNotification/resource";
import { sendSmartInviteNotification } from "../functions/sendSmartInviteNotification/resource";
import { updatePresence } from "../functions/updatePresence/resource";
// import { sendMessage } from "../functions/sendMessage/resource"; // TODO: Fix bundling issues

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

const ProfileCheckResult = a.customType({
      isProfileComplete: a.boolean().required(),
      missingFields: a.string().array().required(),
});

const UserData = a.customType({
      id: a.string(),
      userId: a.string(),
      userName: a.string(),
      email: a.string(),
      avatarUri: a.string(),
      firstName: a.string(),
      lastName: a.string(),
      name: a.string(),
      phone: a.string(),
});

const NotificationResult = a.customType({
      success: a.boolean().required(),
      message: a.string().required(),
      deviceCount: a.integer(),
});

const PresenceStatus = a.customType({
      userId: a.string().required(),
      isOnline: a.boolean().required(),
      lastSeen: a.string(),
      platform: a.string(),
});

const InviteNotificationResult = a.customType({
      success: a.boolean().required(),
      message: a.string().required(),
      deliveryMethod: a.string().required(), // "FCM" or "ABLY"
      channelName: a.string(),
});

const SendMessageResult = a.customType({
  success: a.boolean().required(),
  messageId: a.string(),
  channelId: a.string().required(),
  deliveredVia: a.string().required(), // "ABLY" | "DB"
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
           createdChannels: a.hasMany('Channel', 'creatorId'),
           receivedChannels: a.hasMany('Channel', 'receiverId'),
           messages: a.hasMany('Message', 'senderId'),
           notifications: a.hasMany('Notification', 'userId'),
         })
         .secondaryIndexes((index) => [
                   index("phone")
                   .sortKeys(["userName"])
                   .queryField("listByPhone")
                   .name("userIndexPhone")
               ])
         .authorization((allow) => [
           allow.authenticated(), // Allow any authenticated user
           allow.publicApiKey() // Keep API key for backwards compatibility
         ]),

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
    .authorization((allow) => [
      allow.authenticated(),
      allow.publicApiKey(),
    ]),

   UserContact: a
     .model({
       userId: a.id().required(),
       contactId: a.id().required(),
       user: a.belongsTo('User', 'userId'),
       contact: a.belongsTo('User', 'contactId'),
     })
     .authorization((allow) => [
       allow.authenticated(), // Allow any authenticated user
       allow.publicApiKey() // Keep API key for backwards compatibility
     ]),

   UserGroup: a
     .model({
       userId: a.id().required(),
       groupId: a.id().required(),
       role: a.enum(['CREATOR', 'MODERATOR', 'MEMBER']),
       user: a.belongsTo('User', 'userId'),
       group: a.belongsTo('Group', 'groupId'),
     })
     .authorization((allow) => [allow.publicApiKey()]),

   Channel: a
   .model({
       name: a.string(),
       creatorId: a.id().required(),
       receiverId: a.id(),
       creator: a.belongsTo('User', 'creatorId'),
       receiver: a.belongsTo('User', 'receiverId'),
       isGroup: a.boolean().default(false),
       isPublic: a.boolean().default(false),
       messages: a.hasMany('Message', 'channelId'),
   })
   .authorization((allow) => [allow.publicApiKey(), allow.authenticated()]),

    Message: a
      .model({
        content: a.string().required(),
        channelId: a.id().required(),
        senderId: a.id().required(),
        sender: a.belongsTo('User', 'senderId'),
        channel: a.belongsTo('Channel', 'channelId'),
      })
      // TODO: Re-enable grant once we verify the correct syntax
      // .grant(sendMessage, [a.allow.create()])
      .authorization((allow) => [allow.authenticated(),allow.publicApiKey(),
     ]),

    Notification: a
     .model({
         userId: a.id().required(), // User who receives the notification
         type: a.string().required(), // "INVITE", "MESSAGE", "GROUP_INVITE", etc.
         title: a.string().required(),
         message: a.string().required(),
         isRead: a.boolean().default(false),
         relatedId: a.string(), // ID of related entity (inviteId, messageId, etc.)
         senderUserId: a.string(), // User who triggered the notification
         senderName: a.string(), // Name of sender for display
         user: a.belongsTo('User', 'userId'),
     })
     .authorization((allow) => [
       allow.authenticated(),
       allow.publicApiKey(),
     ]),

    FcmToken: a
     .model({
         userId: a.id().required(),
         deviceId: a.id().required(),
         token: a.string().required(),
         platform: a.string().required(),
     })
     .identifier(['userId', 'deviceId'])
     .authorization((allow) => [
       allow.authenticated(),
       allow.publicApiKey(),
     ]),

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

    hasUserCreatedProfile: a
        .query()
        .arguments({userId: a.string().required()})
        .returns(ProfileCheckResult)
        .authorization(allow => [allow.authenticated(), allow.publicApiKey()])
        .handler(a.handler.function(hasUserCreatedProfile)),

    getUserByUserId: a
        .query()
        .arguments({userId: a.string().required()})
        .returns(UserData)
        .authorization(allow => [allow.authenticated(), allow.publicApiKey()])
        .handler(a.handler.function(getUserByUserId)),

    sendInviteNotification: a
        .mutation()
        .arguments({
            recipientUserId: a.string().required(),
            senderName: a.string().required(),
            inviteId: a.string().required()
        })
        .returns(NotificationResult)
        .authorization(allow => [allow.authenticated()])
        .handler(a.handler.function(sendInviteNotification)),

    updatePresence: a
        .mutation()
        .arguments({
            userId: a.string().required(),
            isOnline: a.boolean().required(),
            platform: a.string()
        })
        .returns(a.boolean())
        .authorization(allow => [allow.authenticated(), allow.publicApiKey()])
        .handler(a.handler.function(updatePresence)),

        // TODO: Re-enable once sendMessage bundling is fixed
        // sendMessage: a
        //   .mutation()
        //   .arguments({
        //     channelId: a.id().required(),
        //     senderUserId: a.id().required(),
        //     content: a.string().required(),
        //   })
        //   .returns(SendMessageResult)
        //   .authorization((allow) => [
        //     allow.authenticated(),
        //   ])
        //   .handler(a.handler.function(sendMessage)),

});

export type Schema = ClientSchema<typeof schema>;

export const data = defineData({
    schema,
    //secrets: ["ably_key", "ably_secret"],
    secrets: ['ABLY_API_KEY'],
    authorizationModes: {
        defaultAuthorizationMode: "userPool",
        apiKeyAuthorizationMode: {
          expiresInDays: 30,
        },
    },
});
