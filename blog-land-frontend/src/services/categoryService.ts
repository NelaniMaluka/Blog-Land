import { getAllCategories } from '../api/categoryApi';
import { CategoryResponse } from '../types/category/response';
import { getAxiosErrorMessage } from '../utils/errorUtils';

export const fetchCategories = async (): Promise<CategoryResponse[]> => {
  try {
    const categories = await getAllCategories();
    return categories.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to fetch categories'));
  }
};
