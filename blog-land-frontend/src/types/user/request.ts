import { Provider } from './response';
import { ExperienceLevel } from './response';

export type UpdateUserRequest = {
  firstname: string;
  lastname: string;
  email: string;
  summary?: string;
  title?: string;
  provider: Provider;
  location?: string | null;
  experience?: ExperienceLevel | null;
  socials?: Record<string, string> | null;
};
