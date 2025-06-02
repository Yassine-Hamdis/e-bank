import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import {
  Client,
  CreateClientRequest,
  CreateClientResponse,
  DepositRequest,
  DepositResponse,
  Transaction,
  VerifyTransactionRequest,
  VerifyTransactionResponse,
  DepositStatistics
} from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AgentService {
  private readonly API_URL = 'http://localhost:8081/api/agent';

  constructor(private http: HttpClient) {
    console.log('AgentService initialized with API_URL:', this.API_URL);
  }

  /**
   * Get all managed clients
   * GET /api/agent/clients
   */
  getManagedClients(): Observable<Client[]> {
    return this.http.get<Client[]>(`${this.API_URL}/clients`)
      .pipe(
        catchError(error => {
          console.error('Error fetching managed clients:', error);
          return throwError(() => error);
        })
      );
  }

  /**
   * Create a new client
   * POST /api/agent/clients
   */
  createClient(clientData: CreateClientRequest): Observable<CreateClientResponse> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    // Debug logging
    console.log('AgentService: Sending request to:', `${this.API_URL}/clients`);
    console.log('AgentService: Request headers:', headers);
    console.log('AgentService: Request body:', JSON.stringify(clientData, null, 2));

    return this.http.post<CreateClientResponse>(`${this.API_URL}/clients`, clientData, { headers })
      .pipe(
        catchError(error => {
          console.error('AgentService: Error creating client:', error);
          console.error('AgentService: Error status:', error.status);
          console.error('AgentService: Error URL:', error.url);
          return throwError(() => error);
        })
      );
  }

  /**
   * Delete a client
   * DELETE /api/agent/clients/{clientId}
   */
  deleteClient(clientId: string): Observable<any> {
    return this.http.delete(`${this.API_URL}/clients/${clientId}`)
      .pipe(
        catchError(error => {
          console.error('Error deleting client:', error);
          return throwError(() => error);
        })
      );
  }

  /**
   * Make a deposit for a client
   * POST /api/agent/deposit
   */
  makeDeposit(depositData: DepositRequest): Observable<DepositResponse> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    return this.http.post<DepositResponse>(`${this.API_URL}/deposit`, depositData, { headers })
      .pipe(
        catchError(error => {
          console.error('Error making deposit:', error);
          return throwError(() => error);
        })
      );
  }

  /**
   * Get all transactions
   * GET /api/agent/transactions
   */
  getAllTransactions(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.API_URL}/transactions`)
      .pipe(
        catchError(error => {
          console.error('Error fetching transactions:', error);
          return throwError(() => error);
        })
      );
  }

  /**
   * Get pending transactions
   * GET /api/agent/transactions/pending
   */
  getPendingTransactions(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.API_URL}/transactions/pending`)
      .pipe(
        catchError(error => {
          console.error('Error fetching pending transactions:', error);
          return throwError(() => error);
        })
      );
  }

  /**
   * Verify a transaction
   * POST /api/agent/transactions/{transactionId}/verify
   */
  verifyTransaction(transactionId: string, verificationData: VerifyTransactionRequest): Observable<VerifyTransactionResponse> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    console.log('AgentService: Verifying transaction:', transactionId);
    console.log('AgentService: Verification data:', JSON.stringify(verificationData, null, 2));

    return this.http.post<VerifyTransactionResponse>(`${this.API_URL}/transactions/${transactionId}/verify`, verificationData, { headers })
      .pipe(
        catchError(error => {
          console.error('AgentService: Error verifying transaction:', error);
          console.error('AgentService: Error status:', error.status);
          console.error('AgentService: Error URL:', error.url);
          return throwError(() => error);
        })
      );
  }

  /**
   * Get deposit statistics
   * GET /api/agent/deposit/statistics
   */
  getDepositStatistics(): Observable<DepositStatistics> {
    return this.http.get<DepositStatistics>(`${this.API_URL}/deposit/statistics`)
      .pipe(
        catchError(error => {
          console.error('Error fetching deposit statistics:', error);
          return throwError(() => error);
        })
      );
  }
}
