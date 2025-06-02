import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ClientSidebarComponent } from '../client-sidebar/client-sidebar.component';
import { AuthService } from '../../services/auth.service';
import { ClientService } from '../../services/client.service';
import { SettingsService } from '../../services/settings.service';
import { TransferRequest, AccountDetails } from '../../models/client.model';
import { GlobalSettings } from '../../models/auth.model';

@Component({
  selector: 'app-client-transfer',
  imports: [CommonModule, ReactiveFormsModule, ClientSidebarComponent],
  templateUrl: './client-transfer.component.html',
  styleUrl: './client-transfer.component.css'
})
export class ClientTransferComponent implements OnInit {
  transferForm: FormGroup;
  isLoading = false;
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';
  accountDetails: AccountDetails | null = null;
  globalSettings: GlobalSettings | null = null;
  feePercentage = 2.0; // Default fee percentage

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private clientService: ClientService,
    private settingsService: SettingsService,
    private router: Router
  ) {
    this.transferForm = this.formBuilder.group({
      destinationAccountId: ['', [
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(20),
        Validators.pattern(/^[A-Za-z0-9]+$/)
      ]],
      amount: ['', [
        Validators.required,
        Validators.min(1),
        Validators.max(100000),
        this.positiveNumberValidator
      ]],
      transferFee: [0, [Validators.required, Validators.min(0)]],
      description: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(200),
        this.noSpecialCharactersValidator
      ]]
    });

    // Listen for amount changes to calculate transfer fee
    this.transferForm.get('amount')?.valueChanges.subscribe(amount => {
      this.calculateTransferFee(amount);
    });
  }

  ngOnInit(): void {
    if (!this.authService.isAuthenticated() || !this.authService.isClient()) {
      this.router.navigate(['/login']);
      return;
    }

    this.loadAccountDetails();
    this.loadGlobalSettings();
  }

  loadAccountDetails(): void {
    this.isLoading = true;
    this.clientService.getAccountDetails().subscribe({
      next: (account) => {
        this.accountDetails = account;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading account details:', error);
        this.errorMessage = 'Failed to load account details. Please try again.';
        this.isLoading = false;
      }
    });
  }

  loadGlobalSettings(): void {
    this.settingsService.getGlobalSettings().subscribe({
      next: (response) => {
        this.globalSettings = response.settings;
        this.feePercentage = response.settings.feePercentage;

        // Recalculate fee if amount is already entered
        const currentAmount = this.transferForm.get('amount')?.value;
        if (currentAmount) {
          this.calculateTransferFee(currentAmount);
        }
      },
      error: (error) => {
        console.error('Error loading global settings:', error);
        // Use default fee percentage if settings can't be loaded
        this.feePercentage = 2.0;
      }
    });
  }

  calculateTransferFee(amount: number): void {
    if (amount && amount > 0) {
      const calculatedFee = (amount * this.feePercentage) / 100;
      this.transferForm.patchValue({
        transferFee: parseFloat(calculatedFee.toFixed(2))
      }, { emitEvent: false });
    } else {
      this.transferForm.patchValue({
        transferFee: 0
      }, { emitEvent: false });
    }
  }

  onSubmit(): void {
    if (this.transferForm.valid && this.accountDetails) {
      this.isSubmitting = true;
      this.errorMessage = '';
      this.successMessage = '';

      const transferData: TransferRequest = {
        sourceAccountId: this.accountDetails.accountId,
        destinationAccountId: this.transferForm.value.destinationAccountId,
        transferFee: this.transferForm.value.transferFee,
        amount: this.transferForm.value.amount,
        description: this.transferForm.value.description
      };

      this.clientService.makeTransfer(transferData).subscribe({
        next: (response) => {
          this.isSubmitting = false;
          // Display API response message if available, otherwise use default
          this.successMessage = response.message || `Transfer initiated successfully! Transaction ID: ${response.transactionId}`;
          this.transferForm.reset();
          this.transferForm.patchValue({ transferFee: 0 });
        },
        error: (error) => {
          this.isSubmitting = false;
          console.error('Transfer failed:', error);

          // Display API error message if available, otherwise use status-based messages
          if (error.error && error.error.message) {
            this.errorMessage = error.error.message;
          } else if (error.status === 400) {
            this.errorMessage = 'Invalid transfer details. Please check your input.';
          } else if (error.status === 403) {
            this.errorMessage = 'Insufficient funds or account restrictions.';
          } else {
            this.errorMessage = 'Transfer failed. Please try again later.';
          }
        }
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/client']);
  }

  // Custom validators
  positiveNumberValidator(control: any) {
    const value = parseFloat(control.value);
    if (isNaN(value) || value <= 0) {
      return { positiveNumber: true };
    }
    return null;
  }

  noSpecialCharactersValidator(control: any) {
    const value = control.value;
    if (value && /[<>{}[\]\\\/]/.test(value)) {
      return { noSpecialCharacters: true };
    }
    return null;
  }

  // Enhanced validation methods
  isFieldInvalid(fieldName: string): boolean {
    const field = this.transferForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  getFieldError(fieldName: string): string {
    const field = this.transferForm.get(fieldName);
    if (field && field.errors && (field.dirty || field.touched)) {
      if (field.errors['required']) return `${fieldName} is required`;
      if (field.errors['minlength']) return `${fieldName} is too short`;
      if (field.errors['maxlength']) return `${fieldName} is too long`;
      if (field.errors['min']) return `${fieldName} must be greater than ${field.errors['min'].min}`;
      if (field.errors['max']) return `${fieldName} must be less than ${field.errors['max'].max}`;
      if (field.errors['pattern']) return `${fieldName} contains invalid characters`;
      if (field.errors['positiveNumber']) return `${fieldName} must be a positive number`;
      if (field.errors['noSpecialCharacters']) return `${fieldName} contains invalid special characters`;
    }
    return '';
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 2
    }).format(amount);
  }
}
