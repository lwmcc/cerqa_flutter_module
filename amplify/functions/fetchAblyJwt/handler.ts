import Ably from 'ably';
import type { Handler } from 'aws-lambda';
import { FunctionHandler } from 'aws-amplify-function-runtime-nodejs';
import { type Schema } from '../../data/resource';

export const handler: Schema["fetchAblyJwt"]["functionHandler"] = async (event) => {
    const { userId } = event.arguments

    const key = process.env.ABLY_API_KEY;

    if (!key) {
        throw new Error('ABLY_API_KEY environment variable is not set');
    }

    const ably = new Ably.Rest({ key: key });

    const tokenRequest = await ably.auth.createTokenRequest({
        clientId: userId
    });

    return {
        keyName: tokenRequest.keyName,
        clientId: tokenRequest.clientId,
        timestamp: tokenRequest.timestamp,
        nonce: tokenRequest.nonce,
        mac: tokenRequest.mac,
    };
};
