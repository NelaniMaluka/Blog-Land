import { z } from 'zod';

const passwordSchema = z
  .string()
  .min(6, { message: 'Password must be at least 6 characters long' })
  .regex(/[A-Z]/, { message: 'Password must contain at least one uppercase letter' })
  .regex(/[a-z]/, { message: 'Password must contain at least one lowercase letter' })
  .regex(/[0-9]/, { message: 'Password must contain at least one number' })
  .regex(/[^A-Za-z0-9]/, { message: 'Password must contain at least one special character' })
  .refine((val) => !/\s/.test(val), {
    message: 'Password must not contain spaces',
  });

export const registerSchema = z.object({
  firstname: z.string().min(1, { message: 'First name is required' }),
  lastname: z.string().min(1, { message: 'Last name is required' }),
  email: z.string().email({ message: 'Please enter a valid email address' }),
  password: passwordSchema,
});

export const loginSchema = z.object({
  email: z.string().email({ message: 'Please enter a valid email address' }),
  password: passwordSchema,
});

export const changePasswordSchema = z.object({
  oldPassword: passwordSchema,
  newPassword: passwordSchema,
  repeatPassword: passwordSchema,
});

export const changePasswordWithTokenSchema = z.object({
  token: z.string().min(1, { message: 'Token cannot be empty' }),
  newPassword: passwordSchema,
  repeatPassword: passwordSchema,
});
