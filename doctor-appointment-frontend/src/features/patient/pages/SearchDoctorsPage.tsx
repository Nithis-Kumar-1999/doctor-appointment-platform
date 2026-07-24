import React, { useState } from 'react';
import { Box, Typography, TextField, MenuItem, Button, Card, CardContent, CircularProgress, Alert, Chip, Pagination } from '@mui/material';
import { Grid } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useDoctorSearch } from '../hooks/useDoctorSearch';
import { Specialty } from '../../doctor/types/doctor.types';

const SPECIALTIES: Specialty[] = [
  'CARDIOLOGY', 'DERMATOLOGY', 'NEUROLOGY', 'ORTHOPEDICS', 'PEDIATRICS',
  'PSYCHIATRY', 'GENERAL_MEDICINE', 'GYNECOLOGY', 'ONCOLOGY', 'OPHTHALMOLOGY',
  'ENT', 'UROLOGY', 'DENTISTRY', 'PHYSIOTHERAPY'
];

const SearchDoctorsPage: React.FC = () => {
  const navigate = useNavigate();
  
  const [specialty, setSpecialty] = useState<Specialty | ''>('');
  const [city, setCity] = useState('');
  const [page, setPage] = useState(0);

  // We debounce the city search manually by just passing it, or better, we let the user click search 
  // or use state. Since React Query is fast, we can just pass the state directly.
  const [searchCity, setSearchCity] = useState('');

  const { data, isLoading, isError } = useDoctorSearch(specialty, searchCity, page, 10);

  const handleSearch = () => {
    setSearchCity(city);
    setPage(0); // reset to first page on new search
  };

  const handlePageChange = (event: React.ChangeEvent<unknown>, value: number) => {
    setPage(value - 1);
  };

  return (
    <Box sx={{ p: 3, maxWidth: 1200, mx: 'auto' }}>
      <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 3 }}>
        Find a Doctor
      </Typography>

      {/* Search Filters */}
      <Card sx={{ mb: 4, p: 2 }}>
        <Grid container spacing={2}>
          <Grid size={{ xs: 12, sm: 4 }}>
            <TextField
              select
              fullWidth
              label="Specialty"
              value={specialty}
              onChange={(e) => {
                setSpecialty(e.target.value as Specialty | '');
                setPage(0);
              }}
            >
              <MenuItem value="">All Specialties</MenuItem>
              {SPECIALTIES.map((spec) => (
                <MenuItem key={spec} value={spec}>
                  {spec.replace('_', ' ')}
                </MenuItem>
              ))}
            </TextField>
          </Grid>
          <Grid size={{ xs: 12, sm: 5 }}>
            <TextField
              fullWidth
              label="City / Location"
              value={city}
              onChange={(e) => setCity(e.target.value)}
              onKeyDown={(e) => { if (e.key === 'Enter') handleSearch(); }}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 3 }}>
            <Button
              fullWidth
              variant="contained"
              size="large"
              onClick={handleSearch}
              sx={{ height: 56 }}
            >
              Search
            </Button>
          </Grid>
        </Grid>
      </Card>

      {/* Results */}
      {isLoading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', p: 5 }}>
          <CircularProgress />
        </Box>
      ) : isError ? (
        <Alert severity="error">Failed to load doctors. Please try again later.</Alert>
      ) : data?.content && data.content.length > 0 ? (
        <>
          <Grid container spacing={3}>
            {data.content.map((doctor) => (
              <Grid size={{ xs: 12, sm: 6, md: 4 }} key={doctor.id}>
                <Card 
                  elevation={2} 
                  sx={{ 
                    height: '100%', 
                    display: 'flex', 
                    flexDirection: 'column',
                    transition: 'transform 0.2s, box-shadow 0.2s',
                    '&:hover': { transform: 'translateY(-4px)', boxShadow: 6, cursor: 'pointer' }
                  }}
                  onClick={() => navigate(`/patient/doctor/${doctor.id}`, { state: { doctor } })}
                >
                  <CardContent sx={{ flexGrow: 1 }}>
                    <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                      Dr. {doctor.firstName} {doctor.lastName}
                    </Typography>
                    
                    <Chip 
                      label={doctor.specialty.replace('_', ' ')} 
                      color="primary" 
                      size="small" 
                      sx={{ mt: 1, mb: 2 }} 
                    />
                    
                    <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                      <strong>Qualification:</strong> {doctor.qualification}
                    </Typography>
                    
                    <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                      <strong>Experience:</strong> {doctor.experienceYears} years
                    </Typography>
                    
                    <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                      <strong>Fee:</strong> ${doctor.consultationFee}
                    </Typography>
                  </CardContent>
                  <Box sx={{ p: 2, pt: 0 }}>
                    <Button variant="outlined" fullWidth>
                      View Profile
                    </Button>
                  </Box>
                </Card>
              </Grid>
            ))}
          </Grid>
          
          {data.totalPages > 1 && (
            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
              <Pagination 
                count={data.totalPages} 
                page={page + 1} 
                onChange={handlePageChange} 
                color="primary" 
              />
            </Box>
          )}
        </>
      ) : (
        <Box sx={{ p: 4, textAlign: 'center' }}>
          <Typography variant="h6" color="text.secondary">
            No doctors found matching your search criteria.
          </Typography>
        </Box>
      )}
    </Box>
  );
};

export default SearchDoctorsPage;
