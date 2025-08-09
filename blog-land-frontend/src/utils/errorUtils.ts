import { AxiosError } from 'axios';
import { ZodType } from 'zod';

export function getAxiosErrorMessage(error: unknown, fallback: string): string {
  const axiosError = error as AxiosError<{ message?: string }>;
  return axiosError.response?.data?.message || fallback;
}

export const validateOrThrow = <T>(schema: ZodType<T>, data: unknown): T => {
  const result = schema.safeParse(data);

  if (!result.success) {
    const firstError = result.error.issues[0]?.message ?? 'Invalid input';
    throw new Error(firstError);
  }

  return result.data;
};
