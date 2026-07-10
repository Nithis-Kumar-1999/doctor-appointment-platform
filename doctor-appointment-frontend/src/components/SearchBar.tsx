import React from 'react';
import { Paper, InputBase, IconButton, SxProps, Theme } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import ClearIcon from '@mui/icons-material/Clear';

interface SearchBarProps {
  value: string;
  onChange: (value: string) => void;
  onSearch?: () => void;
  placeholder?: string;
  sx?: SxProps<Theme>;
}

/**
 * Standardized Search Bar with clear functionality.
 */
const SearchBar: React.FC<SearchBarProps> = ({ value, onChange, onSearch, placeholder = 'Search...', sx }) => {
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (onSearch) onSearch();
  };

  return (
    <Paper
      component="form"
      onSubmit={handleSubmit}
      sx={{ p: '2px 4px', display: 'flex', alignItems: 'center', width: 300, boxShadow: 'none', border: '1px solid', borderColor: 'divider', ...sx }}
    >
      <IconButton sx={{ p: '10px' }} aria-label="search" type="submit">
        <SearchIcon />
      </IconButton>
      <InputBase
        sx={{ ml: 1, flex: 1 }}
        placeholder={placeholder}
        inputProps={{ 'aria-label': placeholder }}
        value={value}
        onChange={(e) => onChange(e.target.value)}
      />
      {value && (
        <IconButton sx={{ p: '10px' }} aria-label="clear" onClick={() => onChange('')}>
          <ClearIcon fontSize="small" />
        </IconButton>
      )}
    </Paper>
  );
};

export default SearchBar;
