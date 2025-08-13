import { PostResponse } from '../post/response';

export interface CategoryResponse {
  id: number;
  name: string;
  postCount: number;
}

export interface CategoryPostGroupResponse {
  id: number;
  categoryName: string;
  posts: PostResponse[];
}
