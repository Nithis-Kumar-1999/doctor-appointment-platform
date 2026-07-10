import React from 'react';
import PageContainer from '../../../components/PageContainer';
import PageHeader from '../../../components/PageHeader';
import AppointmentTable from '../components/AppointmentTable';

const DoctorAppointments = () => {
  return (
    <PageContainer>
      <PageHeader title="Appointments Management" breadcrumbs={[{ label: 'Appointments' }]} />
      <AppointmentTable />
    </PageContainer>
  );
};

export default DoctorAppointments;
