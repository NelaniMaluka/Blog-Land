import apiClient from "./apiClient";

export const sendContactMessage = async (fullName, email, message) => {
  const response = await apiClient.post("/contact-us", {
    fullName,
    email,
    message,
  });
  return response;
};
