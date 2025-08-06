import apiClient from "./apiClient";

export const registerUser = async (firstname, password, lastname, email) => {
  const response = await apiClient.post("/auth/register", {
    firstname,
    password,
    lastname,
    email,
  });
  return response;
};

export const loginUser = async (email, password) => {
  const response = await apiClient.post("/auth/login", {
    email,
    password,
  });
  return response;
};

export const getUserDetails = async () => {
  const response = await apiClient.get("/user/get-user", {});
  return response;
};

export const updateUserDetails = async (
  firstname,
  lastname,
  email,
  provider,
  profileIconUrl,
  location,
  experience,
  socials
) => {
  const response = await apiClient.put("/user/update-user", {
    firstname,
    lastname,
    email,
    provider,
    profileIconUrl,
    location,
    experience,
    socials,
  });
  return response;
};

export const deleteUserDetails = async () => {
  const response = await apiClient.delete("/user/delete-user", {});
  return response;
};
