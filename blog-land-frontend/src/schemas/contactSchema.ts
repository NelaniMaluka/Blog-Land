import z from 'zod';

export const addContectSchema = z.object({
  fullName: z.string().min(1, { message: 'Fullname cannot be empty' }),
  email: z.string().email({ message: 'Please enter a valid email address' }),
  message: z.string().min(1, { message: 'Message cannot be empty' }),
});
