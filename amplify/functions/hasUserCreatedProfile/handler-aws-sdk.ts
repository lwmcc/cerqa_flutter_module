import { DynamoDBClient } from "@aws-sdk/client-dynamodb";
import { DynamoDBDocumentClient, GetCommand } from "@aws-sdk/lib-dynamodb";

const client = new DynamoDBClient({});
const docClient = DynamoDBDocumentClient.from(client);

// Replace with your actual DynamoDB table name
const USER_TABLE_NAME = process.env.USER_TABLE_NAME || "User-YOUR_TABLE_SUFFIX";

export const handler = async (event: any) => {
  const { userId } = event.arguments;

  console.log("hasUserCreatedProfile - Checking profile for userId:", userId);

  try {
    // Query DynamoDB to get the user's profile data
    const command = new GetCommand({
      TableName: USER_TABLE_NAME,
      Key: {
        id: userId,
      },
    });

    const response = await docClient.send(command);
    const user = response.Item;

    if (!user) {
      console.error("User not found:", userId);
      return {
        isProfileComplete: false,
        missingFields: ["User not found"],
      };
    }

    console.log("User data:", JSON.stringify(user, null, 2));

    // Define required fields for a complete profile
    const requiredFields = [
      { field: "firstName", value: user.firstName },
      { field: "lastName", value: user.lastName },
      { field: "userName", value: user.userName },
      { field: "phone", value: user.phone },
    ];

    // Check which required fields are missing
    const missingFields = requiredFields
      .filter(({ value }) => !value || value.trim() === "")
      .map(({ field }) => field);

    const isProfileComplete = missingFields.length === 0;

    console.log("Profile complete:", isProfileComplete);
    console.log("Missing fields:", missingFields);

    return {
      isProfileComplete,
      missingFields,
    };
  } catch (error) {
    console.error("Error in hasUserCreatedProfile:", error);
    return {
      isProfileComplete: false,
      missingFields: ["Error checking profile"],
    };
  }
};
