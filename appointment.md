# Appointment System — Postman Testing Guide

Base URL: `http://localhost:8080`

---

## 0. Setup — Get JWT Token

All endpoints (except `/api/auth/**`) require a Bearer token.
Add this header to every request after login:

```
Authorization: Bearer <your_token>
```

---

## STEP 1 — Register a Patient

**POST** `http://localhost:8080/api/auth/register`

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "name": "Ahmed Benali",
  "email": "ahmed@gmail.com",
  "password": "password123",
  "role": "PATIENT",
  "phone": "0612345678",
  "address": "12 Rue Hassan II, Casablanca"
}
```

**Expected Response — 201:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "ahmed@gmail.com",
  "role": "PATIENT"
}
```

---

## STEP 2 — Register a Dentist

**POST** `http://localhost:8080/api/auth/register`

**Body:**
```json
{
  "name": "Dr. Sara Idrissi",
  "email": "sara.dentist@gmail.com",
  "password": "password123",
  "role": "DENTIST",
  "specialty": "Orthodontics",
  "phone": "0698765432",
  "address": "45 Avenue Mohammed V, Rabat",
  "latitude": 34.0209,
  "longitude": -6.8416
}
```

**Expected Response — 201:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "sara.dentist@gmail.com",
  "role": "DENTIST"
}
```

---

## STEP 3 — Login

**POST** `http://localhost:8080/api/auth/login`

**Body:**
```json
{
  "email": "ahmed@gmail.com",
  "password": "password123"
}
```

**Expected Response — 200:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "ahmed@gmail.com",
  "role": "PATIENT"
}
```

> Copy the `token` value — use it as `Bearer <token>` in all requests below.

---

## STEP 4 — Get All Dental Services

**GET** `http://localhost:8080/api/appointments/services`

**Headers:**
```
Authorization: Bearer <token>
```

**Expected Response — 200:**
```json
[
  { "type": "CONSULTATION",         "label": "Consultation",            "durationMinutes": 30 },
  { "type": "CLEANING",             "label": "Teeth Cleaning",          "durationMinutes": 45 },
  { "type": "FILLING",              "label": "Dental Filling",          "durationMinutes": 60 },
  { "type": "EXTRACTION",           "label": "Tooth Extraction",        "durationMinutes": 45 },
  { "type": "ROOT_CANAL",           "label": "Root Canal Treatment",    "durationMinutes": 90 },
  { "type": "CROWN",                "label": "Crown Placement",         "durationMinutes": 75 },
  { "type": "WHITENING",            "label": "Teeth Whitening",         "durationMinutes": 60 },
  { "type": "ORTHODONTIC_CHECKUP",  "label": "Orthodontic Check-up",    "durationMinutes": 30 },
  { "type": "IMPLANT_CONSULTATION", "label": "Implant Consultation",    "durationMinutes": 45 },
  { "type": "EMERGENCY",            "label": "Emergency Care",          "durationMinutes": 30 }
]
```

---

## STEP 5 — Get Available Time Slots

**GET** `http://localhost:8080/api/appointments/dentist/1/slots`

**Headers:**
```
Authorization: Bearer <token>
```

**Query Params:**
```
date        = 2025-08-15
serviceType = CLEANING
```

Full URL:
```
http://localhost:8080/api/appointments/dentist/1/slots?date=2025-08-15&serviceType=CLEANING
```

**Expected Response — 200:**
```json
[
  {
    "startTime": "2025-08-15T09:00:00",
    "endTime":   "2025-08-15T09:45:00",
    "durationMinutes": 45
  },
  {
    "startTime": "2025-08-15T09:15:00",
    "endTime":   "2025-08-15T10:00:00",
    "durationMinutes": 45
  },
  {
    "startTime": "2025-08-15T09:30:00",
    "endTime":   "2025-08-15T10:15:00",
    "durationMinutes": 45
  }
]
```

> Slots are generated on a 15-minute grid from 09:00 to 18:00.
> Sunday returns an empty array `[]`.

---

## STEP 6 — Book an Appointment

**POST** `http://localhost:8080/api/appointments`

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Body:**
```json
{
  "patientId":   1,
  "dentistId":   2,
  "startTime":   "2025-08-15T09:00:00",
  "serviceType": "CLEANING",
  "notes":       "First visit, sensitive gums"
}
```

