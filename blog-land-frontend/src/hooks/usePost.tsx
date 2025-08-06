// hooks/usePost.tsx
import { useState, useCallback } from 'react';
import { searchPosts } from '../services/postService';
import axios, { AxiosResponse } from 'axios';

export const useSearch = <T,>() => {
  const [searchData, setSearchData] = useState<T[] | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const getSearch = useCallback(async (keyword: string) => {
    setLoading(true);
    setError(null);

    try {
      const response: AxiosResponse<T[]> = await searchPosts(keyword);
      setSearchData(response.data);
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

  return { searchData, loading, error, setError, getSearch };
};
