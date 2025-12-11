import type { Schema } from "../../data/resource";
import { Amplify } from "aws-amplify";
import { generateClient } from "aws-amplify/data";
import { env } from "$amplify/env/hasUserCreatedProfile";

const client = generateClient<Schema>({
  authMode: "identityPool",
});

export const handler: Schema["checkProfileComplete"]["functionHandler"] = async (event) => {
  const { userId } = event.arguments;

  console.log("hasUserCreatedProfile - Checking profile for userId:", userId);

  try {
    // Query the User table to get the user's profile data
    const { data: user, errors } = await client.models.User.get({ id: userId });

    if (errors || !user) {
      console.error("Error fetching user:", errors);
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