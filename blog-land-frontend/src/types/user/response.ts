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
  NEW_BLOGGER = 'NEW_BLOGGER',
  CASUAL_POSTER = 'CASUAL_POSTER',
  COMMUNITY_WRITER = 'COMMUNITY_WRITER',
  FREQUENT_CONTRIBUTOR = 'FREQUENT_CONTRIBUTOR',
  PRO_BLOGGER = 'PRO_BLOGGER',
}

export const ExperienceLabels: Record<ExperienceLevel, string> = {
  [ExperienceLevel.NEW_BLOGGER]: 'New Blogger',
  [ExperienceLevel.CASUAL_POSTER]: 'Casual Poster',
  [ExperienceLevel.COMMUNITY_WRITER]: 'Community Writer',
  [ExperienceLevel.FREQUENT_CONTRIBUTOR]: 'Frequent Contributor',
  [ExperienceLevel.PRO_BLOGGER]: 'Pro Blogger',
};

// ---------- Response Interfaces ----------

export interface AuthState {
  token: string | null;
  user: UserResponse | null;
  isAuthenticated: boolean;
}

export interface LoginResponse {
  token: string;
  expiresIn: number;
  user: UserResponse;
}

export interface UserResponse {
  naniId: string;
  email?: string;
  firstname: string;
  lastname: string;
  provider?: Provider;
  title?: string;
  summary?: string;
  joinedAt?: string;
  profileIconUrl?: string | null;
  location?: string | null;
  experience?: ExperienceLevel | null;
  role?: Role;
  socials: {
    twitter?: string;
    github?: string;
    linkedin?: string;
    facebook?: string;
    instagram?: string;
  };
}
