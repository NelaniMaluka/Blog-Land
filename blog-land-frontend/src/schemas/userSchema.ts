import { z } from 'zod';
import { Provider, ExperienceLevel } from '../types/user/response';

// Validate provider enum
export const providerSchema = z.enum([Provider.GOOGLE, Provider.LOCAL]);

// Validate experience enum
export const experienceSchema = z.enum([
  ExperienceLevel.NEW_BLOGGER,
  ExperienceLevel.CASUAL_POSTER,
  ExperienceLevel.COMMUNITY_WRITER,
  ExperienceLevel.FREQUENT_CONTRIBUTOR,
  ExperienceLevel.PRO_BLOGGER,
]);

// Each social link is an optional string that should be a valid URL if present
const socialLinkSchema = z
  .string()
  .trim()
  .refine((val) => val === '' || /^https?:\/\/[^\s/$.?#].[^\s]*$/i.test(val), {
    message: 'Please enter a valid URL',
  });

// Main update user schema
export const updateUserSchema = z.object({
  firstname: z.string().min(1, { message: 'First name cannot be empty' }),
  lastname: z.string().min(1, { message: 'Last name cannot be empty' }),
  email: z.string().email({ message: 'Please enter a valid email address' }),
  provider: providerSchema,
  title: z.string().optional(),
  summary: z.string().optional(),
  profileIconUrl: z.string().optional(),
  location: z.string().optional(),
  experience: experienceSchema.optional(),
  socials: z.record(z.string(), socialLinkSchema).optional(),
});
