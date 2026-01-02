 File: amplify/functions/sendMessage/handler.tsimport { DynamoDBClient, PutItemCommand } from "@aws-sdk/client-dynamodb";
 import { Ably } from "ably";
 import { randomUUID } from "crypto";

 const ddb = new DynamoDBClient({});
 // Make sure ABLY_API_KEY is set in your environment variables
 const ably = new Ably.Rest(process.env.ABLY_API_KEY!);

 // Define the shape of the event arguments for type safety
 type SendMessageEventArgs = {
   channelId: string;
   senderUserId: string;
   content: string;
 };

 export const handler = async (event: { arguments: SendMessageEventArgs }) => {
   const { channelId, senderUserId, content } = event.arguments;

   const messageId = randomUUID();
   const timestamp = new Date().toISOString();

   // You need the Message table name from environment variables
   const tableName = process.env.AMPLIFY_DATA_MESSAGE_TABLE_NAME;
   if (!tableName) {
     throw new Error("Message table name not found in environment variables.");
   }

  await ddb.send(
    new PutItemCommand({
      TableName: tableName,
      Item: {
        id: { S: messageId },
        channelId: { S: channelId },
        senderUserId: { S: senderUserId },
        content: { S: content },
        createdAt: { S: timestamp },
        // __typename is required for AppSync to know the object type
        __typename: { S: 'Message' },
        // You might need updatedAt as well if your model requires it
        updatedAt: { S: timestamp },
      },
    })
  );

  const ablyChannel = ably.channels.get(`chat:${channelId}`);
  await ablyChannel.publish("message", {
    id: messageId,
    senderUserId,
    content,
    createdAt: timestamp,
  });

  return {
    success: true,
    messageId,
    channelId,
    deliveredVia: "ABLY",
  };
};