import { Post } from './postType';

export interface Category {
  categoryId: number;
  name: string;
  postCount: number;
}

export interface CategoryPostGroup {
  categoryId: number;
  categoryName: string;
  posts: Post[];
}
