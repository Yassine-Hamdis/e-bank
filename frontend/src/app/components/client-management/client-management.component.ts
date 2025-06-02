import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AgentService } from '../../services/agent.service';
import { User, Client, CreateClientRequest, DepositRequest } from '../../models/auth.model';
import { AgentSidebarComponent } from '../agent-sidebar/agent-sidebar.component';

@Component({
  selector: 'app-client-management',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, AgentSidebarComponent],
  templateUrl: './client-management.component.html',
  styleUrls: ['./client-management.component.css']
})
export class ClientManagementComponent implements OnInit {
  user: User | null = null;
  clients: Client[] = [];
  isLoading = true;
  error: string | null = null;

  // Create Client Modal
  showCreateModal = false;
  createClientForm: FormGroup;
  isCreating = false;
  createError: string | null = null;

  // Deposit Modal
  showDepositModal = false;
  depositForm: FormGroup;
  selectedClient: Client | null = null;
  isDepositing = false;
  depositError: string | null = null;
  depositSuccess: string | null = null;

  // Delete confirmation
  showDeleteModal = false;
  clientToDelete: Client | null = null;
  isDeleting = false;
  deleteError: string | null = null;

  constructor(
    private authService: AuthService,
    private agentService: AgentService,
    private router: Router,
    private formBuilder: FormBuilder
  ) {
    this.createClientForm = this.formBuilder.group({
      address: ['', [Validators.required, Validators.minLength(10)]],
      username: ['', [Validators.required, Validators.minLength(3), Validators.pattern(/^[a-zA-Z0-9_]+$/)]],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: ['', [Validators.required, Validators.pattern(/^\+?[1-9]\d{1,14}$/)]],
      nationalId: ['', [Validators.required, Validators.minLength(5)]]
    });

    this.depositForm = this.formBuilder.group({
      accountId: ['', [Validators.required]],
      amount: ['', [Validators.required, Validators.min(0.01)]],
      description: ['', [Validators.required, Validators.minLength(5)]]
    });
  }

  ngOnInit(): void {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    if (!this.authService.isAgent()) {
      this.router.navigate(['/dashboard']);
      return;
    }

    this.user = this.authService.getCurrentUser();
    this.loadClients();
  }

  loadClients(): void {
    this.isLoading = true;
    this.error = null;

    this.agentService.getManagedClients().subscribe({
      next: (clients) => {
        this.clients = clients;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading clients:', error);
        this.error = 'Failed to load clients. Please try again.';
        this.isLoading = false;
      }
    });
  }

  // Create Client Methods
  openCreateModal(): void {
    this.showCreateModal = true;
    this.createClientForm.reset();
    this.createError = null;
  }

  closeCreateModal(): void {
    this.showCreateModal = false;
    this.createClientForm.reset();
    this.createError = null;
  }

  createClient(): void {
    if (this.createClientForm.valid) {
      this.isCreating = true;
      this.createError = null;

      const clientData: CreateClientRequest = {
        address: this.createClientForm.value.address,
        username: this.createClientForm.value.username,
        email: this.createClientForm.value.email,
        phoneNumber: this.createClientForm.value.phoneNumber,
        nationalId: this.createClientForm.value.nationalId
      };

      // Debug: Log the data being sent
      console.log('Creating client with data:', clientData);

      this.agentService.createClient(clientData).subscribe({
        next: (response) => {
          this.isCreating = false;
          this.closeCreateModal();
          this.loadClients(); // Refresh the list
          console.log('Client created successfully:', response);
        },
        error: (error) => {
          this.isCreating = false;
          console.error('Error creating client:', error);
          console.error('Error details:', error.error);

          if (error.status === 400) {
            // Try to get more specific error message from backend
            const errorMessage = error.error?.message || error.error?.error || 'Invalid client data. Please check your inputs.';
            this.createError = errorMessage;
          } else if (error.status === 409) {
            this.createError = 'Client ID already exists. Please use a different ID.';
          } else if (error.status === 401) {
            this.createError = 'Authentication failed. Please login again.';
          } else {
            this.createError = 'Failed to create client. Please try again.';
          }
        }
      });
    } else {
      this.markFormGroupTouched(this.createClientForm);
    }
  }

  // Deposit Methods
  openDepositModal(client: Client): void {
    this.selectedClient = client;
    this.showDepositModal = true;
    this.depositForm.reset();
    this.depositError = null;
    this.depositSuccess = null;

    // Pre-fill account ID if available
    if (client.viewAccount.length > 0) {
      this.depositForm.patchValue({
        accountId: client.viewAccount[0]
      });
    }
  }

  closeDepositModal(): void {
    this.showDepositModal = false;
    this.selectedClient = null;
    this.depositForm.reset();
    this.depositError = null;
    this.depositSuccess = null;
  }

  makeDeposit(): void {
    if (this.depositForm.valid && this.selectedClient) {
      this.isDepositing = true;
      this.depositError = null;
      this.depositSuccess = null;

      const depositData: DepositRequest = {
        clientId: this.selectedClient.clientId,
        accountId: this.depositForm.value.accountId,
        amount: parseFloat(this.depositForm.value.amount),
        description: this.depositForm.value.description
      };

      this.agentService.makeDeposit(depositData).subscribe({
        next: (response) => {
          this.isDepositing = false;
          console.log('Deposit successful:', response);
          // Display API response message if available, otherwise use default
          this.depositSuccess = response.message || `Deposit successful! Transaction ID: ${response.transactionId}`;

          // Auto-close modal after 3 seconds
          setTimeout(() => {
            this.closeDepositModal();
          }, 3000);
        },
        error: (error) => {
          this.isDepositing = false;
          console.error('Error making deposit:', error);

          // Display API error message if available, otherwise use status-based messages
          if (error.error && error.error.message) {
            this.depositError = error.error.message;
          } else if (error.status === 400) {
            this.depositError = 'Invalid deposit data. Please check your inputs.';
          } else if (error.status === 404) {
            this.depositError = 'Client or account not found.';
          } else {
            this.depositError = 'Failed to make deposit. Please try again.';
          }
        }
      });
    } else {
      this.markFormGroupTouched(this.depositForm);
    }
  }

  // Delete Methods
  openDeleteModal(client: Client): void {
    this.clientToDelete = client;
    this.showDeleteModal = true;
    this.deleteError = null;
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.clientToDelete = null;
    this.deleteError = null;
  }

  deleteClient(): void {
    if (this.clientToDelete) {
      this.isDeleting = true;
      this.deleteError = null;

      this.agentService.deleteClient(this.clientToDelete.clientId).subscribe({
        next: () => {
          this.isDeleting = false;
          this.closeDeleteModal();
          this.loadClients(); // Refresh the list
        },
        error: (error) => {
          this.isDeleting = false;
          console.error('Error deleting client:', error);

          if (error.status === 404) {
            this.deleteError = 'Client not found.';
          } else if (error.status === 409) {
            this.deleteError = 'Cannot delete client with active accounts.';
          } else {
            this.deleteError = 'Failed to delete client. Please try again.';
          }
        }
      });
    }
  }

  // Utility Methods
  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }



  navigateToAgent(): void {
    this.router.navigate(['/agent']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString();
  }

  getStatusBadgeClass(status: string): string {
    return status === 'ACTIVE'
      ? 'bg-green-100 text-green-800'
      : 'bg-red-100 text-red-800';
  }

}
