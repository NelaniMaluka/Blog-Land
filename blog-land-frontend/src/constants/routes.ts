export const ROUTES = {
  HOME: '/',
  RANDOM_POSTS: '/posts/random',
  TRENDING_POSTS: '/posts/trending',
  LATEST_POSTS: '/posts/latest',
  VIEW_ALL: '/posts',
  CATEGORY_POSTS: (name: string) => `/post/category/${name}`,
  POST: (id: number) => `/post/${id}`,
};
