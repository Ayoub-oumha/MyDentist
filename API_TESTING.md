# MyDentist2 — API Reference & Angular Integration Guide

## Base URL
```
http://localhost:8080
```

---

## Angular Setup

### 1. Environment
```typescript
// src/environments/environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

---

### 2. Models (TypeScript interfaces)
```typescript
// src/app/models/auth.model.ts
export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  role: 'PATIENT' | 'DENTIST';
  phone?: string;
  address?: string;
  specialty?: string;
  latitude?: number;
  longitude?: number;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  id: number;
  name: string;
  email: string;
  role: 'PATIENT' | 'DENTIST';
}
```

```typescript
// src/app/models/patient.model.ts
export interface PatientRequest {
  name: string;
  email: string;
  phone?: string;
  address?: string;
}

export interface PatientResponse {
  id: number;
  name: string;
  email: string;
  phone: string;
  address: string;
}
```

```typescript
// src/app/models/dentist.model.ts
export interface DentistRequest {
  name: string;
  email: string;
  phone?: string;
  specialty?: string;
  address?: string;
  latitude?: number;
  longitude?: number;
}

export interface DentistResponse {
  id: number;
  name: string;
  email: string;
  phone: string;
  specialty: string;
  address: string;
  latitude: number;
  longitude: number;
}
```

```typescript
// src/app/models/appointment.model.ts
export type AppointmentStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED';

export interface AppointmentRequest {
  patientId: number;
  dentistId: number;
  dateTime: string; // ISO format: "2025-09-15T10:00:00"
  notes?: string;
}

export interface AppointmentResponse {
  id: number;
  patientId: number;
  patientName: string;
  dentistId: number;
  dentistName: string;
  dateTime: string;
  status: AppointmentStatus;
  notes: string;
}
```

```typescript
// src/app/models/payment.model.ts
export type PaymentStatus = 'PENDING' | 'COMPLETED' | 'REFUNDED' | 'FAILED';

export interface PaymentRequest {
  appointmentId: number;
  amount: number;
  stripePaymentId: string;
}

export interface PaymentResponse {
  id: number;
  appointmentId: number;
  amount: number;
  status: PaymentStatus;
  stripePaymentId: string;
  paidAt: string;
}
```

```typescript
// src/app/models/document.model.ts
export type DocumentType = 'XRAY' | 'ANALYSIS' | 'PRESCRIPTION' | 'OTHER';

export interface DocumentRequest {
  patientId: number;
  uploadedById: number;
  fileName: string;
  s3Key: string;
  type: DocumentType;
}

