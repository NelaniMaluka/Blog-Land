import React, { createContext, useContext, useState } from 'react';
import Snackbar from '@mui/material/Snackbar';
import Slide from '@mui/material/Slide';
import MuiAlert from '@mui/material/Alert';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';

interface SnackbarContextValue {
  showError: (msg: string) => void;
}

const SnackbarContext = createContext<SnackbarContextValue | undefined>(undefined);

export const SnackbarProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [message, setMessage] = useState<string | null>(null);

  const showError = (msg: string) => {
    setMessage(msg);
    setTimeout(() => setMessage(null), 2500);
  };

  return (
    <SnackbarContext.Provider value={{ showError }}>
      {children}

      <Snackbar
        open={!!message}
        autoHideDuration={2500}
        onClose={() => setMessage(null)}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'left' }}
      >
        <MuiAlert
          elevation={6}
          variant="filled"
          severity="error"
          icon={<ErrorOutlineIcon sx={{ fontSize: '1rem', color: '#fff' }} />}
          sx={{
            backgroundColor: '#d32f2f',
            color: '#fff',
            fontSize: '0.57rem',
            display: 'flex',
            alignItems: 'center',
            gap: '3px',
            height: '36px',
          }}
        >
          {message}
        </MuiAlert>
      </Snackbar>
    </SnackbarContext.Provider>
  );
};

export const useSnackbar = () => {
  const ctx = useContext(SnackbarContext);
  if (!ctx) throw new Error('useSnackbar must be used within SnackbarProvider');
  return ctx;
};
