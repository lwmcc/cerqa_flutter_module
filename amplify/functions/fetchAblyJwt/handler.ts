import Ably from 'ably';
import type { Handler } from 'aws-lambda';
import { FunctionHandler } from 'aws-amplify-function-runtime-nodejs';
import crypto from 'crypto';
import { secret } from 'aws-amplify-function-runtime-nodejs';

export const handler: Schema["fetchAblyJwt"]["functionHandler"] = async (event) => {
    const { userId } = event.arguments

    const key = `${process.env.ABLY_KEY}:${process.env.ABLY_SECRET}`;
    const secret = process.env.ABLY_SECRET;

    const ably = new Ably.Rest({ key: key });

    const tokenRequest = await ably.auth.createTokenRequest({
        clientId: userId
    });

    return {
        keyName: tokenRequest.keyName,
        clientId: tokenRequest.clientId,
        timestamp: tokenRequest.timestamp.toString(),
        nonce: tokenRequest.nonce,
        mac: tokenRequest.mac,
      };
};
