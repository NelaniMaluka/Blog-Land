import { UserResponse } from '../user/response';

export interface CommentResponse {
  id: number;
  content: string;
  user: UserResponse;
  createdAt: string;
}

export interface UserCommentsResponse {
  id: number;
}
