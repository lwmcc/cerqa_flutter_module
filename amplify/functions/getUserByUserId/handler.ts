import type { Schema } from "../../data/resource";

// Simple mock for now - just return hardcoded users until Lambda deployment is fixed
export const handler: Schema["getUserByUserId"]["functionHandler"] = async (event) => {
  const { userId } = event.arguments;

  console.log("getUserByUserId - Fetching user for userId:", userId);

  // Hardcoded user data for testing
  const users: Record<string, any> = {
    "c16bb540-b061-70e5-da91-7f7b137f42ee": {
      id: "e9ccfc75-bd3c-4f7b-a707-7e730b558e27",
      userId: "c16bb540-b061-70e5-da91-7f7b137f42ee",
      userName: "kingjames",
      firstName: "LeBron",
      lastName: "James",
      name: "LeBron James",
      email: "admin@cerqa.net",
      phone: "4803331312",
      avatarUri: null
    },
    "e19b1520-0051-7037-ac0b-0c441e32fa0f": {
      id: "larry-profile-id-123",
      userId: "e19b1520-0051-7037-ac0b-0c441e32fa0f",
      userName: "larrym",
      firstName: "Larry",
      lastName: "McCarty",
      name: "Larry McCarty",
      email: "larry@example.com",
      phone: "5551234567",
      avatarUri: null
    }
  };

  const user = users[userId];
  if (user) {
    console.log("Found user:", JSON.stringify(user, null, 2));
    return user;
  }

  console.log("User not found");
  return null;
};
