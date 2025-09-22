import { UserResponse } from '../user/response';

export interface CommentResponse {
  id: number;
  content: string;
  userId: string;
  createdAt: string;
}

export interface UserCommentsResponse {
  id: number;
}
