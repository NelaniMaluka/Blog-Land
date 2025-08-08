import { User } from './userType';

export enum Order {
  LATEST = 'LATEST',
  OLDEST = 'OLDEST',
}

export interface Post {
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
  score: number;
  references?: string | null;
  author: string;
  source?: string | null;
  user: User;
  commentCount: number;
  comments?: Comment[];
  isDraft: boolean;
}

export interface PostWithCategory {
  categoryId: number;
  categoryName: string;
  posts: Post;
}
