import * as React from 'react';
import Dialog from '@mui/material/Dialog';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import classNames from 'classnames';
import styles from './Form.module.css';
import GoogleOAuthButton from '../button/GoogleOAuthButton';
import LoadingScreen from '../../../features/LoadingScreen/LoadingScreen';
import { useRegister } from '../../../hooks/useAuth';

interface RegisterDialogProps {
  open: boolean;
  onClose: () => void;
}

export default function RegisterDialog({ open, onClose }: RegisterDialogProps) {
  const [firstname, setFirstname] = React.useState('');
  const [lastname, setLastname] = React.useState('');
  const [email, setEmail] = React.useState('');
  const [password, setPassword] = React.useState('');

  const [firstNameTouched, setFirstNameTouched] = React.useState(false);
  const [lastNameTouched, setLastNameTouched] = React.useState(false);
  const [emailTouched, setEmailTouched] = React.useState(false);
  const [passwordTouched, setPasswordTouched] = React.useState(false);

  const register = useRegister();

  const validateRequired = (value: string) => value.trim().length > 0;
  const validateEmail = (value: string) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);
  const validatePassword = (value: string) => value.length >= 6;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const firstNameValid = validateRequired(firstname);
    const lastNameValid = validateRequired(lastname);
    const emailValid = validateEmail(email);
    const passwordValid = validatePassword(password);

    if (firstNameValid && lastNameValid && emailValid && passwordValid) {
      console.log('Register data:', { firstname, lastname, email, password });
      try {
        await register.mutateAsync({ firstname, lastname, email, password });
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
    <LoadingScreen isLoading={register.isPending}>
      <Dialog open={open} onClose={onClose} classes={{ paper: styles.dialogPaper }}>
        <form onSubmit={handleSubmit} className={styles.form}>
          <h2 className={styles.title}>Register</h2>

          <div className={styles.inputGroup}>
            <label
              htmlFor="firstName"
              className={classNames(styles.label, {
                [styles.valid]: firstNameTouched && validateRequired(firstname),
                [styles.invalid]: firstNameTouched && !validateRequired(firstname),
              })}
            >
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
              className={classNames(styles.textField, {
                [styles.validField]: firstNameTouched && validateRequired(firstname),
                [styles.invalidField]: firstNameTouched && !validateRequired(firstname),
              })}
            />
          </div>

          <div className={styles.inputGroup}>
            <label
              htmlFor="lastName"
              className={classNames(styles.label, {
                [styles.valid]: lastNameTouched && validateRequired(lastname),
                [styles.invalid]: lastNameTouched && !validateRequired(lastname),
              })}
            >
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
              className={classNames(styles.textField, {
                [styles.validField]: lastNameTouched && validateRequired(lastname),
                [styles.invalidField]: lastNameTouched && !validateRequired(lastname),
              })}
            />
          </div>

          <div className={styles.inputGroup}>
            <label
              htmlFor="email"
              className={classNames(styles.label, {
                [styles.valid]: emailTouched && validateEmail(email),
                [styles.invalid]: emailTouched && !validateEmail(email),
              })}
            >
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
              className={classNames(styles.textField, {
                [styles.validField]: emailTouched && validateEmail(email),
                [styles.invalidField]: emailTouched && !validateEmail(email),
              })}
            />
          </div>

          <div className={styles.inputGroup}>
            <label
              htmlFor="password"
              className={classNames(styles.label, {
                [styles.valid]: passwordTouched && validatePassword(password),
                [styles.invalid]: passwordTouched && !validatePassword(password),
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
              size="small"
              InputLabelProps={{ shrink: false }}
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
              fontSize: '0.55rem',
              height: '35px',
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
        </form>
      </Dialog>
    </LoadingScreen>
  );
}
