export interface CommentResponse {
  id: number;
  postId: number;
  content: string;
  author: string;
  profileImgUrl?: string | null;
  createdAt: string;
}
