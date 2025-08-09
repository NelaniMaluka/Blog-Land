import { z } from 'zod';
import { Order } from '../types/post/response';

export const orderSchema = z.enum([Order.LATEST, Order.OLDEST]);

export const paginationSchema = z.object({
  page: z.number().int().min(1).default(0),
  size: z.number().int().min(1).max(100).default(10),
  order: orderSchema,
});

export const paginationWithCategoryIdSchema = z.object({
  categoryId: z.number().int().min(1, { message: 'Invalid category ID' }),
  page: z.number().int().min(1).default(0),
  size: z.number().int().min(1).max(100).default(10),
  order: orderSchema,
});
