// index.tsx or main.tsx
import React, { StrictMode, useEffect } from 'react';
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

const queryClient = new QueryClient();

function loadGoogleMapsApi() {
  return new Promise<void>((resolve, reject) => {
    const existingScript = document.getElementById('google-maps');
    if (!existingScript) {
      const script = document.createElement('script');
      script.src = `https://maps.googleapis.com/maps/api/js?key=${
        import.meta.env.VITE_GOOGLE_MAPS_API_KEY
      }&libraries=places&loading=async`;
      script.id = 'google-maps';
      script.async = true;
      script.defer = true;
      script.onload = () => resolve();
      script.onerror = () => reject('Failed to load Google Maps script');
      document.head.appendChild(script);
    } else {
      resolve();
    }
  });
}

const container = document.getElementById('root');
if (!container) throw new Error('Root container missing in index.html');

const root = createRoot(container);

loadGoogleMapsApi()
  .then(() => {
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
  .catch((err) => {
    console.error(err);
  });
