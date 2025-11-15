import he from 'he';
import { getAllCategories, getPostsByCategory } from '../api/categoryApi';
import { CategoryResponse } from '../types/category/response';
import { Order, PaginatedPosts, PostResponse } from '../types/post/response';
import { getAxiosErrorMessage, validateOrThrow } from '../utils/errorUtils';
import { stripHtml, formatDate } from '../utils/formatUtils';
import { paginationSchemaWithOrder } from '../schemas/paginationSchema';
import { idSchema } from '../schemas/generalSchema';

export const fetchCategories = async (): Promise<CategoryResponse[]> => {
  try {
    const categories = await getAllCategories();
    return categories.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to fetch categories'));
  }
};

export const fetchPostByCategory = async (payload: {
  categoryId: string;
  page: {
    page: number;
    size: number;
    order: Order;
  };
}): Promise<PaginatedPosts> => {
  const validPayload = validateOrThrow(paginationSchemaWithOrder, payload.page);
  const validId = validateOrThrow(idSchema, payload.categoryId);

  try {
    const response = await getPostsByCategory(validId, validPayload);
    const data = response?.data;

    return {
      ...data,
      content: (data?.content ?? []).map((raw: PostResponse) => ({
        ...raw,
        title: he.decode(stripHtml(raw.title)),
        summary: raw.summary ? he.decode(stripHtml(raw.summary)) : null,
        createdAt: formatDate(raw.createdAt),
      })),
    };
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get category posts'));
  }
};
