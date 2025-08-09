import z from 'zod';

export const idSchema = z.object({
  id: z.number().int().min(1, { message: 'Invalid ID' }),
});

export const emailSchema = z.object({
  email: z.string().email({ message: 'Please enter a valid email address' }),
});
