import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { GlobalStatisticsResponse } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class GlobalStatsService {
  private readonly API_URL = 'http://localhost:8081/api/stats';

  constructor(private http: HttpClient) {}

  /**
   * Get global application statistics
   * GET /api/stats/global
   */
  getGlobalStatistics(): Observable<GlobalStatisticsResponse> {
    return this.http.get<GlobalStatisticsResponse>(`${this.API_URL}/global`)
      .pipe(
        catchError(error => {
          console.error('Error fetching global statistics:', error);
          return throwError(() => error);
        })
      );
  }
}
