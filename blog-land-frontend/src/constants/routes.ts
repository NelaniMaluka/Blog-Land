import { slugify } from '../utils/formatUtils';

export const ROUTES = {
  // General urls
  HOME: '/',
  ABOUT: '/about',
  PRIVACY_POLICY: '/privacy-policy',
  TERMS_AND_CONDITIONS: '/terms-and-conditions',

  // Authentication urls
  LOGIN: '/login',
  REGISTER: '/register',
  FORGOTPASSWORD: '/forgot-password',

  // Single post urls
  POST: (id: number, title: string) => `/post/${id}-${slugify(title)}`,
  LATEST_POST_PAGE: (title: string) => `/posts/latest/${title}`,

  // Post Groups
  TRENDING_POSTS: '/posts/trending',
  LATEST_POSTS: '/posts/latest',
  VIEW_ALL: '/posts',
  CATEGORY_POSTS: (name: string) => `/category/${name}`,

  // Dashboard views urls
  DASHBOARD_PROFILE: '/dashboard/profile',
  DASHBOARD_POSTS: '/dashboard/posts',
};
