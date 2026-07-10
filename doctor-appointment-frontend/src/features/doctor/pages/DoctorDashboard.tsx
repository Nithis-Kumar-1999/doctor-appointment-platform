import React from 'react';
import PageContainer from '../../../components/PageContainer';
import PageHeader from '../../../components/PageHeader';
import DashboardStats from '../components/DashboardStats';
import UpcomingAppointmentsCard from '../components/UpcomingAppointmentsCard';
import { useDoctorDashboardStats } from '../hooks/useDoctorQueries';
import LoadingOverlay from '../../../components/LoadingOverlay';

const DoctorDashboard = () => {
  const { data: stats, isLoading } = useDoctorDashboardStats();

  return (
    <PageContainer>
      <PageHeader 
        title="Doctor Dashboard" 
        subtitle="Welcome back. Here is your practice overview."
        breadcrumbs={[{ label: 'Dashboard' }]}
      />
      
      {isLoading ? (
        <LoadingOverlay open={true} message="Loading dashboard..." />
      ) : (
        <>
          <DashboardStats stats={stats} />
          <UpcomingAppointmentsCard />
        </>
      )}
    </PageContainer>
  );
};

export default DoctorDashboard;
