import * as React from 'react';
import Dialog from '@mui/material/Dialog';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import classNames from 'classnames';
import styles from './Form.module.css';
import GoogleOAuthButton from '../button/GoogleOAuthButton';
import LoadingScreen from '../../../features/LoadingScreen/LoadingScreen';
import { useLogin } from '../../../hooks/useAuth';

interface LoginDialogProps {
  open: boolean;
  onClose: () => void;
}

export default function LoginDialog({ open, onClose }: LoginDialogProps) {
  const [email, setEmail] = React.useState('');
  const [password, setPassword] = React.useState('');
  const [emailTouched, setEmailTouched] = React.useState(false);
  const [passwordTouched, setPasswordTouched] = React.useState(false);
  const login = useLogin();

  const validateEmail = (value: string) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);
  const validatePassword = (value: string) => value.length >= 6;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (validateEmail(email) && validatePassword(password)) {
      try {
        await login.mutateAsync({ email, password });
        console.log('success');
        onClose();
      } catch (error) {
        console.error(error);
      }
    } else {
      console.log('Invalid input');
    }
  };

  const emailValid = validateEmail(email);
  const passwordValid = validatePassword(password);

  return (
    <LoadingScreen isLoading={login.isPending}>
      <Dialog open={open} onClose={onClose} classes={{ paper: styles.dialogPaper }}>
        <form onSubmit={handleSubmit} className={styles.form}>
          <h2 className={styles.title}>Log-In</h2>

          <div className={styles.inputGroup}>
            <label
              htmlFor="email"
              className={classNames(styles.label, {
                [styles.valid]: emailTouched && emailValid,
                [styles.invalid]: emailTouched && !emailValid,
              })}
            >
              Email
            </label>
            <TextField
              id="email"
              value={email}
              onChange={(e) => {
                setEmail(e.target.value);
                setEmailTouched(true);
              }}
              fullWidth
              variant="outlined"
              InputLabelProps={{ shrink: false }}
              className={classNames(styles.textField, {
                [styles.validField]: emailTouched && emailValid,
                [styles.invalidField]: emailTouched && !emailValid,
              })}
            />
          </div>

          <div className={styles.inputGroup}>
            <label
              htmlFor="password"
              className={classNames(styles.label, {
                [styles.valid]: passwordTouched && passwordValid,
                [styles.invalid]: passwordTouched && !passwordValid,
              })}
            >
              Password
            </label>
            <TextField
              id="password"
              type="password"
              value={password}
              onChange={(e) => {
                setPassword(e.target.value);
                setPasswordTouched(true);
              }}
              fullWidth
              variant="outlined"
              InputLabelProps={{ shrink: false }}
              className={classNames(styles.textField, {
                [styles.validField]: passwordTouched && passwordValid,
                [styles.invalidField]: passwordTouched && !passwordValid,
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
              fontSize: '0.55rem',
              height: '35px',
              width: '90%',
              margin: '20px auto 0',
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

          <GoogleOAuthButton
            redirectUrl="http://localhost:8080/oauth2/authorization/google"
            onClick={() => console.log('Google button clicked')}
          />
        </form>
      </Dialog>
    </LoadingScreen>
  );
}
