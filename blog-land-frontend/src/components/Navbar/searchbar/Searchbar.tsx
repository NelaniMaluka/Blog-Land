import * as React from 'react';
import Autocomplete from '@mui/material/Autocomplete';
import TextField from '@mui/material/TextField';
import InputAdornment from '@mui/material/InputAdornment';
import IconButton from '@mui/material/IconButton';
import SearchIcon from '@mui/icons-material/Search';
import Tooltip from '@mui/material/Tooltip';
import { useSearchPost } from '../../../hooks/usePost';
import ErrorMessage from '../../../features/Snackbars/Snackbar';

export default function SearchBar() {
  const { searchTerm, setSearchTerm, results, isLoading, isError, error } = useSearchPost();

  return (
    <>
      <Autocomplete
        freeSolo
        disableClearable
        options={results || []}
        getOptionLabel={(option) => (typeof option === 'string' ? option : option.title)}
        inputValue={searchTerm}
        onInputChange={(event, newValue) => setSearchTerm(newValue)}
        loading={isLoading}
        sx={{ width: 450 }}
        renderOption={(props, option) => (
          <li
            {...props}
            key={option.id}
            style={{
              padding: '3px 8px',
              fontSize: '0.7rem',
              fontWeight: 400,
              maxWidth: '100%',
              display: 'flex',
              cursor: 'default',
            }}
          >
            <Tooltip
              title={`${option.title} — ${option.author}`}
              arrow
              placement="right"
              enterDelay={300}
              leaveDelay={100}
            >
              <div
                style={{
                  whiteSpace: 'nowrap',
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  flexGrow: 1,
                }}
              >
                {option.title} — {option.author}
              </div>
            </Tooltip>
          </li>
        )}
        renderInput={(params) => (
          <TextField
            {...params}
            placeholder="Search Blogs"
            variant="outlined"
            size="medium"
            sx={{
              '& .MuiOutlinedInput-root': {
                borderRadius: '30px',
                height: 40,
                boxShadow: '0 2px 5px rgba(0,0,0,0.1)',
              },
              '& .MuiInputBase-input': {
                padding: '0px 8px',
                fontSize: '0.7rem',
              },
            }}
            InputProps={{
              ...params.InputProps,
              endAdornment: (
                <InputAdornment position="end">
                  <IconButton edge="end" aria-label="search" size="medium">
                    <SearchIcon sx={{ height: '0.9rem' }} />
                  </IconButton>
                </InputAdornment>
              ),
            }}
          />
        )}
      />

      {isError && <ErrorMessage message={error?.message || 'Something went wrong'} />}
    </>
  );
}
