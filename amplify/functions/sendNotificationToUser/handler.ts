import type { Handler } from 'aws-lambda';
import { FunctionHandler } from 'aws-amplify-function-runtime-nodejs';

export const handler: Schema["sendNotificationToUser"]["functionHandler"] = async (event) => {

    const { receiverClientId, title, body,  } = event.arguments;



}