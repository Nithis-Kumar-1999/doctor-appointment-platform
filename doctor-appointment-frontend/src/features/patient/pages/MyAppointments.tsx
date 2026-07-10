import React from 'react';
import PageContainer from '../../../components/PageContainer';
import PageHeader from '../../../components/PageHeader';
import AppointmentHistoryTable from '../components/AppointmentHistoryTable';

const MyAppointments = () => {
  return (
    <PageContainer>
      <PageHeader title="My Appointments" breadcrumbs={[{ label: 'Appointments' }]} />
      <AppointmentHistoryTable />
    </PageContainer>
  );
};

export default MyAppointments;
