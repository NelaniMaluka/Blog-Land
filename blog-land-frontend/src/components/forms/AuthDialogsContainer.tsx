import LoginDialog from './Login';
import RegisterDialog from './Register';
import { useState } from 'react';

function AuthDialogsContainer() {
  const [openLogin, setOpenLogin] = useState(false);
  const [openRegister, setOpenRegister] = useState(false);
  const [openForgot, setOpenForgot] = useState(false);

  return (
    <>
      <LoginDialog
        open={openLogin}
        onClose={() => setOpenLogin(false)}
        onSwitchToRegister={() => {
          setOpenLogin(false);
          setOpenRegister(true);
        }}
        onSwitchToForgot={() => {
          setOpenLogin(false);
          setOpenForgot(true);
        }}
      />
      <RegisterDialog
        open={openRegister}
        onClose={() => setOpenRegister(false)}
        onSwitchToLogin={() => {
          setOpenRegister(false);
          setOpenLogin(true);
        }}
      />
    </>
  );
}
