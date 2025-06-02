import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { GlobalSettingsResponse, UpdateGlobalSettingsRequest, UpdateFeePercentageRequest } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class SettingsService {
  private readonly API_URL = 'http://localhost:8081/api/settings';

  constructor(private http: HttpClient) {}

  /**
   * Get global settings from the API
   * GET /api/settings/global
   */
  getGlobalSettings(): Observable<GlobalSettingsResponse> {
    return this.http.get<GlobalSettingsResponse>(`${this.API_URL}/global`)
      .pipe(
        catchError(error => {
          console.error('Error fetching global settings:', error);
          return throwError(() => error);
        })
      );
  }

  /**
   * Update global settings
   * PUT /api/settings/global
   */
  updateGlobalSettings(settings: UpdateGlobalSettingsRequest): Observable<GlobalSettingsResponse> {
    return this.http.put<GlobalSettingsResponse>(`${this.API_URL}/global`, settings)
      .pipe(
        catchError(error => {
          console.error('Error updating global settings:', error);
          return throwError(() => error);
        })
      );
  }

  /**
   * Update fee percentage
   * PUT /api/settings/global/feePercentage
   */
  updateFeePercentage(feePercentage: UpdateFeePercentageRequest): Observable<any> {
    return this.http.put<any>(`${this.API_URL}/global/feePercentage`, feePercentage)
      .pipe(
        catchError(error => {
          console.error('Error updating fee percentage:', error);
          return throwError(() => error);
        })
      );
  }
}
