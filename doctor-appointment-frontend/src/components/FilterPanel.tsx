import React from 'react';
import { Box, Paper, Typography, Collapse, IconButton } from '@mui/material';
import FilterListIcon from '@mui/icons-material/FilterList';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';

interface FilterPanelProps {
  title?: string;
  children: React.ReactNode;
  defaultExpanded?: boolean;
}

/**
 * Reusable collapsible Filter Panel to house complex search/filtering inputs above a DataTable.
 */
const FilterPanel: React.FC<FilterPanelProps> = ({ title = 'Filters', children, defaultExpanded = false }) => {
  const [expanded, setExpanded] = React.useState(defaultExpanded);

  return (
    <Paper sx={{ mb: 3, boxShadow: 'none', border: '1px solid', borderColor: 'divider' }}>
      <Box 
        sx={{ p: 2, display: 'flex', alignItems: 'center', justifyContent: 'space-between', cursor: 'pointer', bgcolor: 'background.default' }}
        onClick={() => setExpanded(!expanded)}
      >
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          <FilterListIcon color="action" sx={{ mr: 1 }} />
          <Typography variant="subtitle1" fontWeight="bold">
            {title}
          </Typography>
        </Box>
        <IconButton size="small">
          {expanded ? <KeyboardArrowUpIcon /> : <KeyboardArrowDownIcon />}
        </IconButton>
      </Box>
      <Collapse in={expanded}>
        <Box sx={{ p: 2, borderTop: '1px solid', borderColor: 'divider' }}>
          {children}
        </Box>
      </Collapse>
    </Paper>
  );
};

export default FilterPanel;
