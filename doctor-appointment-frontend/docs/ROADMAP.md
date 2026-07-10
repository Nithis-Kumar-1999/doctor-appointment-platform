# 🛣️ Product Roadmap

## Version 1.1 (Short Term)
- **Email Notifications**: Integration with SendGrid/AWS SES for appointment confirmations and cancellations.
- **Password Reset**: Secure forgot-password flow with time-bound JWT reset links.
- **Profile Image Upload**: AWS S3 integration for Doctor and Patient avatars.
- **Better Doctor Search**: Advanced filtering by Zip Code, Gender, and specific specialized treatments.
- **Pagination Improvements**: Server-side cursor pagination for massive appointment histories.

## Version 2.0 (Medium Term)
- **Video Consultation**: WebRTC integration for secure, in-browser telemedicine calls.
- **Online Payments**: Stripe API integration to capture consultation fees during the booking wizard.
- **Medical Records**: Secure PDF document upload and sharing between Patient and Doctor.
- **Real-Time Notifications**: WebSocket (STOMP) integration for instant browser push notifications.
- **Calendar Sync**: Export appointments to Google Calendar and Apple Calendar (`.ics` generation).
- **Ratings & Reviews**: Post-appointment survey allowing patients to rate doctors out of 5 stars.

## Version 3.0 (Long Term)
- **AI Appointment Recommendations**: Machine learning model suggesting ideal time slots based on patient historical behavior.
- **AI Symptom Checker**: NLP-driven chat interface triaging patients to the correct specialty prior to booking.
- **Admin Analytics**: Global dashboard for hospital administrators tracking revenue, no-show rates, and platform usage.
- **Hospital Management**: Multi-tenant architecture supporting distinct clinics under one unified SaaS platform.
- **Multi-language Support**: `i18n` internationalization supporting Spanish, French, and Mandarin.
- **Mobile Apps**: React Native cross-platform applications leveraging the existing Spring Boot APIs.
