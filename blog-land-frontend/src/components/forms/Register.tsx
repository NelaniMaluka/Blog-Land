import * as React from 'react';
import Dialog from '@mui/material/Dialog';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import classNames from 'classnames';
import styles from './Form.module.css';
import GoogleOAuthButton from '../buttons/GoogleButtons/GoogleOAuthButton';
import LoadingScreen from '../../features/LoadingScreen/LoadingScreen';
import { useRegister } from '../../hooks/useAuth';
import ErrorMessage from '../../features/Snackbars/errorMessage';
import { validateRequired, validateEmail, validatePassword } from '../../utils/validationUtils';
import Fade from '@mui/material/Fade';
import { useEffect, useState, FormEvent } from 'react';
import { oauth } from '../../services/authService';
import { useSetOAuthToken } from '../../hooks/useAuth';
import { useLocation, useNavigate } from 'react-router-dom';

interface RegisterDialogProps {
  open: boolean;
  onClose: () => void;
  onSwitchToLogin: () => void;
}

export default function RegisterDialog({ open, onClose, onSwitchToLogin }: RegisterDialogProps) {
  const [firstname, setFirstname] = useState('');
  const [lastname, setLastname] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isSubmitted, setIsSubmitted] = useState(false);

  const register = useRegister();
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
      } catch (error) {}
    } else {
      setIsSubmitted(true);
    }
  };

  useEffect(() => {
    if (!open) return;

    const handleScroll = () => {
      onClose();
    };

    window.addEventListener('scroll', handleScroll, true);

    return () => {
      window.removeEventListener('scroll', handleScroll, true);
    };
  }, [open, onClose]);

  return (
    <>
      <LoadingScreen isLoading={register.isPending}>
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
              <h5 className={styles.title}>Sign-up</h5>
              <p>
                Already Joined?{' '}
                <span
                  onClick={() => {
                    onClose();
                    onSwitchToLogin();
                  }}
                >
                  Login Now
                </span>
              </p>
            </div>

            {/* First Name */}
            <div className={styles.inputGroup}>
              <label htmlFor="firstName" className={styles.label}>
                First Name
              </label>
              <TextField
                id="firstName"
                value={firstname}
                onChange={(e) => {
                  setFirstname(e.target.value);
                }}
                fullWidth
                variant="outlined"
                size="small"
                InputLabelProps={{ shrink: false }}
                inputProps={{ autoComplete: 'given-name' }}
                className={classNames(styles.textField, {
                  [styles.validField]: isSubmitted && validateRequired(firstname),
                  [styles.invalidField]: isSubmitted && !validateRequired(firstname),
                })}
              />
              <p
                className={
                  isSubmitted && !validateRequired(firstname)
                    ? styles.errorText
                    : styles.errorPlaceholder
                }
              >
                {isSubmitted && !validateRequired(firstname)
                  ? 'First name is required'
                  : 'placeholder'}
              </p>
            </div>

            {/* Last Name */}
            <div className={styles.inputGroup}>
              <label htmlFor="lastName" className={styles.label}>
                Last Name
              </label>
              <TextField
                id="lastName"
                value={lastname}
                onChange={(e) => {
                  setLastname(e.target.value);
                }}
                fullWidth
                variant="outlined"
                size="small"
                InputLabelProps={{ shrink: false }}
                inputProps={{ autoComplete: 'family-name' }}
                className={classNames(styles.textField, {
                  [styles.validField]: isSubmitted && validateRequired(lastname),
                  [styles.invalidField]: isSubmitted && !validateRequired(lastname),
                })}
              />
              <p
                className={
                  isSubmitted && !validateRequired(lastname)
                    ? styles.errorText
                    : styles.errorPlaceholder
                }
              >
                {isSubmitted && !validateRequired(lastname)
                  ? 'Last name is required'
                  : 'placeholder'}
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
                onChange={(e) => {
                  setEmail(e.target.value);
                }}
                fullWidth
                variant="outlined"
                size="small"
                InputLabelProps={{ shrink: false }}
                inputProps={{ autoComplete: 'email', style: { color: 'black' } }}
                className={classNames(styles.textField, {
                  [styles.validField]: isSubmitted && validateEmail(email),
                  [styles.invalidField]: isSubmitted && !validateEmail(email),
                })}
              />
              <p
                className={
                  isSubmitted && !validateEmail(email) ? styles.errorText : styles.errorPlaceholder
                }
              >
                {isSubmitted && !validateEmail(email)
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
                }}
                fullWidth
                variant="outlined"
                size="small"
                InputLabelProps={{ shrink: false }}
                inputProps={{ autoComplete: 'current-password', style: { color: 'black' } }}
                className={classNames(styles.textField, {
                  [styles.validField]: isSubmitted && validatePassword(password),
                  [styles.invalidField]: isSubmitted && !validatePassword(password),
                })}
              />
              <p
                className={
                  isSubmitted && !validatePassword(password)
                    ? styles.errorText
                    : styles.errorPlaceholder
                }
              >
                {isSubmitted && !validatePassword(password)
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
                margin: '10px auto 0',
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

            <GoogleOAuthButton onClick={() => oauth()} />
          </form>
        </Dialog>
      </LoadingScreen>
      {register.isError && (
        <ErrorMessage message={register?.error?.message || 'Something went wrong'} />
      )}
    </>
  );
}
