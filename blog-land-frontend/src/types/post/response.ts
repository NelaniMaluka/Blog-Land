import { CommentResponse } from '../comment/response';

export enum Order {
  LATEST = 'latest',
  OLDEST = 'oldest',
}

export interface PostResponse {
  id: string;
  title: string;
  content: string;
  readTime: number;
  createdAt: string;
  updatedAt: string;
  categoryId: string;
  summary?: string | null;
  postImgUrl: string;
  views: number;
  score: number;
  references?: string | null;
  author: string;
  source?: string | null;
  userId: string;
  commentCount: number;
  comments?: CommentResponse[];
  isDraft: boolean;
}

export interface PostWithCategoryResponse {
  categoryId: string;
  categoryName: string;
  posts: PostResponse;
}

export interface PaginatedPosts {
  content: PostResponse[];
  totalPages: number;
  totalElements: number;
  number: number;
}
