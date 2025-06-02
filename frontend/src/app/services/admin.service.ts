import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AgentStatistics, CurrencyStatistics, Agent, CreateAgentRequest, UpdateAgentStatusRequest, CurrenciesResponse, RefreshBinanceResponse, CreateCurrencyRequest, CreateCurrencyResponse, Currency } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private readonly API_URL = 'http://localhost:8081/api/v1';

  constructor(private http: HttpClient) {}

  /**
   * Get agent statistics from the API
   * GET /api/v1/admin/agents/statistics
   */
  getAgentStatistics(): Observable<AgentStatistics> {
    return this.http.get<AgentStatistics>(`${this.API_URL}/admin/agents/statistics`)
      .pipe(
        catchError(error => {
          console.error('Error fetching agent statistics:', error);
          return throwError(() => error);
        })
      );
  }

  /**
   * Get currency statistics from the API
   * GET /api/v1/admin/currencies/statistics
   */
  getCurrencyStatistics(): Observable<CurrencyStatistics> {
    return this.http.get<CurrencyStatistics>(`${this.API_URL}/admin/currencies/statistics`)
      .pipe(
        catchError(error => {
          console.error('Error fetching currency statistics:', error);
          return throwError(() => error);
        })
      );
  }

  // Agent Management Methods

  /**
   * Get all agents
   * GET /api/v1/admin/agents
   */
  getAllAgents(): Observable<Agent[]> {
    return this.http.get<Agent[]>(`${this.API_URL}/admin/agents`)
      .pipe(
        catchError(error => {
          console.error('Error fetching agents:', error);
          return throwError(() => error);
        })
      );
  }

  /**
   * Create a new agent
   * POST /api/v1/admin/agents
   */
  createAgent(agentData: CreateAgentRequest): Observable<Agent> {
    return this.http.post<Agent>(`${this.API_URL}/admin/agents`, agentData)
      .pipe(
        catchError(error => {
          console.error('Error creating agent:', error);
          return throwError(() => error);
        })
      );
  }

  /**
   * Update agent status
   * PUT /api/v1/admin/agents/{id}/status?status={status}
   */
  updateAgentStatus(agentId: number, status: 'ACTIVE' | 'INACTIVE'): Observable<Agent> {
    return this.http.put<Agent>(`${this.API_URL}/admin/agents/${agentId}/status?status=${status}`, {})
      .pipe(
        catchError(error => {
          console.error('Error updating agent status:', error);
          return throwError(() => error);
        })
      );
  }

  /**
   * Delete an agent
   * DELETE /api/v1/admin/agents/{id}
   */
  deleteAgent(agentId: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/admin/agents/${agentId}`)
      .pipe(
        catchError(error => {
          console.error('Error deleting agent:', error);
          return throwError(() => error);
        })
      );
  }

  // Currency Management Methods

  /**
   * Get all currencies
   * GET /api/v1/admin/currencies
   */
  getAllCurrencies(): Observable<CurrenciesResponse> {
    return this.http.get<CurrenciesResponse>(`${this.API_URL}/admin/currencies`)
      .pipe(
        catchError(error => {
          console.error('Error fetching currencies:', error);
          return throwError(() => error);
        })
      );
  }

  /**
   * Refresh Binance data
   * POST /api/v1/admin/currencies/refresh
   */
  refreshBinanceData(): Observable<RefreshBinanceResponse> {
    return this.http.post<RefreshBinanceResponse>(`${this.API_URL}/admin/currencies/refresh`, {})
      .pipe(
        catchError(error => {
          console.error('Error refreshing Binance data:', error);
          return throwError(() => error);
        })
      );
  }

  /**
   * Add custom currency
   * POST /api/v1/admin/currencies
   */
  createCurrency(currencyData: CreateCurrencyRequest): Observable<CreateCurrencyResponse> {
    return this.http.post<CreateCurrencyResponse>(`${this.API_URL}/admin/currencies`, currencyData)
      .pipe(
        catchError(error => {
          console.error('Error creating currency:', error);
          return throwError(() => error);
        })
      );
  }

  /**
   * Update currency status
   * PUT /api/v1/admin/currencies/{symbol}/status?isActive={isActive}
   */
  updateCurrencyStatus(symbol: string, isActive: boolean): Observable<Currency> {
    return this.http.put<Currency>(`${this.API_URL}/admin/currencies/${symbol}/status?isActive=${isActive}`, {})
      .pipe(
        catchError(error => {
          console.error('Error updating currency status:', error);
          return throwError(() => error);
        })
      );
  }

  /**
   * Delete currency
   * DELETE /api/v1/admin/currencies/{symbol}
   */
  deleteCurrency(symbol: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/admin/currencies/${symbol}`)
      .pipe(
        catchError(error => {
          console.error('Error deleting currency:', error);
          return throwError(() => error);
        })
      );
  }
}
