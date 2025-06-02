import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { ChangePasswordRequest } from '../../models/auth.model';

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent implements OnInit {
  passwordForm: ChangePasswordRequest = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  };

  isSubmitting = false;
  errorMessage = '';
  successMessage = '';
  showCurrentPassword = false;
  showNewPassword = false;
  showConfirmPassword = false;
  userRole = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Determine user role for navigation
    if (this.authService.isAdmin()) {
      this.userRole = 'admin';
    } else if (this.authService.isAgent()) {
      this.userRole = 'agent';
    } else if (this.authService.isClient()) {
      this.userRole = 'client';
    }
  }

  togglePasswordVisibility(field: string): void {
    switch (field) {
      case 'current':
        this.showCurrentPassword = !this.showCurrentPassword;
        break;
      case 'new':
        this.showNewPassword = !this.showNewPassword;
        break;
      case 'confirm':
        this.showConfirmPassword = !this.showConfirmPassword;
        break;
    }
  }

  validateForm(): boolean {
    this.errorMessage = '';

    if (!this.passwordForm.currentPassword) {
      this.errorMessage = 'Current password is required';
      return false;
    }

    if (!this.passwordForm.newPassword) {
      this.errorMessage = 'New password is required';
      return false;
    }

    if (this.passwordForm.newPassword.length < 6) {
      this.errorMessage = 'New password must be at least 6 characters long';
      return false;
    }

    if (!this.passwordForm.confirmPassword) {
      this.errorMessage = 'Please confirm your new password';
      return false;
    }

    if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
      this.errorMessage = 'New password and confirmation do not match';
      return false;
    }

    if (this.passwordForm.currentPassword === this.passwordForm.newPassword) {
      this.errorMessage = 'New password must be different from current password';
      return false;
    }

    return true;
  }

  onSubmit(): void {
    if (!this.validateForm()) {
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.authService.changePassword(this.passwordForm).subscribe({
      next: (response) => {
        this.isSubmitting = false;
        this.successMessage = response.message || 'Password changed successfully!';
        
        // Clear the form
        this.passwordForm = {
          currentPassword: '',
          newPassword: '',
          confirmPassword: ''
        };

        // Redirect to dashboard after 2 seconds
        setTimeout(() => {
          this.goBack();
        }, 2000);
      },
      error: (error) => {
        this.isSubmitting = false;
        if (error.error && error.error.message) {
          this.errorMessage = error.error.message;
        } else if (error.message) {
          this.errorMessage = error.message;
        } else {
          this.errorMessage = 'An error occurred while changing password. Please try again.';
        }
      }
    });
  }

  goBack(): void {
    switch (this.userRole) {
      case 'admin':
        this.router.navigate(['/admin']);
        break;
      case 'agent':
        this.router.navigate(['/agent']);
        break;
      case 'client':
        this.router.navigate(['/client']);
        break;
      default:
        this.router.navigate(['/dashboard']);
        break;
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
