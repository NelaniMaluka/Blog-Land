import apiClient from "./apiClient";

export const subscribeToNewsletter = async (email) => {
  const response = await apiClient.post("/newsletter", { email });
  return response;
};
