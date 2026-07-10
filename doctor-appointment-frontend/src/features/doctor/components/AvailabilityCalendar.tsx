import React, { useState } from 'react';
import { Box, Card, CardContent, Typography, Button, Switch, FormControlLabel, IconButton, Grid, TextField } from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import AddIcon from '@mui/icons-material/Add';
import { DoctorAvailability } from '../types/doctorTypes';

interface AvailabilityCalendarProps {
  availabilities: DoctorAvailability[];
  onAdd: (data: Partial<DoctorAvailability>) => void;
  isLoading: boolean;
}

const DAYS_OF_WEEK = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];

const AvailabilityCalendar: React.FC<AvailabilityCalendarProps> = ({ availabilities, onAdd, isLoading }) => {
  const [selectedDay, setSelectedDay] = useState<string>('MONDAY');
  const [startTime, setStartTime] = useState('09:00');
  const [endTime, setEndTime] = useState('17:00');

  const handleAdd = () => {
    onAdd({
      dayOfWeek: selectedDay,
      startTime: `${startTime}:00`, // backend might expect HH:MM:SS
      endTime: `${endTime}:00`,
      isAvailable: true
    });
  };

  if (isLoading) return <Typography>Loading schedule...</Typography>;

  return (
    <Card>
      <CardContent>
        <Typography variant="h6" fontWeight="bold" gutterBottom>
          Weekly Schedule
        </Typography>
        
        {DAYS_OF_WEEK.map((day) => {
          const dayAvails = availabilities.filter(a => a.dayOfWeek === day);
          return (
            <Box key={day} sx={{ mb: 3, p: 2, border: '1px solid', borderColor: 'divider', borderRadius: 2 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                <Typography variant="subtitle1" fontWeight="bold">{day}</Typography>
                <FormControlLabel control={<Switch defaultChecked color="success" />} label="Available" />
              </Box>
              
              {dayAvails.length === 0 ? (
                <Typography variant="body2" color="text.secondary">No time slots set.</Typography>
              ) : (
                <Grid container spacing={1}>
                  {dayAvails.map(avail => (
                    <Grid item xs={12} sm={6} md={4} key={avail.id}>
                      <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', bgcolor: 'background.default', p: 1, borderRadius: 1 }}>
                        <Typography variant="body2">{avail.startTime.substring(0, 5)} - {avail.endTime.substring(0, 5)}</Typography>
                        <IconButton size="small" color="error"><DeleteIcon fontSize="small" /></IconButton>
                      </Box>
                    </Grid>
                  ))}
                </Grid>
              )}
            </Box>
          );
        })}

        <Box sx={{ mt: 4, p: 2, bgcolor: 'background.default', borderRadius: 2 }}>
          <Typography variant="subtitle2" fontWeight="bold" gutterBottom>Add Time Slot</Typography>
          <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap', alignItems: 'center' }}>
            <TextField 
              select 
              size="small" 
              value={selectedDay} 
              onChange={(e) => setSelectedDay(e.target.value)}
              SelectProps={{ native: true }}
            >
              {DAYS_OF_WEEK.map(day => <option key={day} value={day}>{day}</option>)}
            </TextField>
            <TextField type="time" size="small" value={startTime} onChange={(e) => setStartTime(e.target.value)} />
            <Typography>to</Typography>
            <TextField type="time" size="small" value={endTime} onChange={(e) => setEndTime(e.target.value)} />
            <Button variant="contained" startIcon={<AddIcon />} onClick={handleAdd}>Add</Button>
          </Box>
        </Box>
      </CardContent>
    </Card>
  );
};

export default AvailabilityCalendar;
