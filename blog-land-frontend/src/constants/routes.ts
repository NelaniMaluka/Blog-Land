export const ROUTES = {
  HOME: '/',
  RANDOM_POSTS: '/posts/random',
  TRENDING_POSTS: '/posts/trending',
  LATEST_POSTS: '/posts/latest',
  VIEW_ALL: '/posts',
  CATEGORY_POSTS: (name: string) => `/category/${name}`,
  POST: (id: number) => `/post/${id}`,
  LATEST_POST_Page: (title: string) => `/latest/post/${title}`,
  ABOUT: '/about',
  PRIVACY_POLICY: '/privacy-policy',
  TERMS_AND_CONDITIONS: '/terms-and-conditions',
};
