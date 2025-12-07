import { Schema } from "../../data/resource"

export const handler: Schema["checkProfileComplete"]["functionHandler"] = async (event) => {
  const { userId } = event.arguments;

  return {
    isProfileComplete: true,
    missingFields: [],
  };
};