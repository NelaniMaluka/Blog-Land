import * as React from 'react';
import Dialog from '@mui/material/Dialog';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import classNames from 'classnames';
import styles from './Form.module.css';
import GoogleOAuthButton from '../button/GoogleOAuthButton';
import LoadingScreen from '../../features/LoadingScreen/LoadingScreen';
import { useLogin } from '../../hooks/useAuth';
import { validateEmail, validatePassword } from '../../utils/validation';
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
            <h2 className={styles.title}>Log-In</h2>

            <div className={styles.inputGroup}>
              <label htmlFor="email" className={classNames(styles.label, {})}>
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
                FormHelperTextProps={{
                  style: { marginTop: '0px', lineHeight: '0.35rem' },
                }}
                helperText={
                  emailTouched &&
                  !validateEmail(email) && (
                    <span className={styles.errorText}>
                      Please enter a valid email address (e.g. name@example.com).
                    </span>
                  )
                }
                className={classNames(styles.textField, {
                  [styles.validField]: emailTouched && emailValid,
                  [styles.invalidField]: emailTouched && !emailValid,
                })}
              />
            </div>

            <div className={styles.inputGroup}>
              <label htmlFor="password" className={classNames(styles.label, {})}>
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
                FormHelperTextProps={{
                  style: { marginTop: '0px', lineHeight: '0.35rem' },
                }}
                helperText={
                  passwordTouched &&
                  !validatePassword(password) && (
                    <span className={styles.errorText}>
                      Password must be at least 6 characters, include uppercase, lowercase, a
                      number, and a special character, with no spaces.
                    </span>
                  )
                }
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
                fontSize: '0.45rem',
                height: '33px',
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
            <Button
              onClick={() => {
                onClose();
                onSwitchToRegister();
              }}
              variant="contained"
              fullWidth
              sx={{
                mt: 4,
                borderRadius: '10px',
                fontSize: '0.45rem',
                height: '33px',
                width: '90%',
                margin: '20px auto 0',
                display: 'block',
              }}
            >
              Register
            </Button>
          </form>
        </Dialog>
      </LoadingScreen>
      {login.isError && <ErrorMessage message={login?.error?.message || 'Something went wrong'} />}
    </>
  );
}
