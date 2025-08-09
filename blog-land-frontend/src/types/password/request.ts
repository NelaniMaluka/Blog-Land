export type changePasswordRequest = {
  oldPassword: string;
  newPassword: string;
  repeatPassword: string;
};

export type changePasswordWithTokenRequest = {
  token: string;
  newPassword: string;
  repeatPassword: string;
};