export interface DocumentResponse {
  id: number;
  patientId: number;
  patientName: string;
  uploadedById: number;
  uploadedByName: string;
  fileName: string;
  s3Key: string;
  type: DocumentType;
  uploadedAt: string;
}
```

---

### 3. JWT Interceptor
```typescript
// src/app/interceptors/auth.interceptor.ts
import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = inject(AuthService).getToken();
  if (token) {
    req = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
  }
  return next(req);
};
```

Register it in `app.config.ts`:
```typescript
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './interceptors/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(withInterceptors([authInterceptor]))
  ]
};
```

---

### 4. Auth Service
```typescript
// src/app/services/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private url = `${environment.apiUrl}/auth`;

  constructor(private http: HttpClient) {}

  register(request: RegisterRequest) {
    return this.http.post<AuthResponse>(`${this.url}/register`, request).pipe(
      tap(res => this.saveSession(res))
    );
  }

  login(request: LoginRequest) {
    return this.http.post<AuthResponse>(`${this.url}/login`, request).pipe(
      tap(res => this.saveSession(res))
    );
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getUser(): AuthResponse | null {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  private saveSession(res: AuthResponse) {
    localStorage.setItem('token', res.token);
    localStorage.setItem('user', JSON.stringify(res));
  }
}
```

---

### 5. Patient Service
```typescript
// src/app/services/patient.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { PatientRequest, PatientResponse } from '../models/patient.model';

@Injectable({ providedIn: 'root' })
export class PatientService {
  private url = `${environment.apiUrl}/patients`;

  constructor(private http: HttpClient) {}

  getAll()                              { return this.http.get<PatientResponse[]>(this.url); }
  getById(id: number)                   { return this.http.get<PatientResponse>(`${this.url}/${id}`); }
  update(id: number, req: PatientRequest) { return this.http.put<PatientResponse>(`${this.url}/${id}`, req); }
  delete(id: number)                    { return this.http.delete<void>(`${this.url}/${id}`); }
}
```

---

### 6. Dentist Service
```typescript
// src/app/services/dentist.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { DentistRequest, DentistResponse } from '../models/dentist.model';

@Injectable({ providedIn: 'root' })
export class DentistService {
  private url = `${environment.apiUrl}/dentists`;

  constructor(private http: HttpClient) {}

  getAll()                                { return this.http.get<DentistResponse[]>(this.url); }
  getById(id: number)                     { return this.http.get<DentistResponse>(`${this.url}/${id}`); }
  getBySpecialty(specialty: string)       { return this.http.get<DentistResponse[]>(`${this.url}/specialty/${specialty}`); }
  update(id: number, req: DentistRequest) { return this.http.put<DentistResponse>(`${this.url}/${id}`, req); }
  delete(id: number)                      { return this.http.delete<void>(`${this.url}/${id}`); }
}
```

---

### 7. Appointment Service
```typescript
// src/app/services/appointment.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { AppointmentRequest, AppointmentResponse, AppointmentStatus } from '../models/appointment.model';

@Injectable({ providedIn: 'root' })
export class AppointmentService {
  private url = `${environment.apiUrl}/appointments`;

  constructor(private http: HttpClient) {}

  create(req: AppointmentRequest)             { return this.http.post<AppointmentResponse>(this.url, req); }
  getById(id: number)                         { return this.http.get<AppointmentResponse>(`${this.url}/${id}`); }
  getByPatient(patientId: number)             { return this.http.get<AppointmentResponse[]>(`${this.url}/patient/${patientId}`); }
  getByDentist(dentistId: number)             { return this.http.get<AppointmentResponse[]>(`${this.url}/dentist/${dentistId}`); }
  update(id: number, req: AppointmentRequest) { return this.http.put<AppointmentResponse>(`${this.url}/${id}`, req); }
  updateStatus(id: number, status: AppointmentStatus) {
    return this.http.patch<AppointmentResponse>(`${this.url}/${id}/status?status=${status}`, {});
  }
  cancel(id: number)                          { return this.http.patch<void>(`${this.url}/${id}/cancel`, {}); }
}
```

---

### 8. Payment Service
```typescript
// src/app/services/payment.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { PaymentRequest, PaymentResponse } from '../models/payment.model';

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private url = `${environment.apiUrl}/payments`;

  constructor(private http: HttpClient) {}

  create(req: PaymentRequest)         { return this.http.post<PaymentResponse>(this.url, req); }
  getAll()                            { return this.http.get<PaymentResponse[]>(this.url); }
  getById(id: number)                 { return this.http.get<PaymentResponse>(`${this.url}/${id}`); }
  getByAppointment(appointmentId: number) { return this.http.get<PaymentResponse>(`${this.url}/appointment/${appointmentId}`); }
  refund(id: number)                  { return this.http.patch<PaymentResponse>(`${this.url}/${id}/refund`, {}); }
}
```

---

### 9. Document Service
```typescript
// src/app/services/document.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { DocumentRequest, DocumentResponse, DocumentType } from '../models/document.model';

@Injectable({ providedIn: 'root' })
export class DocumentService {
  private url = `${environment.apiUrl}/documents`;

  constructor(private http: HttpClient) {}

