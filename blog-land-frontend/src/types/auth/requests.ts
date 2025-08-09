export type RegisterRequest = {
  firstname: string;
  lastname: string;
  email: string;
  password: string;
};

export type LoginRequest = {
  email: string;
  password: string;
};
