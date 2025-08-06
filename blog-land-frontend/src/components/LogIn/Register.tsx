import * as React from 'react';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';

interface RegisterDialogProps {
  open: boolean;
  onClose: () => void;
}

export default function RegisterDialog({ open, onClose }: RegisterDialogProps) {
  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>Register</DialogTitle>
      <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2, width: 300 }}>
        <TextField label="Full Name" variant="outlined" size="small" />
        <TextField label="Email" type="email" variant="outlined" size="small" />
        <TextField label="Password" type="password" variant="outlined" size="small" />
        <Button variant="contained" color="primary">
          Create Account
        </Button>
      </DialogContent>
    </Dialog>
  );
}
