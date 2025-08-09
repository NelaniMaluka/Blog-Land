import { Provider } from './response';
import { ExperienceLevel } from './response';

export type UpdateUserRequest = {
  firstname: string;
  lastname: string;
  email: string;
  provider: Provider;
  profileIconUrl?: string | null;
  location?: string | null;
  experience?: ExperienceLevel | null;
  socials?: Record<string, string> | null;
};
