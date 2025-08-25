import * as React from 'react';
import Dialog from '@mui/material/Dialog';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import classNames from 'classnames';
import styles from './Form.module.css';
import GoogleOAuthButton from '../buttons/GoogleButtons/GoogleOAuthButton';
import LoadingScreen from '../../features/LoadingScreen/LoadingScreen';
import { useLogin, useSetOAuthToken } from '../../hooks/useAuth';
import { validateEmail, validatePassword } from '../../utils/validationUtils';
import ErrorMessage from '../../features/Snackbars/errorMessage';
import Fade from '@mui/material/Fade';
import { useEffect, useState, FormEvent } from 'react';
import { oauth } from '../../services/authService';
import { useLocation, useNavigate } from 'react-router-dom';

interface LoginDialogProps {
  open: boolean;
  onClose: () => void;
  onSwitchToRegister: () => void;
  onSwitchToForgot: () => void; // 👈 add this
}

export default function LoginDialog({
  open,
  onClose,
  onSwitchToRegister,
  onSwitchToForgot,
}: LoginDialogProps) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isSubmitted, setIsSubmitted] = useState(false);

  const login = useLogin();
  const setOAuthToken = useSetOAuthToken();
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const token = params.get('token');
    if (token) {
      setOAuthToken.mutate(token, { onSuccess: () => navigate('/') });
    }
  }, [location.search, navigate, setOAuthToken]);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (validateEmail(email) && validatePassword(password)) {
      try {
        await login.mutateAsync({ email, password });
        setEmail('');
        setPassword('');
        onClose();
      } catch (error) {}
    } else {
      setIsSubmitted(true);
    }
  };

  return (
    <LoadingScreen isLoading={login.isPending}>
      <Dialog
        open={open}
        onClose={onClose}
        classes={{ paper: styles.dialogPaper }}
        TransitionComponent={Fade}
        transitionDuration={600}
        disableScrollLock={true}
      >
        <form onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.header}>
            <h5 className={styles.title}>Login</h5>
            <p>
              Haven't Joined Yet?{' '}
              <span
                onClick={() => {
                  onClose();
                  onSwitchToRegister();
                }}
              >
                Sign-Up
              </span>
            </p>
          </div>

          {/* Email */}
          <div className={styles.inputGroup}>
            <label htmlFor="email" className={styles.label}>
              Email
            </label>
            <TextField
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              fullWidth
              size="small"
              inputProps={{ autoComplete: 'email', style: { color: 'black' } }}
              className={classNames(styles.textField, {
                [styles.validField]: isSubmitted && validateEmail(email),
                [styles.invalidField]: isSubmitted && !validateEmail(email),
              })}
            />
          </div>

          {/* Password */}
          <div className={styles.inputGroup}>
            <label htmlFor="password" className={styles.label2}>
              Password
              <p className={styles.forgotPassword}>
                <span
                  onClick={() => {
                    onClose();
                    onSwitchToForgot(); // 👈 trigger forgot password
                  }}
                >
                  Forgot password?
                </span>
              </p>
            </label>
            <TextField
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              fullWidth
              size="small"
              inputProps={{ autoComplete: 'current-password', style: { color: 'black' } }}
              className={classNames(styles.textField, {
                [styles.validField]: isSubmitted && validatePassword(password),
                [styles.invalidField]: isSubmitted && !validatePassword(password),
              })}
            />
          </div>

          <Button
            type="submit"
            variant="contained"
            fullWidth
            sx={{
              mt: 4,
              borderRadius: '10px',
              fontSize: '0.45rem',
              height: '33px',
              width: '100%',
              margin: '10px auto 0',
              display: 'block',
            }}
          >
            Log In
          </Button>

          <div className={styles.dividerContainer}>
            <hr className={styles.divider} />
            <span className={styles.orText}>or</span>
            <hr className={styles.divider} />
          </div>

          <GoogleOAuthButton onClick={() => oauth()} />
        </form>
      </Dialog>

      {login.isError && <ErrorMessage message={login?.error?.message || 'Something went wrong'} />}
    </LoadingScreen>
  );
}
