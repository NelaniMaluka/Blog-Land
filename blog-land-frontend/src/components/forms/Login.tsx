import * as React from 'react';
import Dialog from '@mui/material/Dialog';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import classNames from 'classnames';
import styles from './Form.module.css';
import GoogleOAuthButton from '../buttons/GoogleButtons/GoogleOAuthButton';
import LoadingScreen from '../../features/LoadingScreen/LoadingScreen';
import { useLogin } from '../../hooks/useAuth';
import { validateEmail, validatePassword } from '../../utils/validationUtils';
import ErrorMessage from '../../features/Snackbars/Snackbar';
import Fade from '@mui/material/Fade';

interface LoginDialogProps {
  open: boolean;
  onClose: () => void;
  onSwitchToRegister: () => void;
}

export default function LoginDialog({ open, onClose, onSwitchToRegister }: LoginDialogProps) {
  const [email, setEmail] = React.useState('');
  const [password, setPassword] = React.useState('');
  const [emailTouched, setEmailTouched] = React.useState(false);
  const [passwordTouched, setPasswordTouched] = React.useState(false);
  const [openRegister, setOpenRegister] = React.useState(false);
  const login = useLogin();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (validateEmail(email) && validatePassword(password)) {
      try {
        await login.mutateAsync({ email, password });
        setEmail('');
        setPassword('');
        onClose();
      } catch (error) {
        console.error(error);
      }
    } else {
      setEmailTouched(true);
      setPasswordTouched(true);
    }
  };

  const emailValid = validateEmail(email);
  const passwordValid = validatePassword(password);

  return (
    <>
      <LoadingScreen isLoading={login.isPending}>
        <Dialog
          open={open}
          onClose={onClose}
          classes={{ paper: styles.dialogPaper }}
          TransitionComponent={Fade}
          transitionDuration={1200}
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
                value={email}
                onChange={(e) => {
                  setEmail(e.target.value);
                  setEmailTouched(true);
                }}
                fullWidth
                variant="outlined"
                size="small"
                InputLabelProps={{ shrink: false }}
                inputProps={{ autoComplete: 'email', style: { color: 'black' } }}
                className={classNames(styles.textField, {
                  [styles.validField]: emailTouched && validateEmail(email),
                  [styles.invalidField]: emailTouched && !validateEmail(email),
                })}
              />
              <p
                className={
                  emailTouched && !validateEmail(email) ? styles.errorText : styles.errorPlaceholder
                }
              >
                {emailTouched && !validateEmail(email)
                  ? 'Please enter a valid email address (e.g. name@example.com).'
                  : 'placeholder'}
              </p>
            </div>

            {/* Password */}
            <div className={styles.inputGroup}>
              <label htmlFor="password" className={styles.label}>
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
                size="small"
                InputLabelProps={{ shrink: false }}
                inputProps={{ autoComplete: 'current-password', style: { color: 'black' } }}
                className={classNames(styles.textField, {
                  [styles.validField]: passwordTouched && validatePassword(password),
                  [styles.invalidField]: passwordTouched && !validatePassword(password),
                })}
              />
              <p
                className={
                  passwordTouched && !validatePassword(password)
                    ? styles.errorText
                    : styles.errorPlaceholder
                }
              >
                {passwordTouched && !validatePassword(password)
                  ? 'Password must be at least 6 characters, include uppercase, lowercase, a number, and a special character, with no spaces.'
                  : 'placeholder'}
              </p>
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
      {login.isError && <ErrorMessage message={login?.error?.message || 'Something went wrong'} />}
    </>
  );
}
