import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ClientSidebarComponent } from '../client-sidebar/client-sidebar.component';
import { AuthService } from '../../services/auth.service';
import { ClientService } from '../../services/client.service';
import { MobileRechargeRequest, MobileOperator } from '../../models/client.model';

@Component({
  selector: 'app-mobile-recharge',
  imports: [CommonModule, ReactiveFormsModule, ClientSidebarComponent],
  templateUrl: './mobile-recharge.component.html',
  styleUrl: './mobile-recharge.component.css'
})
export class MobileRechargeComponent implements OnInit {
  rechargeForm: FormGroup;
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';

  mobileOperators: MobileOperator[] = [
    { name: 'Orange', code: 'Orange', supportedTypes: ['PREPAID', 'POSTPAID'] },
    { name: 'Maroc Telecom', code: 'Maroc_Telecom', supportedTypes: ['PREPAID', 'POSTPAID'] },
    { name: 'Inwi', code: 'Inwi', supportedTypes: ['PREPAID', 'POSTPAID'] }
  ];

  rechargeAmounts = [10, 20, 50, 100, 200, 500];

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private clientService: ClientService,
    private router: Router
  ) {
    this.rechargeForm = this.formBuilder.group({
      phoneNumber: ['', [
        Validators.required,
        Validators.pattern(/^[0-9]{10}$/),
        this.phoneNumberValidator
      ]],
      operator: ['', [Validators.required]],
      rechargeType: ['PREPAID', [Validators.required]],
      amount: ['', [
        Validators.required,
        Validators.min(5),
        Validators.max(1000),
        this.positiveNumberValidator
      ]],
      description: ['Mobile recharge', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(200),
        this.noSpecialCharactersValidator
      ]]
    });
  }

  ngOnInit(): void {
    if (!this.authService.isAuthenticated() || !this.authService.isClient()) {
      this.router.navigate(['/login']);
      return;
    }
  }

  selectAmount(amount: number): void {
    this.rechargeForm.patchValue({ amount: amount });
  }

  onSubmit(): void {
    if (this.rechargeForm.valid) {
      this.isSubmitting = true;
      this.errorMessage = '';
      this.successMessage = '';

      const rechargeData: MobileRechargeRequest = {
        phoneNumber: this.rechargeForm.value.phoneNumber,
        operator: this.rechargeForm.value.operator,
        rechargeType: this.rechargeForm.value.rechargeType,
        amount: this.rechargeForm.value.amount,
        description: this.rechargeForm.value.description
      };

      this.clientService.mobileRecharge(rechargeData).subscribe({
        next: (response) => {
          this.isSubmitting = false;
          // Display API response message if available, otherwise use default
          this.successMessage = response.message || `Mobile recharge successful! Transaction ID: ${response.transactionId}`;
          this.rechargeForm.reset();
          this.rechargeForm.patchValue({
            rechargeType: 'PREPAID',
            description: 'Mobile recharge'
          });
        },
        error: (error) => {
          this.isSubmitting = false;
          console.error('Mobile recharge failed:', error);

          // Display API error message if available, otherwise use status-based messages
          if (error.error && error.error.message) {
            this.errorMessage = error.error.message;
          } else if (error.status === 400) {
            this.errorMessage = 'Invalid recharge details. Please check your input.';
          } else if (error.status === 403) {
            this.errorMessage = 'Insufficient funds or account restrictions.';
          } else {
            this.errorMessage = 'Mobile recharge failed. Please try again later.';
          }
        }
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/client']);
  }

  // Custom validators
  phoneNumberValidator(control: any) {
    const value = control.value;
    if (value && !/^[0-9]{10}$/.test(value)) {
      return { phoneNumber: true };
    }
    return null;
  }

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
    const field = this.rechargeForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  getFieldError(fieldName: string): string {
    const field = this.rechargeForm.get(fieldName);
    if (field && field.errors && (field.dirty || field.touched)) {
      if (field.errors['required']) return `${fieldName} is required`;
      if (field.errors['minlength']) return `${fieldName} is too short`;
      if (field.errors['maxlength']) return `${fieldName} is too long`;
      if (field.errors['min']) return `${fieldName} must be at least ${field.errors['min'].min}`;
      if (field.errors['max']) return `${fieldName} must be less than ${field.errors['max'].max}`;
      if (field.errors['pattern']) return `${fieldName} format is invalid`;
      if (field.errors['phoneNumber']) return `${fieldName} must be exactly 10 digits`;
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

  getSelectedOperator(): MobileOperator | undefined {
    return this.mobileOperators.find(op => op.code === this.rechargeForm.value.operator);
  }
}
