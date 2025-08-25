import * as React from 'react';
import Dialog from '@mui/material/Dialog';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import classNames from 'classnames';
import styles from './Form.module.css';
import { validateEmail } from '../../utils/validationUtils';
import { useState, FormEvent } from 'react';
import { useForgotPassword } from '../../hooks/usePassword';
import LoadingScreen from '../../features/LoadingScreen/LoadingScreen';
import ErrorMessage from '../../features/Snackbars/errorMessage';

interface ForgotPasswordDialogProps {
  open: boolean;
  onClose: () => void;
  onSwitchToLogin: () => void;
}

export default function ForgotPasswordDialog({
  open,
  onClose,
  onSwitchToLogin,
}: ForgotPasswordDialogProps) {
  const [email, setEmail] = useState('');
  const [isSubmitted, setIsSubmitted] = useState(false);
  const forgotPassword = useForgotPassword();

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (validateEmail(email)) {
      try {
        await forgotPassword.mutateAsync(email);
        setEmail('');
        onClose();
      } catch (error) {}
    } else {
      setIsSubmitted(true);
    }
  };

  return (
    <LoadingScreen isLoading={forgotPassword.isPending}>
      <Dialog open={open} onClose={onClose} classes={{ paper: styles.dialogPaper }}>
        <form onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.header}>
            <h5 className={styles.title}>Forgot Password</h5>
            <p>
              Remembered your password?{' '}
              <span
                onClick={() => {
                  onClose();
                  onSwitchToLogin();
                }}
              >
                Back to Login
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
              variant="outlined"
              size="small"
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
            Send Reset Link
          </Button>
        </form>

        {forgotPassword.isError && (
          <ErrorMessage message={forgotPassword?.error?.message || 'Something went wrong'} />
        )}
      </Dialog>
    </LoadingScreen>
  );
}