**Expected Response — 201:**
```json
{
  "id":              1,
  "patientId":       1,
  "patientName":     "Ahmed Benali",
  "dentistId":       2,
  "dentistName":     "Dr. Sara Idrissi",
  "startTime":       "2025-08-15T09:00:00",
  "endTime":         "2025-08-15T09:45:00",
  "serviceType":     "CLEANING",
  "serviceLabel":    "Teeth Cleaning",
  "durationMinutes": 45,
  "status":          "PENDING",
  "notes":           "First visit, sensitive gums"
}
```

---

## STEP 7 — Book a Second Appointment (Overlap Test)

**POST** `http://localhost:8080/api/appointments`

**Body (overlaps the first — same dentist, same time window):**
```json
{
  "patientId":   1,
  "dentistId":   2,
  "startTime":   "2025-08-15T09:20:00",
  "serviceType": "CONSULTATION",
  "notes":       "This should fail"
}
```

**Expected Response — 409 Conflict:**
```json
{
  "status":  409,
  "message": "Dentist already has an appointment overlapping 2025-08-15T09:20 – 2025-08-15T09:50",
  "errors":  null
}
```

---

## STEP 8 — Book Outside Working Hours (Validation Test)

**POST** `http://localhost:8080/api/appointments`

**Body (starts at 19:00 — after clinic closes):**
```json
{
  "patientId":   1,
  "dentistId":   2,
  "startTime":   "2025-08-15T19:00:00",
  "serviceType": "CONSULTATION"
}
```

**Expected Response — 400 Bad Request:**
```json
{
  "status":  400,
  "message": "Appointment must be within working hours 09:00–18:00. Requested: 19:00 – 19:30",
  "errors":  null
}
```

---

## STEP 9 — Get Appointment by ID

**GET** `http://localhost:8080/api/appointments/1`

**Headers:**
```
Authorization: Bearer <token>
```

**Expected Response — 200:**
```json
{
  "id":              1,
  "patientId":       1,
  "patientName":     "Ahmed Benali",
  "dentistId":       2,
  "dentistName":     "Dr. Sara Idrissi",
  "startTime":       "2025-08-15T09:00:00",
  "endTime":         "2025-08-15T09:45:00",
  "serviceType":     "CLEANING",
  "serviceLabel":    "Teeth Cleaning",
  "durationMinutes": 45,
  "status":          "PENDING",
  "notes":           "First visit, sensitive gums"
}
```

---

## STEP 10 — Get All Appointments for a Patient

**GET** `http://localhost:8080/api/appointments/patient/1`

**Headers:**
```
Authorization: Bearer <token>
```

**Expected Response — 200:**
```json
[
  {
    "id":              1,
    "patientName":     "Ahmed Benali",
    "dentistName":     "Dr. Sara Idrissi",
    "startTime":       "2025-08-15T09:00:00",
    "endTime":         "2025-08-15T09:45:00",
    "serviceLabel":    "Teeth Cleaning",
    "durationMinutes": 45,
    "status":          "PENDING"
  }
]
```

---

## STEP 11 — Get All Appointments for a Dentist

**GET** `http://localhost:8080/api/appointments/dentist/2`

**Headers:**
```
Authorization: Bearer <token>
```

---

## STEP 12 — Daily Schedule (Calendar View)

**GET** `http://localhost:8080/api/appointments/dentist/2/daily?date=2025-08-15`

**Headers:**
```
Authorization: Bearer <token>
```

**Expected Response — 200:**
```json
[
  {
    "id":              1,
    "patientName":     "Ahmed Benali",
    "startTime":       "2025-08-15T09:00:00",
    "endTime":         "2025-08-15T09:45:00",
    "serviceLabel":    "Teeth Cleaning",
    "durationMinutes": 45,
    "status":          "PENDING"
  }
]
```

---

## STEP 13 — Weekly Schedule (Calendar View)

**GET** `http://localhost:8080/api/appointments/dentist/2/weekly?weekStart=2025-08-11`

**Headers:**
```
Authorization: Bearer <token>
```

