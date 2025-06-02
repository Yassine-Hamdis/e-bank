import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { AdminService } from '../../services/admin.service';
import { User, Currency, CreateCurrencyRequest } from '../../models/auth.model';
import { AdminSidebarComponent } from '../admin-sidebar/admin-sidebar.component';

@Component({
  selector: 'app-currency-management',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule, AdminSidebarComponent],
  templateUrl: './currency-management.component.html',
  styleUrls: ['./currency-management.component.css']
})
export class CurrencyManagementComponent implements OnInit {
  user: User | null = null;
  currencies: Currency[] = [];
  isLoading = true;
  error: string | null = null;
  
  // Create Currency Modal
  showCreateModal = false;
  createCurrencyForm: FormGroup;
  isCreating = false;
  createError: string | null = null;
  
  // Delete Currency Modal
  showDeleteModal = false;
  selectedCurrency: Currency | null = null;
  isDeleting = false;
  deleteError: string | null = null;
  
  // Refresh Binance
  isRefreshing = false;
  refreshMessage: string | null = null;

  constructor(
    private authService: AuthService,
    private adminService: AdminService,
    private router: Router,
    private formBuilder: FormBuilder
  ) {
    this.createCurrencyForm = this.formBuilder.group({
      symbol: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(10)]],
      name: ['', [Validators.required, Validators.minLength(2)]],
      currentPrice: [0, [Validators.required, Validators.min(0)]],
      priceChange24h: [0, [Validators.required]],
      marketCap: [null],
      volume24h: [0, [Validators.required, Validators.min(0)]],
      isActive: [true, [Validators.required]]
    });
  }

  ngOnInit(): void {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    if (!this.authService.isAdmin()) {
      this.router.navigate(['/dashboard']);
      return;
    }
    
    this.user = this.authService.getCurrentUser();
    this.loadCurrencies();
  }

  loadCurrencies(): void {
    this.isLoading = true;
    this.error = null;

    this.adminService.getAllCurrencies().subscribe({
      next: (response) => {
        this.currencies = response.currencies;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading currencies:', error);
        this.error = 'Failed to load currencies. Please try again.';
        this.isLoading = false;
      }
    });
  }

  refreshBinanceData(): void {
    this.isRefreshing = true;
    this.refreshMessage = null;

    this.adminService.refreshBinanceData().subscribe({
      next: (response) => {
        this.refreshMessage = response.message;
        this.loadCurrencies(); // Reload currencies after refresh
        this.isRefreshing = false;
        
        // Clear message after 5 seconds
        setTimeout(() => {
          this.refreshMessage = null;
        }, 5000);
      },
      error: (error) => {
        console.error('Error refreshing Binance data:', error);
        this.refreshMessage = 'Failed to refresh Binance data. Please try again.';
        this.isRefreshing = false;
        
        // Clear message after 5 seconds
        setTimeout(() => {
          this.refreshMessage = null;
        }, 5000);
      }
    });
  }

  openCreateModal(): void {
    this.showCreateModal = true;
    this.createCurrencyForm.reset();
    this.createCurrencyForm.patchValue({ isActive: true });
    this.createError = null;
  }

  closeCreateModal(): void {
    this.showCreateModal = false;
    this.createCurrencyForm.reset();
    this.createError = null;
  }

  createCurrency(): void {
    if (this.createCurrencyForm.valid) {
      this.isCreating = true;
      this.createError = null;

      const currencyData: CreateCurrencyRequest = this.createCurrencyForm.value;

      this.adminService.createCurrency(currencyData).subscribe({
        next: (response) => {
          this.currencies.push(response.currency);
          this.isCreating = false;
          this.closeCreateModal();
        },
        error: (error) => {
          console.error('Error creating currency:', error);
          this.createError = error.error?.message || 'Failed to create currency. Please try again.';
          this.isCreating = false;
        }
      });
    } else {
      this.markFormGroupTouched();
    }
  }

  toggleCurrencyStatus(currency: Currency): void {
    const newStatus = !currency.isActive;
    
    this.adminService.updateCurrencyStatus(currency.symbol, newStatus).subscribe({
      next: (updatedCurrency) => {
        const index = this.currencies.findIndex(c => c.id === currency.id);
        if (index !== -1) {
          this.currencies[index] = updatedCurrency;
        }
      },
      error: (error) => {
        console.error('Error updating currency status:', error);
        this.error = 'Failed to update currency status. Please try again.';
      }
    });
  }

  openDeleteModal(currency: Currency): void {
    this.selectedCurrency = currency;
    this.showDeleteModal = true;
    this.deleteError = null;
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.selectedCurrency = null;
    this.deleteError = null;
  }

  confirmDelete(): void {
    if (this.selectedCurrency) {
      this.isDeleting = true;
      this.deleteError = null;

      this.adminService.deleteCurrency(this.selectedCurrency.symbol).subscribe({
        next: () => {
          this.currencies = this.currencies.filter(c => c.id !== this.selectedCurrency!.id);
          this.isDeleting = false;
          this.closeDeleteModal();
        },
        error: (error) => {
          console.error('Error deleting currency:', error);
          this.deleteError = error.error?.message || 'Failed to delete currency. Please try again.';
          this.isDeleting = false;
        }
      });
    }
  }

  private markFormGroupTouched(): void {
    Object.keys(this.createCurrencyForm.controls).forEach(key => {
      this.createCurrencyForm.get(key)?.markAsTouched();
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  // Helper methods for template
  formatPrice(price: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 2,
      maximumFractionDigits: 8
    }).format(price);
  }

  formatVolume(volume: number): string {
    if (volume >= 1e9) {
      return (volume / 1e9).toFixed(2) + 'B';
    } else if (volume >= 1e6) {
      return (volume / 1e6).toFixed(2) + 'M';
    } else if (volume >= 1e3) {
      return (volume / 1e3).toFixed(2) + 'K';
    }
    return volume.toFixed(2);
  }

  formatMarketCap(marketCap: number | null): string {
    if (!marketCap) return 'N/A';
    return this.formatVolume(marketCap);
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleString();
  }
}
