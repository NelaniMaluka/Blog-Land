import z from 'zod';

export const addPostSchema = z.object({
  title: z.string().min(1, { message: 'First name cannot be empty' }),
  content: z.string().min(1, { message: 'First name cannot be empty' }),
  categoryId: z.number().int().min(1).default(0),
  summary: z.string().min(1, { message: 'First name cannot be empty' }),
  imgUrl: z.string().min(1, { message: 'First name cannot be empty' }),
  draft: z.boolean(),
  scheduledAt: z.string().min(1, { message: 'First name cannot be empty' }).optional(),
});

export const updatePostSchema = z.object({
  id: z.uuid({ message: 'Invalid ID format' }),
  title: z.string().min(1, { message: 'First name cannot be empty' }),
  content: z.string().min(1, { message: 'First name cannot be empty' }),
  categoryId: z.number().int().min(1).default(0),
  summary: z.string().min(1, { message: 'First name cannot be empty' }),
  imgUrl: z.string().min(1, { message: 'First name cannot be empty' }),
  draft: z.boolean(),
  scheduledAt: z.string().min(1, { message: 'First name cannot be empty' }).optional(),
});
