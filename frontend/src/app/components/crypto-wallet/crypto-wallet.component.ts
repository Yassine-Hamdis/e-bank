import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClientService } from '../../services/client.service';
import {
  CryptoWalletDetails,
  CryptoBalanceDetail,
  CryptoRates,
  CryptoBuyRequest,
  CryptoBuyFromMainRequest,
  CryptoSellRequest,
  CryptoTransaction,
  WalletAddressUpdateRequest
} from '../../models/client.model';

@Component({
  selector: 'app-crypto-wallet',
  imports: [CommonModule, FormsModule],
  templateUrl: './crypto-wallet.component.html',
  styleUrl: './crypto-wallet.component.css'
})
export class CryptoWalletComponent implements OnInit {
  walletDetails: CryptoWalletDetails | null = null;
  cryptoRates: CryptoRates | null = null;
  transactions: CryptoTransaction[] = [];
  isLoading = true;
  errorMessage = '';
  successMessage = '';

  // Trading form data
  selectedCrypto = 'BTC';
  tradeAmount = 0;
  tradeType: 'buy' | 'sell' | 'buy-from-main' = 'buy';
  walletAddress = '';
  description = '';
  platformFee = 10.00;
  useRealTimeRate = true;

  // UI state
  activeTab: 'trade' | 'transactions' = 'trade';
  isTrading = false;

  constructor(private clientService: ClientService) {}

  ngOnInit(): void {
    this.loadCryptoData();
  }

  loadCryptoData(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    // Load wallet details, rates, and transactions
    Promise.all([
      this.clientService.getCryptoWalletDetails().toPromise(),
      this.clientService.getCryptoRates().toPromise(),
      this.clientService.getCryptoTransactions().toPromise()
    ]).then(([wallet, rates, transactions]) => {
      this.walletDetails = wallet!;
      this.cryptoRates = rates!;
      this.transactions = transactions!;
      this.isLoading = false;
    }).catch(error => {
      console.error('Error loading crypto data:', error);
      this.errorMessage = error.error?.message || 'Failed to load crypto data. Please try again.';
      this.isLoading = false;
    });
  }

  setActiveTab(tab: 'trade' | 'transactions'): void {
    this.activeTab = tab;
  }

  formatCurrency(amount: number, currency: string = 'MAD'): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency === 'MAD' ? 'USD' : currency,
      minimumFractionDigits: 2
    }).format(amount);
  }

  formatCrypto(amount: number | undefined, crypto: string): string {
    if (amount === undefined || amount === null) {
      return `0.00000000 ${crypto}`;
    }
    return `${amount.toFixed(8)} ${crypto}`;
  }

  formatDate(dateString: string | number): string {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  getCryptoRate(crypto: string): number {
    if (!this.cryptoRates) return 0;
    return crypto === 'BTC' ? this.cryptoRates.BTC :
           crypto === 'ETH' ? this.cryptoRates.ETH :
           this.cryptoRates.USDT;
  }

  getCryptoBalance(crypto: string): number {
    if (!this.walletDetails?.cryptoBalances) return 0;
    return this.walletDetails.cryptoBalances[crypto] || 0;
  }

  getCryptoValueInMAD(crypto: string): number {
    const balanceDetail = this.walletDetails?.balanceDetails?.find(detail => detail.cryptoType === crypto);
    return balanceDetail?.valueInMAD || 0;
  }

  getActiveCryptos(): string[] {
    if (!this.walletDetails?.balanceDetails) return [];
    return this.walletDetails.balanceDetails.map(detail => detail.cryptoType);
  }

  getCryptoName(crypto: string): string {
    const cryptoNames: { [key: string]: string } = {
      'BTC': 'Bitcoin',
      'ETH': 'Ethereum',
      'USDT': 'Tether',
      'BNB': 'Binance Coin'
    };
    return cryptoNames[crypto] || crypto;
  }

  executeTrade(): void {
    if (!this.cryptoRates || !this.walletDetails) return;

    this.isTrading = true;
    this.errorMessage = '';
    this.successMessage = '';
    const rate = this.cryptoRates[this.selectedCrypto as keyof CryptoRates] as number;

    if (this.tradeType === 'buy') {
      const buyRequest: CryptoBuyRequest = {
        cryptoType: this.selectedCrypto,
        amount: this.tradeAmount,
        exchangeRate: rate,
        walletAddress: this.walletAddress,
        description: this.description || `Buy ${this.selectedCrypto}`
      };

      this.clientService.buyCrypto(buyRequest).subscribe({
        next: (response) => {
          console.log('Buy transaction successful:', response);
          // Display API response message if available
          this.successMessage = response.message || `Buy transaction successful! Transaction ID: ${response.transactionId}`;
          this.loadCryptoData();
          this.resetTradeForm();
          this.isTrading = false;
        },
        error: (error) => {
          console.error('Buy transaction failed:', error);
          // Display API error message if available
          this.errorMessage = error.error?.message || 'Buy transaction failed. Please try again.';
          this.isTrading = false;
        }
      });
    } else if (this.tradeType === 'buy-from-main') {
      const buyFromMainRequest: CryptoBuyFromMainRequest = {
        cryptoType: this.selectedCrypto,
        amount: this.tradeAmount,
        description: this.description || `Buy ${this.selectedCrypto} from main account`,
        platformFee: this.platformFee,
        useRealTimeRate: this.useRealTimeRate
      };

      this.clientService.buyCryptoFromMain(buyFromMainRequest).subscribe({
        next: (response) => {
          console.log('Buy from main transaction successful:', response);
          // Display API response message if available
          this.successMessage = response.message || `Buy from main transaction successful! Transaction ID: ${response.transactionId}`;
          this.loadCryptoData();
          this.resetTradeForm();
          this.isTrading = false;
        },
        error: (error) => {
          console.error('Buy from main transaction failed:', error);
          // Display API error message if available
          this.errorMessage = error.error?.message || 'Buy from main transaction failed. Please try again.';
          this.isTrading = false;
        }
      });
    } else {
      const cryptoAmount = this.tradeAmount / rate;
      const sellRequest: CryptoSellRequest = {
        cryptoType: this.selectedCrypto,
        cryptoAmount: cryptoAmount,
        exchangeRate: rate,
        walletAddress: this.walletAddress,
        description: this.description || `Sell ${this.selectedCrypto}`
      };

      this.clientService.sellCrypto(sellRequest).subscribe({
        next: (response) => {
          console.log('Sell transaction successful:', response);
          // Display API response message if available
          this.successMessage = response.message || `Sell transaction successful! Transaction ID: ${response.transactionId}`;
          this.loadCryptoData();
          this.resetTradeForm();
          this.isTrading = false;
        },
        error: (error) => {
          console.error('Sell transaction failed:', error);
          // Display API error message if available
          this.errorMessage = error.error?.message || 'Sell transaction failed. Please try again.';
          this.isTrading = false;
        }
      });
    }
  }

  resetTradeForm(): void {
    this.tradeAmount = 0;
    this.walletAddress = '';
    this.description = '';
    this.platformFee = 10.00;
    this.useRealTimeRate = true;
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'COMPLETED': return 'bg-green-100 text-green-800';
      case 'PENDING': return 'bg-yellow-100 text-yellow-800';
      case 'FAILED': return 'bg-red-100 text-red-800';
      case 'CANCELLED': return 'bg-gray-100 text-gray-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  }

  getTypeBadgeClass(type: string): string {
    return type === 'CRYPTO_BUY' ? 'bg-blue-100 text-blue-800' : 'bg-purple-100 text-purple-800';
  }
}
