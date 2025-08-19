import * as React from 'react';
import Autocomplete from '@mui/material/Autocomplete';
import TextField from '@mui/material/TextField';
import InputAdornment from '@mui/material/InputAdornment';
import IconButton from '@mui/material/IconButton';
import SearchIcon from '@mui/icons-material/Search';
import Tooltip from '@mui/material/Tooltip';
import { useSearchPost } from '../../../hooks/usePost';
import ErrorMessage from '../../../features/Snackbars/Snackbar';
import styles from './Searchbar.module.css';
import { ROUTES } from '../../../constants/routes';
import { useNavigate } from 'react-router-dom';

export default function SearchBar() {
  const { searchTerm, setSearchTerm, results, isLoading, isError, error } = useSearchPost();
  const navigate = useNavigate();

  return (
    <>
      <Autocomplete
        freeSolo
        disableClearable
        options={results || []}
        getOptionLabel={(option) => (typeof option === 'string' ? option : option.title)}
        inputValue={searchTerm}
        onInputChange={(event, newValue) => setSearchTerm(newValue)}
        onChange={(event, selectedOption) => {
          if (selectedOption && typeof selectedOption !== 'string') {
            navigate(ROUTES.POST(selectedOption.id));
          }
        }}
        loading={isLoading}
        className={styles.autocomplete}
        renderOption={(props, option) => (
          <li {...props} key={option.id} className={styles.option}>
            <Tooltip
              title={`${option.title}`}
              arrow
              placement="right"
              enterDelay={300}
              leaveDelay={100}
            >
              <div className={styles.optionText}>{option.title}</div>
            </Tooltip>
          </li>
        )}
        renderInput={(params) => (
          <TextField
            {...params}
            placeholder="Search Blogs"
            variant="outlined"
            size="medium"
            InputProps={{
              ...params.InputProps,
              classes: {
                root: styles.inputRoot,
                input: styles.inputInput,
              },
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
