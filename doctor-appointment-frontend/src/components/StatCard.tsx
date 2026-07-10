import React from 'react';
import { Card, CardContent, Typography, Box } from '@mui/material';

interface StatCardProps {
  title: string;
  value: string | number;
  icon: React.ReactNode;
  color?: 'primary' | 'secondary' | 'success' | 'warning' | 'error' | 'info';
  trend?: {
    value: number;
    label: string;
    isPositive: boolean;
  };
}

/**
 * Reusable dashboard metric card.
 */
const StatCard: React.FC<StatCardProps> = ({ title, value, icon, color = 'primary', trend }) => {
  return (
    <Card sx={{ height: '100%' }}>
      <CardContent>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
          <Box>
            <Typography variant="subtitle2" color="text.secondary" gutterBottom fontWeight="bold" sx={{ textTransform: 'uppercase' }}>
              {title}
            </Typography>
            <Typography variant="h4" color="text.primary" fontWeight="bold">
              {value}
            </Typography>
            
            {trend && (
              <Box sx={{ display: 'flex', alignItems: 'center', mt: 1 }}>
                <Typography variant="body2" sx={{ color: trend.isPositive ? 'success.main' : 'error.main', fontWeight: 'bold', mr: 1 }}>
                  {trend.isPositive ? '+' : '-'}{Math.abs(trend.value)}%
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {trend.label}
                </Typography>
              </Box>
            )}
          </Box>
          <Box 
            sx={{ 
              p: 1.5, 
              borderRadius: 2, 
              display: 'flex', 
              backgroundColor: `${color}.light`, 
              color: `${color}.dark`,
              opacity: 0.8
            }}
          >
            {icon}
          </Box>
        </Box>
      </CardContent>
    </Card>
  );
};

export default StatCard;
