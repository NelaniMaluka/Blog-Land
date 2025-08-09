import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import App from './App.jsx';
import { Provider } from 'react-redux';
import { BrowserRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

const queryClient = new QueryClient();

createRoot(document.getElementById('root')).render(
  <StrictMode>
    {/**<Provider> */}
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        {' '}
        <App />
      </BrowserRouter>
    </QueryClientProvider>
    {/**</Provider> */}
  </StrictMode>
);
