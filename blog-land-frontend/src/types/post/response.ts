import { CommentResponse } from '../comment/response';
import { UserResponse } from '../user/response';

export enum Order {
  LATEST = 'latest',
  OLDEST = 'oldest',
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
  postImgUrl: string;
  views: number;
  score: number;
  references?: string | null;
  author: string;
  source?: string | null;
  user: UserResponse;
  commentCount: number;
  comments?: CommentResponse[];
  isDraft: boolean;
}

export interface PostWithCategoryResponse {
  categoryId: number;
  categoryName: string;
  posts: PostResponse;
}
