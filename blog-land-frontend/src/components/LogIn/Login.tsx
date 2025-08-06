import * as React from 'react';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import styles from './Form.module.css';

interface LoginDialogProps {
  open: boolean;
  onClose: () => void;
}

export default function LoginDialog({ open, onClose }: LoginDialogProps) {
  return (
    <Dialog open={open} onClose={onClose}>
      <form action="" className={styles.form}>
        <h1>LogIn</h1>
      </form>
    </Dialog>
  );
}
