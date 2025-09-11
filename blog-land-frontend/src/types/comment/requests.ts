export type AddCommentRequest = {
  postId: number;
  content: string;
};

export type UpdateCommentRequest = {
  id: number;
  content: string;
  postId: number;
};
