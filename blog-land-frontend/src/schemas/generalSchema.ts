import z from 'zod';

export const idSchema = z.uuid({ message: 'Invalid ID format' });

export const emailSchema = z.email({ message: 'Please enter a valid email address' });
