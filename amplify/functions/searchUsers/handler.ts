import type { Handler } from 'aws-lambda';
import { FunctionHandler } from 'aws-amplify-function-runtime-nodejs';
import { secret } from 'aws-amplify-function-runtime-nodejs';

export const handler: Schema["searchUsers"]["functionHandler"] = async (event) => {
    const { userName } = event.arguments

}