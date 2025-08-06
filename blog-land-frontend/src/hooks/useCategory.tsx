// hooks/usePost.tsx
import { useState, useCallback } from 'react';
import { getAllCategories } from '../services/categoryService';
import axios, { AxiosResponse } from 'axios';

export const useCategories = <T,>() => {
  const [categoryData, setCategoryData] = useState<T[] | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const getCategories = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const response: AxiosResponse<T[]> = await getAllCategories();
      setCategoryData(response.data);
    } catch (err: unknown) {
      if (axios.isAxiosError(err)) {
        if (err.response) {
          if (err.response.status >= 500) {
            setError('Internal Server Error');
          } else {
            setError(`Error ${err.response.status}: ${err.message}`);
          }
        } else if (err.request) {
          setError('No response from server.');
        } else {
          setError(`Request error: ${err.message}`);
        }
      } else {
        setError('Unexpected error occurred.');
      }
    } finally {
      setLoading(false);
    }
  }, []);

  return { categoryData, loading, error, setError, getCategories };
};
