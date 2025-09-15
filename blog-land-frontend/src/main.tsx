// index.tsx or main.tsx
import React, { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import App from './App';
import { Provider } from 'react-redux';
import { DialogProvider } from './features/LoginProvider/LoginProvider';
import { PersistGate } from 'redux-persist/integration/react';
import { BrowserRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { store, persistor } from './store/store';
import { HelmetProvider } from 'react-helmet-async';
import './polyfills';
import { Loader } from '@googlemaps/js-api-loader'; // <-- import here

// Polyfill global for sockjs-client
(window as any).global = window;

const queryClient = new QueryClient();

const container = document.getElementById('root');
if (!container) throw new Error('Root container missing in index.html');

const root = createRoot(container);

// Initialize Google Maps API
const loader = new Loader({
  apiKey: import.meta.env.VITE_GOOGLE_MAPS_API_KEY,
  libraries: ['places'],
});

loader
  .load()
  .then(() => {
    // Only render app once Google Maps API is ready
    root.render(
      <StrictMode>
        <Provider store={store}>
          <PersistGate loading={null} persistor={persistor}>
            <QueryClientProvider client={queryClient}>
              <HelmetProvider>
                <DialogProvider>
                  <BrowserRouter>
                    <App />
                  </BrowserRouter>
                </DialogProvider>
              </HelmetProvider>
            </QueryClientProvider>
          </PersistGate>
        </Provider>
      </StrictMode>
    );
  })
  .catch((err) => {});
