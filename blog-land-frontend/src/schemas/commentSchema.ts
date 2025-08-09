import { z } from 'zod';

export const addCommentSchema = z.object({
  postId: z.number().int().min(1, { message: 'Invalid post ID' }),
  content: z.string().min(1, { message: 'Comment cannot be empty' }),
});

export const updateCommentSchema = z.object({
  id: z.number().int().min(1, { message: 'Invalid comment ID' }),
  postId: z.number().int().min(1, { message: 'Invalid post ID' }),
  content: z.string().min(1, { message: 'Comment cannot be empty' }),
});
