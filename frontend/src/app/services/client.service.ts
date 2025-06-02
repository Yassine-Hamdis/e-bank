import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, forkJoin } from 'rxjs';
import { map } from 'rxjs/operators';
import {
  ClientProfile,
  AccountDetails,
  AccountBalance,
  CryptoWallet,
  ClientDashboardData,
  TransferRequest,
  MobileRechargeRequest,
  TransactionResponse,
  ClientNotification,
  NotificationDeleteResponse,
  CryptoWalletDetails,
  CryptoBalanceDetail,
  CryptoRates,
  CryptoBuyRequest,
  CryptoBuyFromMainRequest,
  CryptoTransferRequest,
  CryptoSellRequest,
  CryptoTransactionResponse,
  CryptoTransaction,
  WalletAddressUpdateRequest,
  WalletAddressUpdateResponse
} from '../models/client.model';

@Injectable({
  providedIn: 'root'
})
export class ClientService {
  private readonly API_URL = 'http://localhost:8081/api/client';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json'
    });
  }

  // Get client profile
  getProfile(): Observable<ClientProfile> {
    return this.http.get<ClientProfile>(`${this.API_URL}/profile`, {
      headers: this.getHeaders()
    });
  }

  // Get account details
  getAccountDetails(): Observable<AccountDetails> {
    return this.http.get<AccountDetails>(`${this.API_URL}/account`, {
      headers: this.getHeaders()
    });
  }

  // Get account balance
  getAccountBalance(): Observable<AccountBalance> {
    return this.http.get<AccountBalance>(`${this.API_URL}/account/balance`, {
      headers: this.getHeaders()
    });
  }

  // Get crypto wallet details
  getCryptoWallet(): Observable<CryptoWallet> {
    return this.http.get<CryptoWallet>(`${this.API_URL}/crypto/wallet`, {
      headers: this.getHeaders()
    });
  }

  // Get all dashboard data in one call
  getDashboardData(): Observable<ClientDashboardData> {
    return forkJoin({
      profile: this.getProfile(),
      account: this.getAccountDetails(),
      balance: this.getAccountBalance(),
      cryptoWallet: this.getCryptoWallet()
    }).pipe(
      map(data => ({
        profile: data.profile,
        account: data.account,
        balance: data.balance,
        cryptoWallet: data.cryptoWallet
      }))
    );
  }

  // Transfer Operations
  makeTransfer(transferData: TransferRequest): Observable<TransactionResponse> {
    return this.http.post<TransactionResponse>(`${this.API_URL}/transfers`, transferData, {
      headers: this.getHeaders()
    });
  }

  // Mobile Recharge Operations
  mobileRecharge(rechargeData: MobileRechargeRequest): Observable<TransactionResponse> {
    return this.http.post<TransactionResponse>(`${this.API_URL}/mobile-recharge`, rechargeData, {
      headers: this.getHeaders()
    });
  }

  // Notification Operations
  getNotifications(): Observable<ClientNotification[]> {
    return this.http.get<ClientNotification[]>(`${this.API_URL}/notifications`, {
      headers: this.getHeaders()
    });
  }

  markNotificationAsRead(notificationId: string): Observable<ClientNotification> {
    return this.http.put<ClientNotification>(`${this.API_URL}/notifications/${notificationId}/read`, {}, {
      headers: this.getHeaders()
    });
  }

  deleteNotification(notificationId: string): Observable<NotificationDeleteResponse> {
    return this.http.delete<NotificationDeleteResponse>(`${this.API_URL}/notifications/${notificationId}`, {
      headers: this.getHeaders()
    });
  }

  // Crypto Trading Operations
  getCryptoWalletDetails(): Observable<CryptoWalletDetails> {
    return this.http.get<CryptoWalletDetails>(`${this.API_URL}/crypto/wallet`, {
      headers: this.getHeaders()
    });
  }

  getCryptoRates(): Observable<CryptoRates> {
    return this.http.get<CryptoRates>(`${this.API_URL}/crypto/rates`, {
      headers: this.getHeaders()
    });
  }

  buyCrypto(buyRequest: CryptoBuyRequest): Observable<CryptoTransactionResponse> {
    return this.http.post<CryptoTransactionResponse>(`${this.API_URL}/crypto/buy`, buyRequest, {
      headers: this.getHeaders()
    });
  }

  buyCryptoFromMain(buyRequest: CryptoBuyFromMainRequest): Observable<CryptoTransactionResponse> {
    return this.http.post<CryptoTransactionResponse>(`${this.API_URL}/crypto/buy-from-main`, buyRequest, {
      headers: this.getHeaders()
    });
  }

  transferCrypto(transferRequest: CryptoTransferRequest): Observable<CryptoTransactionResponse> {
    return this.http.post<CryptoTransactionResponse>(`${this.API_URL}/crypto/transfer`, transferRequest, {
      headers: this.getHeaders()
    });
  }

  sellCrypto(sellRequest: CryptoSellRequest): Observable<CryptoTransactionResponse> {
    return this.http.post<CryptoTransactionResponse>(`${this.API_URL}/crypto/sell`, sellRequest, {
      headers: this.getHeaders()
    });
  }

  getCryptoTransactions(): Observable<CryptoTransaction[]> {
    return this.http.get<CryptoTransaction[]>(`${this.API_URL}/crypto/transactions`, {
      headers: this.getHeaders()
    });
  }

  updateWalletAddress(updateRequest: WalletAddressUpdateRequest): Observable<WalletAddressUpdateResponse> {
    return this.http.put<WalletAddressUpdateResponse>(`${this.API_URL}/crypto/wallet/address`, updateRequest, {
      headers: this.getHeaders()
    });
  }
}
