import * as React from 'react';
import Snackbar from '@mui/material/Snackbar';
import Slide from '@mui/material/Slide';
import { SlideProps } from '@mui/material/Slide';
import MuiAlert, { AlertProps } from '@mui/material/Alert';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';

function SlideTransition(props: SlideProps) {
  return <Slide {...props} direction="up" />;
}

const Alert = React.forwardRef<HTMLDivElement, AlertProps>(function Alert(props, ref) {
  return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
});

interface ErrorMessageProps {
  message: string;
}

export default function ErrorMessage({ message }: ErrorMessageProps) {
  const [open, setOpen] = React.useState(true);

  React.useEffect(() => {
    const timer = setTimeout(() => {
      setOpen(false);
    }, 2500); 

    return () => clearTimeout(timer);
  }, []);

  return (
    <Snackbar
      open={open}
      TransitionComponent={SlideTransition}
      autoHideDuration={2500}
      onClose={() => setOpen(false)}
      anchorOrigin={{ vertical: 'bottom', horizontal: 'left' }}
      sx={{ display: 'flex', alignItems: 'center' }}
    >
      <Alert
        severity="error"
        icon={false}
        sx={{
          backgroundColor: '#d32f2f',
          color: '#fff',
          fontSize: '0.5rem',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'flex-start',
          gap: '6px',
          minHeight: 'unset',
          height: '36px',
          '& .MuiAlert-message': {
            display: 'flex',
            alignItems: 'center',
            padding: 0,
          },
          '& .MuiAlert-action': {
            display: 'flex',
            alignItems: 'center',
            padding: 0,
          },
        }}
      >
        <ErrorOutlineIcon sx={{ fontSize: '1rem', color: '#fff', paddingRight: '0.4rem' }} />
        {message}
      </Alert>
    </Snackbar>
  );
}
