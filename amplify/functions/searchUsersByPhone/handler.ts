export const handler = async (ctx) => {
  const phoneNumbers = ctx.arguments?.phoneNumbers ?? [];

  if (!Array.isArray(phoneNumbers)) {
    throw new Error('Invalid phoneNumbers');
  }

  const results = await Promise.all(
    phoneNumbers.map(async (phone) => {
      const res = await ctx.data.User.query({
        indexName: "userIndexPhone",
        query: { phone: { eq: phone } },
      });
      return res.items ?? [];
    })
  );

  return results.flat();
};

