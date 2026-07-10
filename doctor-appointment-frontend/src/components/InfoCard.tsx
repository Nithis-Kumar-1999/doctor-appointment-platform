import React from 'react';
import { Card, CardContent, Typography, Box, Divider, List, ListItem, ListItemText } from '@mui/material';

interface InfoCardProps {
  title: string;
  details: { label: string; value: React.ReactNode }[];
  action?: React.ReactNode;
}

/**
 * Card specifically designed for displaying read-only entity details (e.g., Patient Profile details).
 */
const InfoCard: React.FC<InfoCardProps> = ({ title, details, action }) => {
  return (
    <Card sx={{ height: '100%' }}>
      <CardContent>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
          <Typography variant="h6" fontWeight="bold" color="text.primary">
            {title}
          </Typography>
          {action && <Box>{action}</Box>}
        </Box>
        <Divider sx={{ mb: 2 }} />
        
        <List disablePadding>
          {details.map((detail, index) => (
            <ListItem key={index} disablePadding sx={{ mb: 1.5, display: 'flex', flexDirection: { xs: 'column', sm: 'row' }, alignItems: { xs: 'flex-start', sm: 'center' } }}>
              <Typography variant="body2" color="text.secondary" sx={{ width: { sm: '40%' }, mb: { xs: 0.5, sm: 0 } }}>
                {detail.label}
              </Typography>
              <Typography variant="body1" color="text.primary" fontWeight="medium">
                {detail.value || 'N/A'}
              </Typography>
            </ListItem>
          ))}
        </List>
      </CardContent>
    </Card>
  );
};

export default InfoCard;
