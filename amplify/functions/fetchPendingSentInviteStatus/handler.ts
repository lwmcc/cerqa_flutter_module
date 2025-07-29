import type { Schema } from "../../schema";

export const handler: Schema["fetchPendingSentInviteStatus"]["functionHandler"] = async (context) => {

  const { userName } = context.arguments;

  return {
      userName: userName,
      contacts: "",
      invites: "",
  };
};