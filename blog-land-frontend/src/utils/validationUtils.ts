export const validateRequired = (value: string) => value.trim().length > 0;
export const validateEmail = (value: string) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);
export const validatePassword = (value: string) =>
  /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[^A-Za-z0-9])(?!.*\s).{6,}$/.test(value);
