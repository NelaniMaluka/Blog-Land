import { getAllCategories } from '../api/categoryApi';
import { Category } from '../types/categoryType';
import { getAxiosErrorMessage } from '../utils/errorUtils';

export const fetchCategories = async (): Promise<Category[]> => {
  try {
    const categories = await getAllCategories();
    return categories.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to fetch categories'));
  }
};