  create(req: DocumentRequest)                        { return this.http.post<DocumentResponse>(this.url, req); }
  getById(id: number)                                 { return this.http.get<DocumentResponse>(`${this.url}/${id}`); }
  getByPatient(patientId: number)                     { return this.http.get<DocumentResponse[]>(`${this.url}/patient/${patientId}`); }
  getByPatientAndType(patientId: number, type: DocumentType) {
    return this.http.get<DocumentResponse[]>(`${this.url}/patient/${patientId}/type/${type}`);
  }
  delete(id: number)                                  { return this.http.delete<void>(`${this.url}/${id}`); }
}
```

---

### 10. Auth Guard
```typescript
// src/app/guards/auth.guard.ts
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (auth.isLoggedIn()) return true;
  return router.createUrlTree(['/login']);
};
```

```typescript
// src/app/guards/role.guard.ts
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const roleGuard = (role: 'PATIENT' | 'DENTIST'): CanActivateFn => () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (auth.getUser()?.role === role) return true;
  return router.createUrlTree(['/unauthorized']);
};
```

Usage in routes:
```typescript
{ path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
{ path: 'admin',     component: AdminComponent,     canActivate: [() => roleGuard('DENTIST')()] }
```

---

## API Endpoints Quick Reference

### AUTH — `/api/auth`
| Method | Endpoint | Body | Auth |
|--------|----------|------|------|
| POST | `/register` | `RegisterRequest` | ❌ |
| POST | `/login` | `LoginRequest` | ❌ |

### PATIENTS — `/api/patients`
| Method | Endpoint | Body | Auth |
|--------|----------|------|------|
| GET | `/` | — | ✅ |
| GET | `/{id}` | — | ✅ |
| PUT | `/{id}` | `PatientRequest` | ✅ |
| DELETE | `/{id}` | — | ✅ |

### DENTISTS — `/api/dentists`
| Method | Endpoint | Body | Auth |
|--------|----------|------|------|
| GET | `/` | — | ✅ |
| GET | `/{id}` | — | ✅ |
| GET | `/specialty/{specialty}` | — | ✅ |
| PUT | `/{id}` | `DentistRequest` | ✅ |
| DELETE | `/{id}` | — | ✅ |

### APPOINTMENTS — `/api/appointments`
| Method | Endpoint | Body | Auth |
|--------|----------|------|------|
| POST | `/` | `AppointmentRequest` | ✅ |
| GET | `/{id}` | — | ✅ |
| GET | `/patient/{patientId}` | — | ✅ |
| GET | `/dentist/{dentistId}` | — | ✅ |
| PUT | `/{id}` | `AppointmentRequest` | ✅ |
| PATCH | `/{id}/status?status=CONFIRMED` | — | ✅ |
| PATCH | `/{id}/cancel` | — | ✅ |

### PAYMENTS — `/api/payments`
| Method | Endpoint | Body | Auth |
|--------|----------|------|------|
| POST | `/` | `PaymentRequest` | ✅ |
| GET | `/` | — | ✅ |
| GET | `/{id}` | — | ✅ |
| GET | `/appointment/{appointmentId}` | — | ✅ |
| PATCH | `/{id}/refund` | — | ✅ |

### DOCUMENTS — `/api/documents`
| Method | Endpoint | Body | Auth |
|--------|----------|------|------|
| POST | `/` | `DocumentRequest` | ✅ |
| GET | `/{id}` | — | ✅ |
| GET | `/patient/{patientId}` | — | ✅ |
| GET | `/patient/{patientId}/type/{type}` | — | ✅ |
| DELETE | `/{id}` | — | ✅ |

---

## Error Response Format
```typescript
// All errors follow this shape
interface ErrorResponse {
  status: number;
  message: string;
  errors: Record<string, string> | null; // field-level validation errors
  timestamp: string;
}
```

Handle errors in Angular:
```typescript
import { HttpErrorResponse } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

this.authService.login(form.value).pipe(
  catchError((err: HttpErrorResponse) => {
    console.error(err.error.message);
    return throwError(() => err);
  })
).subscribe(res => {
  // res.token, res.role, res.id available here
});
```

---

## HTTP Status Codes
| Code | Meaning |
|------|---------|
| `200` | OK |
| `201` | Created |
| `204` | No Content (delete / cancel) |
| `400` | Bad Request / Validation failed |
| `401` | Unauthorized — missing or invalid token |
| `404` | Resource not found |

---

## CORS — Enable on Backend
Add this to `SecurityConfig` if Angular runs on a different port (e.g. `4200`):
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:4200"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    UrlPathMatcher source = new UrlPathMatcher();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```
And add `.cors(cors -> cors.configurationSource(corsConfigurationSource()))` inside `securityFilterChain`.
