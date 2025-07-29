import Ably from 'ably';
import type { Handler } from 'aws-lambda';
import { FunctionHandler } from 'aws-amplify-function-runtime-nodejs';
import crypto from 'crypto';
import { secret } from 'aws-amplify-function-runtime-nodejs';

export const handler: Schema["sayHello"]["functionHandler"] = async (event) => { const { name } = event.arguments

    const ABLY_KEY = process.env.ABLY_KEY
    const ABLY_SECRET = process.env.ABLY_SECRET

    const header = {
        typ: "JWT",
        alg: "HS256",
        kid: ABLY_KEY,
    };

    const currentTime = Math.floor(Date.now() / 1000);
    const claims = {
        iat: currentTime,
        exp: currentTime + 3600,
        "x-ably-capability": JSON.stringify({ "*": ["*"] }),
    };

    const base64Header = Buffer.from(JSON.stringify(header)).toString('base64url');
    const base64Claims = Buffer.from(JSON.stringify(claims)).toString('base64url');
    const dataToSign = `${base64Header}.${base64Claims}`;

    const signature = crypto
        .createHmac('sha256', ABLY_SECRET)
        .update(dataToSign)
        .digest('base64url');

  const ablyJwt = `${base64Header}.${base64Claims}.${signature}`;

    return {
        statusCode: 200,
        body: JSON.stringify({ token: ablyJwt }),
    };
};
