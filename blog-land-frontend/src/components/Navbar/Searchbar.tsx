import * as React from 'react';
import Autocomplete from '@mui/material/Autocomplete';
import TextField from '@mui/material/TextField';
import InputAdornment from '@mui/material/InputAdornment';
import IconButton from '@mui/material/IconButton';
import SearchIcon from '@mui/icons-material/Search';
import { useState, useEffect } from 'react';
import { useSearch } from '../../hooks/usePost';
import { PostResponse } from '../../types/category/response';
import ErrorMessage from '../Snackbars/Snackbar';

export default function SearchBar() {
  const [inputValue, setInputValue] = useState('');
  const { searchData, loading, error, setError, getSearch } = useSearch<PostResponse>();

  // Debounced search
  useEffect(() => {
    if (!inputValue.trim()) return;
    const delayDebounce = setTimeout(() => {
      getSearch(inputValue);
    }, 500);

    return () => clearTimeout(delayDebounce);
  }, [inputValue, getSearch]);

  return (
    <>
      <Autocomplete<PostResponse, false, true, true>
        freeSolo
        disableClearable
        options={searchData || []}
        getOptionLabel={(option) => (typeof option === 'string' ? option : option.title)}
        inputValue={inputValue}
        onInputChange={(event, newInputValue) => setInputValue(newInputValue)}
        loading={loading}
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
            }}
          >
            <div
              style={{
                whiteSpace: 'nowrap',
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                flexGrow: 1,
              }}
              title={`${option.title}`} // shows full text on hover
            >
              {option.title} — {option.author}
            </div>
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

      {/* Show error snackbar when error is set */}
      {error && <ErrorMessage message={error} open={!!error} onClose={() => setError(null)} />}
    </>
  );
}
