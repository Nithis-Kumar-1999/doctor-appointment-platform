import React, { useState } from 'react';
import { Box, TextField, InputAdornment, Pagination as MuiPagination, CircularProgress, Typography } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import PageContainer from '../../../components/PageContainer';
import PageHeader from '../../../components/PageHeader';
import FilterPanel from '../../../components/FilterPanel';
import DoctorGrid from '../components/DoctorGrid';
import { useFindDoctors } from '../hooks/usePatientQueries';

const FindDoctors = () => {
  const [page, setPage] = useState(1);
  const [size] = useState(12);
  const [search, setSearch] = useState('');
  const [specialty, setSpecialty] = useState('');

  // Local debounce (simplified for scope, usually handled by custom hook)
  const [debouncedSearch, setDebouncedSearch] = useState('');
  React.useEffect(() => {
    const handler = setTimeout(() => setDebouncedSearch(search), 500);
    return () => clearTimeout(handler);
  }, [search]);

  const { data, isLoading } = useFindDoctors(page - 1, size, specialty, debouncedSearch);

  return (
    <PageContainer>
      <PageHeader title="Find Doctors" subtitle="Search and book appointments with top specialists" breadcrumbs={[{ label: 'Find Doctors' }]} />
      
      <FilterPanel title="Search & Filters" defaultExpanded>
        <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
          <TextField
            placeholder="Search by name or clinic..."
            size="small"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start"><SearchIcon /></InputAdornment>
              ),
            }}
            sx={{ minWidth: 300, flexGrow: 1 }}
          />
          <TextField
            select
            size="small"
            value={specialty}
            onChange={(e) => setSpecialty(e.target.value)}
            SelectProps={{ native: true }}
            sx={{ minWidth: 200 }}
          >
            <option value="">All Specialties</option>
            <option value="Cardiology">Cardiology</option>
            <option value="Dermatology">Dermatology</option>
            <option value="Neurology">Neurology</option>
            <option value="Pediatrics">Pediatrics</option>
            <option value="Orthopedics">Orthopedics</option>
          </TextField>
        </Box>
      </FilterPanel>

      {isLoading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}><CircularProgress /></Box>
      ) : (
        <DoctorGrid doctors={data?.content || []} />
      )}

      {data && data.totalElements > 0 && (
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 6 }}>
          <MuiPagination 
            count={Math.ceil(data.totalElements / size)} 
            page={page} 
            onChange={(_, value) => setPage(value)} 
            color="primary" 
          />
        </Box>
      )}
    </PageContainer>
  );
};

export default FindDoctors;
