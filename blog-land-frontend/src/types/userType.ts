// ---------- Enums ----------
export enum Role {
  USER = 'USER',
  ADMIN = 'ADMIN',
}

export enum Provider {
  LOCAL = 'LOCAL',
  GOOGLE = 'GOOGLE',
}

export enum ExperienceLevel {
  BEGINNER = 'BEGINNER',
  INTERMEDIATE = 'INTERMEDIATE',
  ADVANCED = 'ADVANCED',
  EXPERT = 'EXPERT',
}

// ---------- Response Interfaces ----------

export interface AuthState {
  token: string | null;
  user: User | null;
  isAuthenticated: boolean;
}

export interface User {
  token: string;
  email: string;
  firstname: string;
  lastname: string;
  provider: Provider;
  profileIconUrl?: string | null;
  location?: string | null;
  experience?: ExperienceLevel | null;
  socials?: Record<string, string>;
  role?: Role;
}
