import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { NotificationBellComponent } from '../notification-bell/notification-bell.component';
import { ClientSidebarComponent } from '../client-sidebar/client-sidebar.component';

import { AuthService } from '../../services/auth.service';
import { ClientService } from '../../services/client.service';
import { ClientDashboardData, CryptoTransferRequest } from '../../models/client.model';

@Component({
  selector: 'app-client-dashboard',
  imports: [CommonModule, FormsModule, NotificationBellComponent, ClientSidebarComponent],
  templateUrl: './client-dashboard.component.html',
  styleUrl: './client-dashboard.component.css'
})
export class ClientDashboardComponent implements OnInit {
  dashboardData: ClientDashboardData | null = null;
  isLoading = true;
  errorMessage = '';

  // Sensitive information visibility toggles
  showBalance = false;
  showClientId = false;
  showIdentificationNumber = false;
  showWalletAddress = false;
  showAccountId = false;
  showNationalId = false;

  // Crypto Transfer Modal
  showCryptoTransferModal = false;
  isSubmittingCryptoTransfer = false;
  cryptoTransferError = '';
  cryptoTransferSuccess = '';
  cryptoTransfer: CryptoTransferRequest = {
    recipientWalletAddress: '',
    cryptoType: '',
    cryptoAmount: 0,
    networkFee: 0.001,
    description: ''
  };

  constructor(
    private authService: AuthService,
    private clientService: ClientService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (!this.authService.isAuthenticated() || !this.authService.isClient()) {
      this.router.navigate(['/login']);
      return;
    }

    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.clientService.getDashboardData().subscribe({
      next: (data) => {
        this.dashboardData = data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading dashboard data:', error);
        this.errorMessage = 'Failed to load dashboard data. Please try again.';
        this.isLoading = false;
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  // Toggle methods for sensitive information
  toggleBalance(): void {
    this.showBalance = !this.showBalance;
  }

  toggleClientId(): void {
    this.showClientId = !this.showClientId;
  }

  toggleIdentificationNumber(): void {
    this.showIdentificationNumber = !this.showIdentificationNumber;
  }

  toggleWalletAddress(): void {
    this.showWalletAddress = !this.showWalletAddress;
  }

  toggleAccountId(): void {
    this.showAccountId = !this.showAccountId;
  }

  toggleNationalId(): void {
    this.showNationalId = !this.showNationalId;
  }

  // Helper method to mask sensitive data
  maskSensitiveData(data: string, visibleChars: number = 4): string {
    if (!data || data.length <= visibleChars) return '••••••••';
    return data.substring(0, visibleChars) + '••••••••';
  }

  getCryptoKeys(): string[] {
    return this.dashboardData?.cryptoWallet.totalValue ?
      Object.keys(this.dashboardData.cryptoWallet.totalValue) : [];
  }

  formatCurrency(amount: number, currency: string = 'MAD'): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency === 'MAD' ? 'USD' : currency,
      minimumFractionDigits: 2
    }).format(amount);
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  navigateToTransfer(): void {
    this.router.navigate(['/client/transfer']);
  }

  navigateToMobileRecharge(): void {
    this.router.navigate(['/client/mobile-recharge']);
  }

  navigateToCryptoWallet(): void {
    this.router.navigate(['/client/crypto-wallet']);
  }

  // Crypto Transfer Modal Methods
  openCryptoTransferModal(): void {
    this.showCryptoTransferModal = true;
    this.resetCryptoTransferForm();
  }

  closeCryptoTransferModal(): void {
    this.showCryptoTransferModal = false;
    this.resetCryptoTransferForm();
  }

  resetCryptoTransferForm(): void {
    this.cryptoTransfer = {
      recipientWalletAddress: '',
      cryptoType: '',
      cryptoAmount: 0,
      networkFee: 0.001,
      description: ''
    };
    this.cryptoTransferError = '';
    this.cryptoTransferSuccess = '';
    this.isSubmittingCryptoTransfer = false;
  }

  submitCryptoTransfer(): void {
    if (this.isSubmittingCryptoTransfer) return;

    this.isSubmittingCryptoTransfer = true;
    this.cryptoTransferError = '';
    this.cryptoTransferSuccess = '';

    this.clientService.transferCrypto(this.cryptoTransfer).subscribe({
      next: (response) => {
        this.isSubmittingCryptoTransfer = false;
        // Display API response message if available, otherwise use default
        this.cryptoTransferSuccess = response.message || `Crypto transfer initiated successfully! Transaction ID: ${response.transactionId}`;

        // Auto-close modal after 3 seconds
        setTimeout(() => {
          this.closeCryptoTransferModal();
        }, 3000);
      },
      error: (error) => {
        this.isSubmittingCryptoTransfer = false;
        console.error('Crypto transfer failed:', error);

        // Display API error message if available, otherwise use status-based messages
        if (error.error && error.error.message) {
          this.cryptoTransferError = error.error.message;
        } else if (error.status === 400) {
          this.cryptoTransferError = 'Invalid transfer details. Please check your input.';
        } else if (error.status === 403) {
          this.cryptoTransferError = 'Insufficient crypto balance or wallet restrictions.';
        } else if (error.status === 404) {
          this.cryptoTransferError = 'Recipient wallet address not found.';
        } else {
          this.cryptoTransferError = 'Crypto transfer failed. Please try again later.';
        }
      }
    });
  }
}
