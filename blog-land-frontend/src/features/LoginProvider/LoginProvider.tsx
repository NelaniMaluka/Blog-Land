// src/context/DialogContext.tsx
import { createContext, useState, ReactNode, useContext } from 'react';

interface DialogContextType {
  openLogin: boolean;
  openRegister: boolean;
  openForgot: boolean;
  showLogin: () => void;
  showRegister: () => void;
  showForgot: () => void;
  hideAll: () => void;
}

const DialogContext = createContext<DialogContextType | undefined>(undefined);

export const DialogProvider = ({ children }: { children: ReactNode }) => {
  const [openLogin, setOpenLogin] = useState(false);
  const [openRegister, setOpenRegister] = useState(false);
  const [openForgot, setOpenForgot] = useState(false);

  const showLogin = () => setOpenLogin(true);
  const showRegister = () => setOpenRegister(true);
  const showForgot = () => setOpenForgot(true);

  const hideAll = () => {
    setOpenLogin(false);
    setOpenRegister(false);
    setOpenForgot(false);
  };

  return (
    <DialogContext.Provider
      value={{ openLogin, openRegister, openForgot, showLogin, showRegister, showForgot, hideAll }}
    >
      {children}
    </DialogContext.Provider>
  );
};

export const useDialog = () => {
  const context = useContext(DialogContext);
  if (!context) throw new Error('useDialog must be used within DialogProvider');
  return context;
};
