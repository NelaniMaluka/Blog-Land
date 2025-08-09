import { z } from 'zod';
import { Provider, ExperienceLevel } from '../types/user/response';

export const providerSchema = z.enum([Provider.GOOGLE, Provider.LOCAL]);
export const experienceSchema = z.enum([
  ExperienceLevel.BEGINNER,
  ExperienceLevel.INTERMEDIATE,
  ExperienceLevel.ADVANCED,
  ExperienceLevel.EXPERT,
]);

const socialLinkSchema = z.string().min(1, { message: 'Social link cannot be empty' });

export const updateUserSchema = z.object({
  firstname: z.string().min(1, { message: 'First name cannot be empty' }),
  lastname: z.string().min(1, { message: 'Last name cannot be empty' }),
  email: z.string().email({ message: 'Please enter a valid email address' }),
  provider: providerSchema,
  profileIconUrl: z.string().min(1, { message: 'Profile icon URL cannot be empty' }).optional(),
  location: z.string().min(1, { message: 'Location cannot be empty' }).optional(),
  experience: experienceSchema.optional(),
  socials: z.record(z.string(), socialLinkSchema).optional(),
});
