import * as React from 'react';
import Dialog from '@mui/material/Dialog';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import classNames from 'classnames';
import styles from './Form.module.css';
import GoogleOAuthButton from '../button/GoogleOAuthButton';
import LoadingScreen from '../../features/LoadingScreen/LoadingScreen';
import { useRegister } from '../../hooks/useAuth';
import ErrorMessage from '../../features/Snackbars/Snackbar';
import { validateRequired, validateEmail, validatePassword } from '../../utils/validation';
import Fade from '@mui/material/Fade';

interface RegisterDialogProps {
  open: boolean;
  onClose: () => void;
  onSwitchToLogin: () => void;
}

export default function RegisterDialog({ open, onClose, onSwitchToLogin }: RegisterDialogProps) {
  const [firstname, setFirstname] = React.useState('');
  const [lastname, setLastname] = React.useState('');
  const [email, setEmail] = React.useState('');
  const [password, setPassword] = React.useState('');

  const [firstNameTouched, setFirstNameTouched] = React.useState(false);
  const [lastNameTouched, setLastNameTouched] = React.useState(false);
  const [emailTouched, setEmailTouched] = React.useState(false);
  const [passwordTouched, setPasswordTouched] = React.useState(false);

  const [openLogin, setOpenLogin] = React.useState(false);

  const register = useRegister();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const firstNameValid = validateRequired(firstname);
    const lastNameValid = validateRequired(lastname);
    const emailValid = validateEmail(email);
    const passwordValid = validatePassword(password);

    if (firstNameValid && lastNameValid && emailValid && passwordValid) {
      try {
        await register.mutateAsync({ firstname, lastname, email, password });
        setFirstname('');
        setLastname('');
        setEmail('');
        setPassword('');
        onClose();
      } catch (error) {
        console.log(error);
      }
    } else {
      setFirstNameTouched(true);
      setLastNameTouched(true);
      setEmailTouched(true);
      setPasswordTouched(true);
    }
  };

  return (
    <>
      <LoadingScreen isLoading={register.isPending}>
        <Dialog
          open={open}
          onClose={onClose}
          classes={{ paper: styles.dialogPaper }}
          TransitionComponent={Fade}
          transitionDuration={1200}
        >
          <form onSubmit={handleSubmit} className={styles.form}>
            <h2 className={styles.title}>Register</h2>

            <div className={styles.inputGroup}>
              <label htmlFor="firstName" className={classNames(styles.label, {})}>
                First Name
              </label>
              <TextField
                id="firstName"
                value={firstname}
                onChange={(e) => {
                  setFirstname(e.target.value);
                  setFirstNameTouched(true);
                }}
                fullWidth
                variant="outlined"
                size="small"
                InputLabelProps={{ shrink: false }}
                FormHelperTextProps={{
                  style: { marginTop: '0px', lineHeight: '0.35rem' },
                }}
                helperText={
                  firstNameTouched &&
                  !validateRequired(firstname) && (
                    <span className={styles.errorText}>First name is required</span>
                  )
                }
                className={classNames(styles.textField, {
                  [styles.validField]: firstNameTouched && validateRequired(firstname),
                  [styles.invalidField]: firstNameTouched && !validateRequired(firstname),
                })}
              />
            </div>

            <div className={styles.inputGroup}>
              <label htmlFor="lastName" className={classNames(styles.label, {})}>
                Last Name
              </label>
              <TextField
                id="lastName"
                value={lastname}
                onChange={(e) => {
                  setLastname(e.target.value);
                  setLastNameTouched(true);
                }}
                fullWidth
                variant="outlined"
                size="small"
                InputLabelProps={{ shrink: false }}
                FormHelperTextProps={{
                  style: { marginTop: '0px', lineHeight: '0.35rem' },
                }}
                helperText={
                  lastNameTouched &&
                  !validateRequired(lastname) && (
                    <span className={styles.errorText}>Last name is required</span>
                  )
                }
                className={classNames(styles.textField, {
                  [styles.validField]: lastNameTouched && validateRequired(lastname),
                  [styles.invalidField]: lastNameTouched && !validateRequired(lastname),
                })}
              />
            </div>

            <div className={styles.inputGroup}>
              <label htmlFor="email" className={classNames(styles.label, {})}>
                Email
              </label>
              <TextField
                id="email"
                type="email"
                value={email}
                onChange={(e) => {
                  setEmail(e.target.value);
                  setEmailTouched(true);
                }}
                fullWidth
                variant="outlined"
                size="small"
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
                  [styles.validField]: emailTouched && validateEmail(email),
                  [styles.invalidField]: emailTouched && !validateEmail(email),
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
                size="small"
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
                  [styles.validField]: passwordTouched && validatePassword(password),
                  [styles.invalidField]: passwordTouched && !validatePassword(password),
                })}
                inputProps={{ style: { color: 'black' } }}
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
              Create Account
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
                onSwitchToLogin();
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
                color: '',
              }}
            >
              Login
            </Button>
          </form>
        </Dialog>
      </LoadingScreen>
      {register.isError && (
        <ErrorMessage message={register?.error?.message || 'Something went wrong'} />
      )}
    </>
  );
}
