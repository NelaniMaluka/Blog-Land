// src/types/api.ts

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

export interface UserResponse {
  email: string;
  firstname: string;
  lastname: string;
  provider: Provider;
  profileIconUrl?: string | null;
  location?: string | null;
  experience?: ExperienceLevel | null;
  socials?: Record<string, string>;
  role?: Role; // optional, in case your DTO later includes it
}

export interface CommentResponse {
  id: number;
  postId: number;
  content: string;
  author: string;
  profileImgUrl?: string | null;
  createdAt: string; // LocalDateTime → string
}

export interface PostResponse {
  id: number;
  title: string;
  content: string;
  readTime: number;
  createdAt: string;
  updatedAt: string;
  categoryId: number;
  summary?: string | null;
  postImgUrl?: string | null;
  views: number;
  references?: string | null;
  author: string;
  source?: string | null;
  score: number;
  user: UserResponse;
  commentCount: number;
  comments?: CommentResponse[];
  isDraft: boolean;
}

export interface CategoryResponse {
  categoryId: number;
  name: string;
  postCount: number;
}

export interface CategoryPostGroupResponse {
  categoryId: number;
  categoryName: string;
  posts: PostResponse[];
}
