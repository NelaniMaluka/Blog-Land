import { PostResponse } from '../post/response';

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
