import * as React from 'react';
import Dialog from '@mui/material/Dialog';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import classNames from 'classnames';
import styles from './Form.module.css';
import GoogleOAuthButton from './GoogleOAuthButton';

interface RegisterDialogProps {
  open: boolean;
  onClose: () => void;
}

export default function RegisterDialog({ open, onClose }: RegisterDialogProps) {
  const [firstName, setFirstName] = React.useState('');
  const [lastName, setLastName] = React.useState('');
  const [email, setEmail] = React.useState('');
  const [password, setPassword] = React.useState('');

  const [firstNameTouched, setFirstNameTouched] = React.useState(false);
  const [lastNameTouched, setLastNameTouched] = React.useState(false);
  const [emailTouched, setEmailTouched] = React.useState(false);
  const [passwordTouched, setPasswordTouched] = React.useState(false);

  const validateRequired = (value: string) => value.trim().length > 0;
  const validateEmail = (value: string) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);
  const validatePassword = (value: string) => value.length >= 6;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    const firstNameValid = validateRequired(firstName);
    const lastNameValid = validateRequired(lastName);
    const emailValid = validateEmail(email);
    const passwordValid = validatePassword(password);

    if (firstNameValid && lastNameValid && emailValid && passwordValid) {
      console.log('Register data:', { firstName, lastName, email, password });
      // onClose();
    } else {
      setFirstNameTouched(true);
      setLastNameTouched(true);
      setEmailTouched(true);
      setPasswordTouched(true);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} classes={{ paper: styles.dialogPaper }}>
      <form onSubmit={handleSubmit} className={styles.form}>
        <h2 className={styles.title}>Register</h2>

        <div className={styles.inputGroup}>
          <label
            htmlFor="firstName"
            className={classNames(styles.label, {
              [styles.valid]: firstNameTouched && validateRequired(firstName),
              [styles.invalid]: firstNameTouched && !validateRequired(firstName),
            })}
          >
            First Name
          </label>
          <TextField
            id="firstName"
            value={firstName}
            onChange={(e) => {
              setFirstName(e.target.value);
              setFirstNameTouched(true);
            }}
            fullWidth
            variant="outlined"
            size="small"
            InputLabelProps={{ shrink: false }}
            className={classNames(styles.textField, {
              [styles.validField]: firstNameTouched && validateRequired(firstName),
              [styles.invalidField]: firstNameTouched && !validateRequired(firstName),
            })}
          />
        </div>

        <div className={styles.inputGroup}>
          <label
            htmlFor="lastName"
            className={classNames(styles.label, {
              [styles.valid]: lastNameTouched && validateRequired(lastName),
              [styles.invalid]: lastNameTouched && !validateRequired(lastName),
            })}
          >
            Last Name
          </label>
          <TextField
            id="lastName"
            value={lastName}
            onChange={(e) => {
              setLastName(e.target.value);
              setLastNameTouched(true);
            }}
            fullWidth
            variant="outlined"
            size="small"
            InputLabelProps={{ shrink: false }}
            className={classNames(styles.textField, {
              [styles.validField]: lastNameTouched && validateRequired(lastName),
              [styles.invalidField]: lastNameTouched && !validateRequired(lastName),
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
  );
}
