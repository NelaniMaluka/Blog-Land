import { PostResponse } from '../post/response';

export interface CategoryResponse {
  id: string;
  name: string;
  postCount: number;
}

export interface CategoryPostGroupResponse {
  id: string;
  categoryName: string;
  posts: PostResponse[];
}
