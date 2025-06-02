import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { SettingsService } from '../../services/settings.service';
import { GlobalSettings, UpdateGlobalSettingsRequest } from '../../models/auth.model';
import { AdminSidebarComponent } from '../admin-sidebar/admin-sidebar.component';

@Component({
  selector: 'app-admin-system-settings',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, AdminSidebarComponent],
  templateUrl: './admin-system-settings.component.html',
  styleUrls: ['./admin-system-settings.component.css']
})
export class AdminSystemSettingsComponent implements OnInit {
  // Global Settings properties
  globalSettings: GlobalSettings | null = null;
  isLoadingSettings = true;
  settingsError: string | null = null;
  isEditingSettings = false;
  isSavingSettings = false;
  settingsForm: UpdateGlobalSettingsRequest = {
    maxClientAccountBalance: 0,
    maxDailyNewClients: 0,
    feePercentage: 0
  };

  constructor(
    private authService: AuthService,
    private settingsService: SettingsService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    // Check if user is admin
    if (!this.authService.isAdmin()) {
      this.router.navigate(['/dashboard']);
      return;
    }

    this.loadGlobalSettings();
  }

  private loadGlobalSettings(): void {
    this.isLoadingSettings = true;
    this.settingsError = null;

    this.settingsService.getGlobalSettings().subscribe({
      next: (response) => {
        this.globalSettings = response.settings;
        this.settingsForm = {
          maxClientAccountBalance: response.settings.maxClientAccountBalance,
          maxDailyNewClients: response.settings.maxDailyNewClients,
          feePercentage: response.settings.feePercentage
        };
        this.isLoadingSettings = false;
      },
      error: (error) => {
        console.error('Error loading global settings:', error);
        this.settingsError = 'Failed to load global settings. Please try again.';
        this.isLoadingSettings = false;
      }
    });
  }

  editSettings(): void {
    this.isEditingSettings = true;
  }

  cancelEdit(): void {
    this.isEditingSettings = false;
    // Reset form to original values
    if (this.globalSettings) {
      this.settingsForm = {
        maxClientAccountBalance: this.globalSettings.maxClientAccountBalance,
        maxDailyNewClients: this.globalSettings.maxDailyNewClients,
        feePercentage: this.globalSettings.feePercentage
      };
    }
  }

  saveSettings(): void {
    this.isSavingSettings = true;
    this.settingsError = null;

    this.settingsService.updateGlobalSettings(this.settingsForm).subscribe({
      next: (response) => {
        this.globalSettings = response.settings;
        this.isEditingSettings = false;
        this.isSavingSettings = false;
        console.log('Settings updated successfully');
      },
      error: (error) => {
        console.error('Error updating global settings:', error);
        this.settingsError = 'Failed to update settings. Please try again.';
        this.isSavingSettings = false;
      }
    });
  }
}
