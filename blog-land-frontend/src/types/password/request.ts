export type changePasswordRequest = {
  oldPassword: string;
  newPassword: string;
  repeatPassword: string;
};

export type forgotPasswordRequest = {
  newPassword: string;
  repeatPassword: string;
};
