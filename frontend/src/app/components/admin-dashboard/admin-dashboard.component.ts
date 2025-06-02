import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { forkJoin } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { AdminService } from '../../services/admin.service';
import { SettingsService } from '../../services/settings.service';
import { User, AgentStatistics, CurrencyStatistics, GlobalSettings, UpdateGlobalSettingsRequest } from '../../models/auth.model';
import { Chart, registerables } from 'chart.js';
import { AdminSidebarComponent } from '../admin-sidebar/admin-sidebar.component';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, AdminSidebarComponent],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit, AfterViewInit {
  user: User | null = null;
  agentStats: AgentStatistics | null = null;
  currencyStats: CurrencyStatistics | null = null;
  isLoadingStats = true;
  statsError: string | null = null;

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

  // Chart references
  @ViewChild('agentStatsChart', { static: false }) agentStatsChart!: ElementRef<HTMLCanvasElement>;
  @ViewChild('currencyStatsChart', { static: false }) currencyStatsChart!: ElementRef<HTMLCanvasElement>;
  @ViewChild('combinedStatsChart', { static: false }) combinedStatsChart!: ElementRef<HTMLCanvasElement>;

  private charts: Chart[] = [];

  constructor(
    private authService: AuthService,
    private adminService: AdminService,
    private settingsService: SettingsService,
    private router: Router
  ) {
    // Register Chart.js components
    Chart.register(...registerables);
  }

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

    this.user = this.authService.getCurrentUser();
    this.loadStatistics();
    this.loadGlobalSettings();
  }

  ngAfterViewInit(): void {
    // Charts will be created after statistics are loaded
    // Try to create charts if data is already available
    if (this.agentStats && this.currencyStats) {
      this.tryCreateCharts(0);
    }
  }

  private loadStatistics(): void {
    this.isLoadingStats = true;
    this.statsError = null;

    // Load both statistics in parallel
    forkJoin({
      agentStats: this.adminService.getAgentStatistics(),
      currencyStats: this.adminService.getCurrencyStatistics()
    }).subscribe({
      next: (data) => {
        this.agentStats = data.agentStats;
        this.currencyStats = data.currencyStats;
        this.isLoadingStats = false;
        // Create charts after data is loaded and view is initialized
        this.tryCreateCharts(0);
      },
      error: (error) => {
        console.error('Error loading statistics:', error);
        this.statsError = 'Failed to load statistics. Please try again.';
        this.isLoadingStats = false;
      }
    });
  }

  refreshStatistics(): void {
    this.loadStatistics();
  }

  refreshCharts(): void {
    console.log('Manual chart refresh triggered');
    this.tryCreateCharts(0);
  }

  testChart(): void {
    console.log('Testing chart creation...');
    if (this.agentStatsChart) {
      const ctx = this.agentStatsChart.nativeElement.getContext('2d');
      if (ctx) {
        console.log('Canvas context available, creating test chart');
        new Chart(ctx, {
          type: 'doughnut',
          data: {
            labels: ['Test 1', 'Test 2'],
            datasets: [{
              data: [1, 1],
              backgroundColor: ['#10B981', '#EF4444']
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false
          }
        });
        console.log('Test chart created successfully');
      } else {
        console.error('No canvas context available');
      }
    } else {
      console.error('No chart element available');
    }
  }

  private tryCreateCharts(attempt: number): void {
    const maxAttempts = 5;
    const delay = 200 * (attempt + 1); // Increasing delay

    console.log(`Attempting to create charts (attempt ${attempt + 1}/${maxAttempts})`);

    if (attempt >= maxAttempts) {
      console.error('Failed to create charts after maximum attempts');
      return;
    }

    setTimeout(() => {
      if (this.agentStats && this.currencyStats &&
          this.agentStatsChart && this.currencyStatsChart && this.combinedStatsChart) {
        console.log('All requirements met, creating charts');
        this.createCharts();
      } else {
        console.log('Requirements not met, retrying...', {
          agentStats: !!this.agentStats,
          currencyStats: !!this.currencyStats,
          agentStatsChart: !!this.agentStatsChart,
          currencyStatsChart: !!this.currencyStatsChart,
          combinedStatsChart: !!this.combinedStatsChart
        });
        this.tryCreateCharts(attempt + 1);
      }
    }, delay);
  }

  private createCharts(): void {
    if (!this.agentStats || !this.currencyStats) {
      console.log('Charts not created: missing data', { agentStats: this.agentStats, currencyStats: this.currencyStats });
      return;
    }

    // Check if ViewChild elements are available
    if (!this.agentStatsChart || !this.currencyStatsChart || !this.combinedStatsChart) {
      console.log('Charts not created: ViewChild elements not ready', {
        agentStatsChart: !!this.agentStatsChart,
        currencyStatsChart: !!this.currencyStatsChart,
        combinedStatsChart: !!this.combinedStatsChart
      });
      return;
    }

    console.log('Creating charts with data:', { agentStats: this.agentStats, currencyStats: this.currencyStats });

    // Destroy existing charts
    this.charts.forEach(chart => chart.destroy());
    this.charts = [];

    this.createAgentStatsChart();
    this.createCurrencyStatsChart();
    this.createCombinedStatsChart();
  }

  private createAgentStatsChart(): void {
    if (!this.agentStats || !this.agentStatsChart) {
      console.log('Agent chart not created: missing requirements', { agentStats: !!this.agentStats, agentStatsChart: !!this.agentStatsChart });
      return;
    }

    const ctx = this.agentStatsChart.nativeElement.getContext('2d');
    if (!ctx) {
      console.log('Agent chart not created: no canvas context');
      return;
    }

    console.log('Creating agent stats chart with data:', this.agentStats);

    // Handle edge case where one value is 0
    const activeAgents = this.agentStats.activeAgents || 0;
    const inactiveAgents = this.agentStats.inactiveAgents || 0;

    // If both are 0, show a placeholder
    const chartData = activeAgents === 0 && inactiveAgents === 0
      ? [1]
      : [activeAgents, inactiveAgents];

    const chartLabels = activeAgents === 0 && inactiveAgents === 0
      ? ['No Data']
      : ['Active Agents', 'Inactive Agents'];

    const chartColors = activeAgents === 0 && inactiveAgents === 0
      ? ['#E5E7EB']
      : ['#10B981', '#EF4444'];

    const chart = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: chartLabels,
        datasets: [{
          data: chartData,
          backgroundColor: chartColors,
          borderWidth: 2,
          borderColor: '#ffffff'
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'bottom',
            labels: {
              padding: 20,
              usePointStyle: true
            }
          },
          title: {
            display: true,
            text: 'Agent Status Distribution',
            font: {
              size: 16,
              weight: 'bold'
            }
          }
        }
      }
    });

    this.charts.push(chart);
  }

  private createCurrencyStatsChart(): void {
    if (!this.currencyStats || !this.currencyStatsChart) {
      console.log('Currency chart not created: missing requirements', { currencyStats: !!this.currencyStats, currencyStatsChart: !!this.currencyStatsChart });
      return;
    }

    const ctx = this.currencyStatsChart.nativeElement.getContext('2d');
    if (!ctx) {
      console.log('Currency chart not created: no canvas context');
      return;
    }

    console.log('Creating currency stats chart with data:', this.currencyStats);

    const chart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: ['Active', 'Inactive', 'Manual', 'Binance'],
        datasets: [{
          label: 'Currencies',
          data: [
            this.currencyStats.activeCurrencies,
            this.currencyStats.inactiveCurrencies,
            this.currencyStats.manualCurrencies,
            this.currencyStats.binanceCurrencies
          ],
          backgroundColor: [
            '#10B981', // Green for active
            '#EF4444', // Red for inactive
            '#8B5CF6', // Purple for manual
            '#F59E0B'  // Yellow for binance
          ],
          borderColor: [
            '#059669',
            '#DC2626',
            '#7C3AED',
            '#D97706'
          ],
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: false
          },
          title: {
            display: true,
            text: 'Currency Statistics',
            font: {
              size: 16,
              weight: 'bold'
            }
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              stepSize: 1
            }
          }
        }
      }
    });

    this.charts.push(chart);
  }

  private createCombinedStatsChart(): void {
    if (!this.agentStats || !this.currencyStats || !this.combinedStatsChart) {
      console.log('Combined chart not created: missing requirements', {
        agentStats: !!this.agentStats,
        currencyStats: !!this.currencyStats,
        combinedStatsChart: !!this.combinedStatsChart
      });
      return;
    }

    const ctx = this.combinedStatsChart.nativeElement.getContext('2d');
    if (!ctx) {
      console.log('Combined chart not created: no canvas context');
      return;
    }

    console.log('Creating combined stats chart with data:', { agentStats: this.agentStats, currencyStats: this.currencyStats });

    const chart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: ['Total Agents', 'Active Agents', 'Total Currencies', 'Active Currencies', 'Managed Clients'],
        datasets: [{
          label: 'System Overview',
          data: [
            this.agentStats.totalAgents,
            this.agentStats.activeAgents,
            this.currencyStats.totalCurrencies,
            this.currencyStats.activeCurrencies,
            this.agentStats.totalManagedClients
          ],
          borderColor: '#3B82F6',
          backgroundColor: 'rgba(59, 130, 246, 0.1)',
          borderWidth: 3,
          fill: true,
          tension: 0.4,
          pointBackgroundColor: '#3B82F6',
          pointBorderColor: '#ffffff',
          pointBorderWidth: 2,
          pointRadius: 6
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: false
          },
          title: {
            display: true,
            text: 'System Overview',
            font: {
              size: 16,
              weight: 'bold'
            }
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              stepSize: 1
            }
          }
        }
      }
    });

    this.charts.push(chart);
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

  refreshGlobalSettings(): void {
    this.loadGlobalSettings();
  }

  editSettings(): void {
    this.isEditingSettings = true;
  }

  cancelEditSettings(): void {
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

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
