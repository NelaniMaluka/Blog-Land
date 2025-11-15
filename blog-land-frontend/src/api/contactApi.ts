import apiClient from './apiClient';
import { AddContactRequest } from '../types/contact/requests';

export const sendContactMessage = async (payload: AddContactRequest) => {
  const response = await apiClient.post('/public/contact', {
    payload,
  });
  return response;
};
