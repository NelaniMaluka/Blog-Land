export type AddCommentRequest = {
  postId: number;
  content: string;
};

export type UpdateCommentRequest = {
  id: number;
  postId: number;
  content: string;
};
