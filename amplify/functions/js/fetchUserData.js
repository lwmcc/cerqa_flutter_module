const { DynamoDB } = require('aws-sdk');
const db = new DynamoDB.DocumentClient();

exports.handler = async (event) => {

const { userName } = event.arguments;

    try {
        return userName
    } catch(e: Exception) {

    }
}