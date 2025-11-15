export type AddPostRequest = {
  title: string;
  content: string;
  categoryId: number;
  summary: string;
  imgUrl: string;
  draft: boolean;
  scheduledAt?: string | null;
};

export type UpdatePostRequest = {
  id: string;
  title: string;
  content: string;
  categoryId: string;
  summary: string;
  imgUrl: string;
  draft: boolean;
  scheduledAt?: string | null;
};
