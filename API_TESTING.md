# MyDentist2 — API Testing Guide (Postman)

## Setup

- **Base URL**: `http://localhost:8080`
- **Content-Type**: `application/json` (on all requests with a body)
- After login/register, copy the `token` from the response and set it as:
  - Header: `Authorization: Bearer <token>`

---

## 1. AUTH

### Register Patient
```
POST /api/auth/register
```
```json
{
  "name": "Alice Martin",
  "email": "alice@example.com",
  "password": "password123",
  "role": "PATIENT",
  "phone": "0612345678",
  "address": "12 Rue de Paris, Casablanca"
}
```

---

### Register Dentist
```
POST /api/auth/register
```
```json
{
  "name": "Dr. Karim Benali",
  "email": "karim@example.com",
  "password": "password123",
  "role": "DENTIST",
  "phone": "0698765432",
  "specialty": "Orthodontie",
  "address": "5 Avenue Hassan II, Rabat",
  "latitude": 34.0209,
  "longitude": -6.8416
}
```

---

### Login
```
POST /api/auth/login
```
```json
{
  "email": "alice@example.com",
  "password": "password123"
}
```
**Response:**
```json
{
  "token": "jwt-token-placeholder",
  "id": 1,
  "name": "Alice Martin",
  "email": "alice@example.com",
  "role": "PATIENT"
}
```

---

## 2. PATIENTS

### Get All Patients
```
GET /api/patients
Authorization: Bearer <token>
```

---

### Get Patient by ID
```
GET /api/patients/1
Authorization: Bearer <token>
```

---

### Update Patient
```
PUT /api/patients/1
Authorization: Bearer <token>
```
```json
{
  "name": "Alice Martin",
  "email": "alice@example.com",
  "phone": "0699999999",
  "address": "20 Rue Ibn Sina, Casablanca"
}
```

---

### Delete Patient
```
DELETE /api/patients/1
Authorization: Bearer <token>
```
**Response:** `204 No Content`

---

## 3. DENTISTS

### Get All Dentists
```
GET /api/dentists
Authorization: Bearer <token>
```

---

### Get Dentist by ID
```
GET /api/dentists/2
Authorization: Bearer <token>
```

---

### Get Dentists by Specialty
```
GET /api/dentists/specialty/Orthodontie
Authorization: Bearer <token>
```

---

### Update Dentist
```
PUT /api/dentists/2
Authorization: Bearer <token>
```
```json
{
  "name": "Dr. Karim Benali",
  "email": "karim@example.com",
  "phone": "0698765432",
  "specialty": "Implantologie",
  "address": "5 Avenue Hassan II, Rabat",
  "latitude": 34.0209,
  "longitude": -6.8416
}
```

---

### Delete Dentist
```
DELETE /api/dentists/2
Authorization: Bearer <token>
```
**Response:** `204 No Content`

---

## 4. APPOINTMENTS

### Create Appointment
```
POST /api/appointments
Authorization: Bearer <token>
```
```json
{
  "patientId": 1,
  "dentistId": 2,
  "dateTime": "2025-09-15T10:00:00",
  "notes": "First consultation"
}
```

---

### Get Appointment by ID
```
GET /api/appointments/1
Authorization: Bearer <token>
```

---

### Get Appointments by Patient
```
GET /api/appointments/patient/1
Authorization: Bearer <token>
```

---

### Get Appointments by Dentist
```
GET /api/appointments/dentist/2
Authorization: Bearer <token>
```

---

### Update Appointment
```
PUT /api/appointments/1
Authorization: Bearer <token>
```
```json
{
  "patientId": 1,
  "dentistId": 2,
  "dateTime": "2025-09-16T14:00:00",
  "notes": "Rescheduled"
}
```

---

### Update Appointment Status
```
PATCH /api/appointments/1/status?status=CONFIRMED
Authorization: Bearer <token>
```
> Available statuses: `PENDING`, `CONFIRMED`, `CANCELLED`, `COMPLETED`

---

### Cancel Appointment
```
PATCH /api/appointments/1/cancel
Authorization: Bearer <token>
```
**Response:** `204 No Content`

---

## 5. PAYMENTS

### Create Payment
```
POST /api/payments
Authorization: Bearer <token>
```
```json
{
  "appointmentId": 1,
  "amount": 350.00,
  "stripePaymentId": "pi_3OxampleStripeId"
}
```

---

### Get All Payments
```
GET /api/payments
Authorization: Bearer <token>
```

---

### Get Payment by ID
```
GET /api/payments/1
Authorization: Bearer <token>
```

---

### Get Payment by Appointment
```
GET /api/payments/appointment/1
Authorization: Bearer <token>
```

---

### Refund Payment
```
PATCH /api/payments/1/refund
Authorization: Bearer <token>
```

---

## 6. MEDICAL DOCUMENTS

### Upload Document (metadata)
```
POST /api/documents
Authorization: Bearer <token>
```
```json
{
  "patientId": 1,
  "uploadedById": 2,
  "fileName": "radio_panoramique.jpg",
  "s3Key": "documents/patient-1/radio_panoramique.jpg",
  "type": "XRAY"
}
```
> Available types: `XRAY`, `ANALYSIS`, `PRESCRIPTION`, `OTHER`

---

### Get Document by ID
```
GET /api/documents/1
Authorization: Bearer <token>
```

---

### Get Documents by Patient
```
GET /api/documents/patient/1
Authorization: Bearer <token>
```

---

### Get Documents by Patient and Type
```
GET /api/documents/patient/1/type/XRAY
Authorization: Bearer <token>
```

---

### Delete Document
```
DELETE /api/documents/1
Authorization: Bearer <token>
```
**Response:** `204 No Content`

---

## Error Responses

All errors follow this format:

```json
{
  "status": 404,
  "message": "Patient not found with id: 99",
  "errors": null,
  "timestamp": "2025-08-01T10:30:00"
}
```

Validation errors return field-level details:

```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "email": "must be a well-formed email address",
    "password": "size must be between 8 and 2147483647"
  },
  "timestamp": "2025-08-01T10:30:00"
}
```

---

## HTTP Status Codes Summary

| Code | Meaning |
|------|---------|
| `200` | OK |
| `201` | Created |
| `204` | No Content (delete/cancel) |
| `400` | Bad Request / Validation error |
| `404` | Resource not found |
| `401` | Unauthorized (missing/invalid token) |