> `weekStart` = Monday of the week. Returns all appointments Mon–Sun for that dentist.

---

## STEP 14 — Update an Appointment

**PUT** `http://localhost:8080/api/appointments/1`

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Body (reschedule to 11:00, change service to ROOT_CANAL):**
```json
{
  "patientId":   1,
  "dentistId":   2,
  "startTime":   "2025-08-15T11:00:00",
  "serviceType": "ROOT_CANAL",
  "notes":       "Rescheduled — patient request"
}
```

**Expected Response — 200:**
```json
{
  "id":              1,
  "startTime":       "2025-08-15T11:00:00",
  "endTime":         "2025-08-15T12:30:00",
  "serviceType":     "ROOT_CANAL",
  "serviceLabel":    "Root Canal Treatment",
  "durationMinutes": 90,
  "status":          "PENDING",
  "notes":           "Rescheduled — patient request"
}
```

---

## STEP 15 — Update Appointment Status

**PATCH** `http://localhost:8080/api/appointments/1/status?status=CONFIRMED`

**Headers:**
```
Authorization: Bearer <token>
```

**Expected Response — 200:**
```json
{
  "id":     1,
  "status": "CONFIRMED"
}
```

> Valid status values: `PENDING` `CONFIRMED` `CANCELLED` `COMPLETED`

---

## STEP 16 — Cancel an Appointment

**PATCH** `http://localhost:8080/api/appointments/1/cancel`

**Headers:**
```
Authorization: Bearer <token>
```

**Expected Response — 204 No Content**

---

## STEP 17 — Delete an Appointment

**DELETE** `http://localhost:8080/api/appointments/1`

**Headers:**
```
Authorization: Bearer <token>
```

**Expected Response — 204 No Content**

---

## Quick Reference Table

| # | Method   | Endpoint                                    | Description                  |
|---|----------|---------------------------------------------|------------------------------|
| 1 | POST     | /api/auth/register                          | Register patient or dentist  |
| 2 | POST     | /api/auth/login                             | Login and get JWT token      |
| 3 | GET      | /api/appointments/services                  | List all dental services     |
| 4 | GET      | /api/appointments/dentist/{id}/slots        | Get available time slots     |
| 5 | POST     | /api/appointments                           | Book an appointment          |
| 6 | GET      | /api/appointments/{id}                      | Get appointment by ID        |
| 7 | GET      | /api/appointments/patient/{id}              | All appointments for patient |
| 8 | GET      | /api/appointments/dentist/{id}              | All appointments for dentist |
| 9 | GET      | /api/appointments/dentist/{id}/daily        | Daily calendar view          |
|10 | GET      | /api/appointments/dentist/{id}/weekly       | Weekly calendar view         |
|11 | PUT      | /api/appointments/{id}                      | Update appointment           |
|12 | PATCH    | /api/appointments/{id}/status               | Change status                |
|13 | PATCH    | /api/appointments/{id}/cancel               | Cancel appointment           |
|14 | DELETE   | /api/appointments/{id}                      | Delete appointment           |

---

## Service Durations Reference

| Service Type         | Label                  | Duration |
|----------------------|------------------------|----------|
| CONSULTATION         | Consultation           | 30 min   |
| CLEANING             | Teeth Cleaning         | 45 min   |
| FILLING              | Dental Filling         | 60 min   |
| EXTRACTION           | Tooth Extraction       | 45 min   |
| ROOT_CANAL           | Root Canal Treatment   | 90 min   |
| CROWN                | Crown Placement        | 75 min   |
| WHITENING            | Teeth Whitening        | 60 min   |
| ORTHODONTIC_CHECKUP  | Orthodontic Check-up   | 30 min   |
| IMPLANT_CONSULTATION | Implant Consultation   | 45 min   |
| EMERGENCY            | Emergency Care         | 30 min   |

---

## Clinic Rules

- Working hours: **09:00 – 18:00**
- Working days: **Monday – Saturday**
- **Sunday**: closed — slot requests return `[]`
- No overlapping appointments per dentist
- Slot grid: every **15 minutes**
- Cannot cancel a `COMPLETED` appointment
- Cannot edit a `COMPLETED` or `CANCELLED` appointment
