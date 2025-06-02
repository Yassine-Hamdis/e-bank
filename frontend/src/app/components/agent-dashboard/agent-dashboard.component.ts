import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AgentService } from '../../services/agent.service';
import { User, Client, DepositStatistics } from '../../models/auth.model';
import { AgentSidebarComponent } from '../agent-sidebar/agent-sidebar.component';

@Component({
  selector: 'app-agent-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, AgentSidebarComponent],
  templateUrl: './agent-dashboard.component.html',
  styleUrls: ['./agent-dashboard.component.css']
})
export class AgentDashboardComponent implements OnInit {
  user: User | null = null;
  clients: Client[] = [];
  isLoadingClients = true;
  clientsError: string | null = null;

  // Deposit statistics properties
  depositStatistics: DepositStatistics | null = null;
  isLoadingDepositStats = true;
  depositStatsError: string | null = null;

  constructor(
    private authService: AuthService,
    private agentService: AgentService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    // Check if user is agent
    if (!this.authService.isAgent()) {
      this.router.navigate(['/dashboard']);
      return;
    }

    this.user = this.authService.getCurrentUser();
    this.loadClients();
    this.loadDepositStatistics();
  }

  private loadClients(): void {
    this.isLoadingClients = true;
    this.clientsError = null;

    this.agentService.getManagedClients().subscribe({
      next: (clients) => {
        this.clients = clients;
        this.isLoadingClients = false;
      },
      error: (error) => {
        console.error('Error loading clients:', error);
        this.clientsError = 'Failed to load clients. Please try again.';
        this.isLoadingClients = false;
      }
    });
  }

  private loadDepositStatistics(): void {
    this.isLoadingDepositStats = true;
    this.depositStatsError = null;

    this.agentService.getDepositStatistics().subscribe({
      next: (stats) => {
        this.depositStatistics = stats;
        this.isLoadingDepositStats = false;
      },
      error: (error) => {
        console.error('Error loading deposit statistics:', error);
        this.depositStatsError = 'Failed to load deposit statistics. Please try again.';
        this.isLoadingDepositStats = false;
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  // Statistics calculations
  get totalClients(): number {
    return this.clients.length;
  }

  get activeClients(): number {
    return this.clients.filter(client => client.status === 'ACTIVE').length;
  }

  get inactiveClients(): number {
    return this.clients.filter(client => client.status === 'INACTIVE').length;
  }

  get clientsWithAccounts(): number {
    return this.clients.filter(client => client.viewAccount.length > 0).length;
  }

  // Utility methods for formatting
  formatAmount(amount: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
